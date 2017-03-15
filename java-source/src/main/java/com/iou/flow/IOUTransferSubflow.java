package com.iou.flow;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableSet;
import com.iou.state.IOUState;
import net.corda.core.contracts.TransactionResolutionException;
import net.corda.core.crypto.CompositeKey;
import net.corda.core.crypto.DigitalSignature;
import net.corda.core.crypto.Party;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.transactions.SignedTransaction;
import net.corda.flows.ResolveTransactionsFlow;

import java.io.FileNotFoundException;
import java.security.SignatureException;
import java.util.Set;

import static kotlin.collections.CollectionsKt.single;

public class IOUTransferSubflow {
    public static class Initiator extends FlowLogic<DigitalSignature.WithKey> {
        private final SignedTransaction stx;
        private final Party otherParty;

        public Initiator(SignedTransaction stx, Party otherParty) {
            this.stx = stx;
            this.otherParty = otherParty;
        }

        @Suspendable
        public DigitalSignature.WithKey call() throws FlowException {
            // Stage 1 - Retrieving a signature over the transaction from the counterparty.
            return this.sendAndReceive(DigitalSignature.WithKey.class, otherParty, stx).unwrap(tx -> tx);
        }
    }

    public static class Acceptor extends FlowLogic<Void> {
        private final Party otherParty;

        public Acceptor(Party otherParty) {
            this.otherParty = otherParty;
        }

        @Suspendable
        @Override
        public Void call() throws FlowException {
            final SignedTransaction partSignedTx = this.receive(SignedTransaction.class, otherParty)
                    .unwrap(tx -> {
                        // Stage 2 - Verifying the signatures
                        final IOUState txOutputState = (IOUState) tx.getTx().getOutputs().get(0).getData();
                        final CompositeKey notaryKey = single(getServiceHub().getNetworkMapCache().getNotaryNodes()).getNotaryIdentity().getOwningKey();
                        try {
                            tx.verifySignatures(
                                    txOutputState.getSender().getOwningKey(),
                                    txOutputState.getRecipient().getOwningKey(),
                                    notaryKey);
                        } catch (SignatureException ex) {
                            throw new RuntimeException("Signatures are invalid or unrecognized.", ex);
                        }

                        return tx;
                    });

            // Stage 3 - Retrieving the transaction's dependencies.
            // We cannot call subflows within the unwrap() block.
            Set<SecureHash> dependencyTxIDs = ImmutableSet.of(partSignedTx.getTx().getInputs().get(0).getTxhash());
            subFlow(new ResolveTransactionsFlow(dependencyTxIDs, otherParty));

            // Stage 4 - Verifying the transaction
            try {
                partSignedTx.toLedgerTransaction(getServiceHub()).verify();
            } catch (SignatureException ex) {
                throw new RuntimeException("Signatures are invalid or unrecognized.", ex);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException("Required attachment was not found in storage.", ex);
            } catch (TransactionResolutionException ex) {
                throw new RuntimeException("An input points to a transaction not found in storage.", ex);
            }

            // Stage 5 - Sending back a signature over the transaction.
            this.send(otherParty, partSignedTx.signWithECDSA(getServiceHub().getLegalIdentityKey()));

            return null;
        }
    }
}
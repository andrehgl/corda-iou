package com.iou.flow;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.iou.state.IOUState;
import kotlin.collections.CollectionsKt;
import net.corda.core.contracts.StateRef;
import net.corda.core.contracts.TransactionResolutionException;
import net.corda.core.crypto.*;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.WireTransaction;
import net.corda.flows.ResolveTransactionsFlow;

import java.io.FileNotFoundException;
import java.security.KeyPair;
import java.security.SignatureException;
import java.util.Collections;
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
            DigitalSignature.WithKey sig = this.sendAndReceive(DigitalSignature.WithKey.class, otherParty, stx).unwrap(tx -> tx);
            return sig;
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
            // Obtain a reference to our key pair.
            final KeyPair keyPair = getServiceHub().getLegalIdentityKey();
            // Obtain a reference to the notary we want to use and its public key.
            final CompositeKey notaryKey = single(getServiceHub().getNetworkMapCache().getNotaryNodes()).getNotaryIdentity().getOwningKey();

            final SignedTransaction partSignedTx = this.receive(SignedTransaction.class, otherParty)
                    .unwrap(tx ->
                    {
                        // Obtain a reference to the participants who don't need to have signed yet.
                        final IOUState outputState = (IOUState) tx.getTx().getOutputs().get(0).getData();
                        final CompositeKey senderKey = outputState.getSender().getOwningKey();
                        final CompositeKey newRecipientKey = outputState.getRecipient().getOwningKey();

                        // Stage 6 - Checking the transaction
                        try {
                            // Check that the signature of the other party is valid.
                            // Our signature and the notary's signature are allowed to be omitted at this stage as
                            // this is only a partially signed transaction.
                            final WireTransaction wireTx = tx.verifySignatures(
                                    senderKey,
                                    newRecipientKey,
                                    notaryKey);

                            Set<SecureHash> dependencyTxIDs = ImmutableSet.of(tx.getTx().getInputs().get(0).getTxhash());
                            subFlow(new ResolveTransactionsFlow(dependencyTxIDs, otherParty));

                            // Run the contract's verify function.
                            // We want to be sure that the agreed-upon IOU is valid under the rules of the contract.
                            // To do this we need to run the contract's verify() function.
                            wireTx.toLedgerTransaction(getServiceHub()).verify();
                        } catch (SignatureException ex) {
                            throw new RuntimeException(tx.getId() + " failed signature checks", ex);
                        } catch (FileNotFoundException ex) {
                            throw new RuntimeException(tx.getId() + " file not found", ex);
                        } catch (TransactionResolutionException ex) {
                            throw new RuntimeException(tx.getId() + " transaction resolution exception", ex);
                        } catch (FlowException ex) {
                            throw new RuntimeException(tx.getId() + " another transaction resolution exception", ex);
                        }

                        return tx;
                    });

            System.out.println("joel");
            // Stage 7 - Signing the transaction
            // Sign the transaction with our key pair and add it to the transaction.
            // We now have 'validation consensus', but we still need uniqueness consensus (i.e. notarisation).
            final DigitalSignature.WithKey mySig = partSignedTx.signWithECDSA(keyPair);

            this.send(otherParty, mySig);

            return null;
        }
    }
}
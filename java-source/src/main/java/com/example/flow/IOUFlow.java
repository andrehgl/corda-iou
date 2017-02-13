package com.example.flow;

import co.paralleluniverse.fibers.Suspendable;
import com.example.contract.IOUContract;
import com.example.state.IOUState;
import com.google.common.collect.ImmutableSet;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.TransactionType;
import net.corda.core.crypto.CompositeKey;
import net.corda.core.crypto.CryptoUtilities;
import net.corda.core.crypto.DigitalSignature;
import net.corda.core.crypto.Party;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.transactions.WireTransaction;
import net.corda.flows.FinalityFlow;

import java.security.KeyPair;
import java.security.SignatureException;
import java.util.Set;

import static kotlin.collections.CollectionsKt.single;

/**
 * This flow allows two parties (the [Initiator] and the [Acceptor]) to come to an agreement about the IOU encapsulated
 * within an [IOUState].
 * <p>
 * In our simple example, the [Acceptor] always accepts a valid IOU.
 * <p>
 * These flows have deliberately been implemented by using only the call() method for ease of understanding. In
 * practice we would recommend splitting up the various stages of the flow into sub-routines.
 * <p>
 * All methods called within the [FlowLogic] sub-class need to be annotated with the @Suspendable annotation.
 */
public class IOUFlow {
    public static class Initiator extends FlowLogic<SignedTransaction> {

        private final IOUState iou;
        private final Party otherParty;

        public Initiator(IOUState iou, Party otherParty) {
            this.iou = iou;
            this.otherParty = otherParty;
        }

        /**
         * The flow logic is encapsulated within the call() method.
         */
        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            // Prep.
            // Obtain a reference to our key pair. Currently, the only key pair used is the one which is registered with
            // the NetWorkMapService. In a future milestone release we'll implement HD key generation such that new keys
            // can be generated for each transaction.
            final KeyPair keyPair = getServiceHub().getLegalIdentityKey();
            // Obtain a reference to the notary we want to use.
            final Party notary = single(getServiceHub().getNetworkMapCache().getNotaryNodes()).getNotaryIdentity();

            // Stage 1 - Generating an unsigned transaction
            final Command txCommand = new Command(new IOUContract.Commands.Create(), iou.getParticipants());
            final TransactionBuilder unsignedTx = new TransactionType.General.Builder(notary).withItems(iou, txCommand);

            // Stage 2 - Verifying that the transaction is valid
            unsignedTx.toWireTransaction().toLedgerTransaction(getServiceHub()).verify();

            // Stage 3 - Signing the transaction
            final SignedTransaction partSignedTx = unsignedTx.signWith(keyPair).toSignedTransaction(false);
    
            // Stage 4 - Sending the transaction to the counterparty
            // -----------------------
            // Flow jumps to Acceptor.
            // -----------------------
            this.send(otherParty, partSignedTx);

            return waitForLedgerCommit(partSignedTx.getId());
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
            // Prep.
            // Obtain a reference to our key pair.
            final KeyPair keyPair = getServiceHub().getLegalIdentityKey();
            final Party notary = single(getServiceHub().getNetworkMapCache().getNotaryNodes()).getNotaryIdentity();
            // Obtain a reference to the notary we want to use and its public key.
            final CompositeKey notaryPubKey = notary.getOwningKey();

            // Stage 5 - Receiving the transaction
            // All messages come off the wire as UntrustworthyData. You need to 'unwrap' them. This is where you
            // validate what you have just received.

            final SignedTransaction partSignedTx = receive(SignedTransaction.class, otherParty)
                    .unwrap(tx ->
                    {
                        // Stage 6 - Checking the transaction
                        try {
                            // Check that the signature of the other party is valid.
                            // Our signature and the notary's signature are allowed to be omitted at this stage as
                            // this is only a partially signed transaction.
                            final WireTransaction wireTx = tx.verifySignatures(CryptoUtilities.getComposite(keyPair.getPublic()), notaryPubKey);

                            // Run the contract's verify function.
                            // We want to be sure that the agreed-upon IOU is valid under the rules of the contract.
                            // To do this we need to run the contract's verify() function.
                            wireTx.toLedgerTransaction(getServiceHub()).verify();
                        } catch (SignatureException ex) {
                            throw new FlowException(tx.getId() + " failed signature checks", ex);
                        }
                        return tx;
                    });

            // Stage 7 - Signing the transaction
            // Sign the transaction with our key pair and add it to the transaction.
            // We now have 'validation consensus', but we still need uniqueness consensus (i.e. notarisation).
            final DigitalSignature.WithKey mySig = partSignedTx.signWithECDSA(keyPair);
            // Add our signature to the transaction.
            final SignedTransaction signedTx = partSignedTx.plus(mySig);

            // Stage 8 - Finalizing the transaction
            final Set<Party> participants = ImmutableSet.of(getServiceHub().getMyInfo().getLegalIdentity(), otherParty);
            // FinalityFlow() notarises the transaction and records it in each party's vault.
            subFlow(new FinalityFlow(signedTx, participants));

            return null;
        }
    }
}
package com.iou.flow;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.iou.contract.IOUContract;
import com.iou.state.IOUState;
import net.corda.core.contracts.*;
import net.corda.core.crypto.CompositeKey;
import net.corda.core.crypto.DigitalSignature;
import net.corda.core.crypto.Party;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.flows.FinalityFlow;

import java.io.FileNotFoundException;
import java.security.KeyPair;
import java.util.List;

import static kotlin.collections.CollectionsKt.single;

public class IOUTransferFlow {
    public static class Initiator extends FlowLogic<SignedTransaction> {
        private final StateRef stateRef;
        private final Party newRecipient;

        public Initiator(StateRef stateRef, Party newRecipient) {
            this.stateRef = stateRef;
            this.newRecipient = newRecipient;
        }

        @Suspendable
        public SignedTransaction call() throws FlowException {
            final Party notary = single(getServiceHub().getNetworkMapCache().getNotaryNodes()).getNotaryIdentity();

            // Stage x - Retrieving the existing IOU from the vault.
            final TransactionState inputState = getServiceHub().getVaultService().statesForRefs(ImmutableList.of(stateRef)).get(stateRef);
            final StateAndRef inputStateAndRef = new StateAndRef(inputState, stateRef);
            final IOUState inputStateData = (IOUState) inputState.getData();

            // Stage x - This flow-logic can only be initiated by the current recipient.
            if (!getServiceHub().getMyInfo().getLegalIdentity().getName().equals(inputStateData.getRecipient().getName())) {
                throw new IllegalArgumentException("IOU transfer can only be initiated by the IOU's current recipient");
            }

            // Stage x - Extracting the relevant information.
            final Party sender = inputStateData.getSender();
            final List<CompositeKey> participants = inputState.getData().getParticipants();
            participants.add(newRecipient.getOwningKey());

            // Stage x - Creating the output state.
            final IOUState outputState = new IOUState(
                    inputStateData.getIOUValue(),
                    sender,
                    newRecipient,
                    new IOUContract()
            );

            // Stage x - Generating an unsigned transaction.
            final Command txCommand = new Command(new IOUContract.Transfer(), participants);
            final TransactionBuilder unsignedTx = new TransactionType.General.Builder(notary).withItems(inputStateAndRef, outputState, txCommand);

            // Stage x - Verifying the transaction.
            try {
                unsignedTx.toWireTransaction().toLedgerTransaction(getServiceHub()).verify();
            } catch (FileNotFoundException ex) {
                throw new FlowException(
                        unsignedTx.toSignedTransaction(false).getId() + " file not found", ex);
            } catch (TransactionResolutionException ex) {
                throw new FlowException(
                        unsignedTx.toSignedTransaction(false).getId() + " transaction resolution exception", ex);
            }

            // Stage x - Signing the transaction.
            final KeyPair keyPair = getServiceHub().getLegalIdentityKey();
            final SignedTransaction partSignedTx = unsignedTx.signWith(keyPair).toSignedTransaction(false);

            // Stage x - Collecting the counterparty signatures.
            final DigitalSignature.WithKey extraSig1 = subFlow(new IOUTransferSubflow.Initiator(partSignedTx, sender));
            final DigitalSignature.WithKey extraSig2 = subFlow(new IOUTransferSubflow.Initiator(partSignedTx, newRecipient));
            final SignedTransaction fullySignedTx = partSignedTx.plus(extraSig1).plus(extraSig2);

            // Stage x - Calling FinalityFlow.
            subFlow(new FinalityFlow(fullySignedTx,
                    ImmutableSet.of(sender, inputStateData.getRecipient(), newRecipient)));

            return fullySignedTx;
        }
    }
}
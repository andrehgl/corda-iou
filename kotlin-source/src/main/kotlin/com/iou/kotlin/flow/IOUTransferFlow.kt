package com.iou.kotlin.flow

import co.paralleluniverse.fibers.Suspendable
import com.iou.kotlin.contract.IOUContract
import com.iou.kotlin.state.IOUState
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.StateRef
import net.corda.core.contracts.TransactionType
import net.corda.core.crypto.Party
import net.corda.core.flows.FlowLogic
import net.corda.core.transactions.SignedTransaction
import net.corda.flows.FinalityFlow

object IOUTransferFlow {
    class Initiator(val stateRef: StateRef, val newRecipient: Party) : FlowLogic<SignedTransaction>() {

        @Suspendable
        override fun call(): SignedTransaction {
            // Stage 1 - Retrieving the existing IOU from the vault.
            val inputState = serviceHub.vaultService.statesForRefs(listOf(stateRef))[stateRef]!!
            val inputStateData = inputState.data as IOUState

            // Stage 2 - This flow-logic can only be initiated by the current recipient.
            if (serviceHub.myInfo.legalIdentity.name != inputStateData.recipient.name) {
                throw IllegalArgumentException("IOU transfer can only be initiated by the IOU's current recipient")
            }

            // Stage 3 - Creating the output state.
            val outputState = inputStateData.copy(recipient = newRecipient)

            // Stage 4 - Creating the transfer command.
            val participants = inputState.data.participants + newRecipient.owningKey
            val txCommand = Command(IOUContract.Commands.Transfer(), participants)

            // Stage 5 - Generating an unsigned transaction.
            val notary = serviceHub.networkMapCache.notaryNodes.single().notaryIdentity
            val inputStateAndRef = StateAndRef(inputState, stateRef)
            val unsignedTx = TransactionType.General.Builder(notary).withItems(inputStateAndRef, outputState, txCommand)

            // Stage 6 - Verifying the transaction.
            unsignedTx.toWireTransaction().toLedgerTransaction(serviceHub).verify()

            // Stage 7 - Signing the transaction.
            val keyPair = serviceHub.legalIdentityKey
            val partSignedTx = unsignedTx.signWith(keyPair).toSignedTransaction(false)

            // Stage 8 - Collecting the counterparty signatures.
            val extraSig1 = subFlow(IOUTransferSubflow.Initiator(partSignedTx, inputStateData.sender))
            val extraSig2 = subFlow(IOUTransferSubflow.Initiator(partSignedTx, newRecipient))
            val fullySignedTx = partSignedTx + extraSig2 + extraSig1

            // Stage 9 - Calling FinalityFlow.
            val parties = setOf(inputStateData.sender, inputStateData.recipient, newRecipient)
            subFlow(FinalityFlow(fullySignedTx, parties))

            return fullySignedTx
        }
    }
}
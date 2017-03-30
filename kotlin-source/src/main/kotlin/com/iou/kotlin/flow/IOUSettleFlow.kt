package com.iou.kotlin.flow

import co.paralleluniverse.fibers.Suspendable
import com.iou.kotlin.contract.IOUContract
import com.iou.kotlin.state.IOUState
import net.corda.core.contracts.*
import net.corda.core.crypto.Party
import net.corda.core.crypto.composite
import net.corda.core.flows.FlowLogic
import net.corda.core.node.services.linearHeadsOfType
import net.corda.flows.FinalityFlow

object IOUSettleFlow {
    class Initiator(val linearId: UniqueIdentifier, val amount: Int) : FlowLogic<Unit>() {
        @Suspendable
        override fun call() {
            val me = serviceHub.myInfo.legalIdentity
            // Get the IOU state from the vault.
            val iouStates = serviceHub.vaultService.linearHeadsOfType<IOUState>()
            val iouToSettle = iouStates[linearId] ?: throw Exception("No such linearId")
            val counterparty = iouToSettle.state.data.recipient
            // Create a transaction builder.
            val notary = iouToSettle.state.notary
            val txBuilder = TransactionType.General.Builder(notary)
            // Get some cash from the vault and add the spend to our transaction builder.
            serviceHub.vaultService.generateSpend(txBuilder, amount.DOLLARS, counterparty.owningKey)
            // Add the iou states and settle command to the transaction builder.
            val settledIOU = iouToSettle.state.data.pay(amount)
            val settleCommand = Command(
                    IOUContract.Commands.Settle(),
                    listOf(counterparty.owningKey, me.owningKey))
            txBuilder.withItems(settledIOU, settleCommand)
            // Verify and sign.
            txBuilder.toWireTransaction().toLedgerTransaction(serviceHub).verify()
            val partSignedTx = txBuilder.signWith(serviceHub.legalIdentityKey).toSignedTransaction(false)
            partSignedTx.toString()
            // Send to other side.
            val signature = subFlow(IOUTransferSubflow.Initiator(partSignedTx, counterparty))
            val fullySignedTx = partSignedTx + signature
            subFlow(FinalityFlow(fullySignedTx, setOf(counterparty, me)))
        }
    }
}
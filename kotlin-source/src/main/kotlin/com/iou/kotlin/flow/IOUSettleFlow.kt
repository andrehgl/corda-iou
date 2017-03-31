package com.iou.kotlin.flow

import co.paralleluniverse.fibers.Suspendable
import com.iou.kotlin.contract.IOUContract
import com.iou.kotlin.state.IOUState
import net.corda.core.contracts.*
import net.corda.core.crypto.DigitalSignature
import net.corda.core.crypto.Party
import net.corda.core.crypto.composite
import net.corda.core.flows.FlowLogic
import net.corda.core.node.services.linearHeadsOfType
import net.corda.core.transactions.SignedTransaction
import net.corda.flows.FinalityFlow
import net.corda.flows.ResolveTransactionsFlow

object IOUSettleFlow {
    class Initiator(val linearId: UniqueIdentifier, val amount: Int) : FlowLogic<Unit>() {
        @Suspendable
        override fun call() {
            val me = serviceHub.myInfo.legalIdentity

            // Retrieve the IOU state from the vault.
            val iouStates = serviceHub.vaultService.linearHeadsOfType<IOUState>()
            val iouToSettle = iouStates[linearId]!!
            val counterparty = iouToSettle.state.data.recipient

            // Create a transaction builder.
            val notary = iouToSettle.state.notary
            val txBuilder = TransactionType.General.Builder(notary)

            // Get some cash from the vault and add a spend to our transaction builder.
            serviceHub.vaultService.generateSpend(txBuilder, amount.DOLLARS, counterparty.owningKey)

            // Add the IOU states and settle command to the transaction builder.
            val settleCommand = Command(
                    IOUContract.Commands.Settle(),
                    listOf(counterparty.owningKey, me.owningKey))

            // Add the input IOU and IOU settle command.
            txBuilder.addCommand(settleCommand)
            txBuilder.addInputState(iouToSettle)

            // Only add an output IOU state of the IOU has not been fully settled.
            val amountRemaining = iouToSettle.state.data.iouValue - iouToSettle.state.data.paid - amount

            if (amountRemaining > 0) {
                val settledIOU = iouToSettle.state.data.pay(amount)
                txBuilder.addOutputState(settledIOU)
            }

            // Verify and sign the transaction.
            txBuilder.toWireTransaction().toLedgerTransaction(serviceHub).verify()
            val partSignedTx = txBuilder.signWith(serviceHub.legalIdentityKey).toSignedTransaction(false)

            // Get the other side's signature.
            val signature = sendAndReceive<DigitalSignature.WithKey>(iouToSettle.state.data.recipient, partSignedTx).unwrap {
                tx -> tx
            }
            val fullySignedTx = partSignedTx + signature

            // Finalize the transaction.
            subFlow(FinalityFlow(fullySignedTx, setOf(counterparty, me)))
        }
    }

    class Acceptor(private val otherParty: Party) : FlowLogic<Unit>() {
        @Suspendable
        override fun call() {
            // Receive the signed transaction.
            val partSignedTx = receive(SignedTransaction::class.java, otherParty).unwrap { tx -> tx }

            // Resolve the inputs.
            val dependencyTxIDs = partSignedTx.tx.inputs.map {it.txhash}.toSet()
            subFlow(ResolveTransactionsFlow(dependencyTxIDs, otherParty))

            // Send back a signature over the transaction.
            send(otherParty, partSignedTx.signWithECDSA(serviceHub.legalIdentityKey))
        }
    }
}
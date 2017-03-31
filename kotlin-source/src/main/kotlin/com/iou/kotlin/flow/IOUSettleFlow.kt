package com.iou.kotlin.flow

import co.paralleluniverse.fibers.Suspendable
import com.iou.kotlin.contract.IOUContract
import com.iou.kotlin.state.IOUState
import net.corda.core.contracts.*
import net.corda.core.crypto.DigitalSignature
import net.corda.core.crypto.Party
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowLogic
import net.corda.core.node.services.linearHeadsOfType
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.flows.FinalityFlow
import net.corda.flows.ResolveTransactionsFlow

object IOUSettleFlow {
    class Initiator(val linearId: UniqueIdentifier, val amount: Int) : FlowLogic<Unit>() {
        @Suspendable
        override fun call() {
            // My identity.
            val me: Party = serviceHub.myInfo.legalIdentity

            // Retrieve the IOU state from the vault.
            val iouStates: Map<UniqueIdentifier, StateAndRef<IOUState>> = serviceHub.vaultService
                    .linearHeadsOfType<IOUState>()
            val iouToSettle: StateAndRef<IOUState> = iouStates[linearId]!!
            val counterparty: Party = iouToSettle.state.data.recipient

            // Create a transaction builder.
            val notary: Party = iouToSettle.state.notary
            val txBuilder: TransactionBuilder = TransactionType.General.Builder(notary)

            // Get some cash from the vault and add a spend to our transaction builder.
            serviceHub.vaultService.generateSpend(txBuilder, amount.DOLLARS, counterparty.owningKey)

            // Add the IOU states and settle command to the transaction builder.
            val settleCommand: Command = Command(
                    IOUContract.Commands.Settle(),
                    listOf(counterparty.owningKey, me.owningKey))

            // Add the input IOU and IOU settle command.
            txBuilder.addCommand(settleCommand)
            txBuilder.addInputState(iouToSettle)

            // Only add an output IOU state of the IOU has not been fully settled.
            val amountRemaining: Int = iouToSettle.state.data.iouValue - iouToSettle.state.data.paid - amount

            if (amountRemaining > 0) {
                val settledIOU: IOUState = iouToSettle.state.data.pay(amount)
                txBuilder.addOutputState(settledIOU)
            }

            // Verify and sign the transaction.
            txBuilder.toWireTransaction().toLedgerTransaction(serviceHub).verify()
            val partSignedTx: SignedTransaction = txBuilder.signWith(serviceHub.legalIdentityKey)
                    .toSignedTransaction(false)

            // Get the other side's signature.
            val signature: DigitalSignature.WithKey = sendAndReceive<DigitalSignature.WithKey>(
                    iouToSettle.state.data.recipient,
                    partSignedTx).unwrap {
                tx -> tx
            }

            val fullySignedTx: SignedTransaction = partSignedTx + signature

            // Finalize the transaction.
            subFlow(FinalityFlow(fullySignedTx, setOf(counterparty, me)))
        }
    }

    class Acceptor(private val otherParty: Party) : FlowLogic<Unit>() {
        @Suspendable
        override fun call() {
            // Receive the signed transaction.
            // We have skipped some of the verification for brevity!!
            val partSignedTx: SignedTransaction = receive(SignedTransaction::class.java, otherParty).unwrap {
                tx -> tx
            }

            // Resolve the inputs by calling the resolve transactions flow.
            val dependencyTxIDs: Set<SecureHash> = partSignedTx.tx.inputs.map {it.txhash}.toSet()
            // Call the resolve transactions flow.
            subFlow(ResolveTransactionsFlow(dependencyTxIDs, otherParty))

            // Send back a signature over the transaction.
            send(otherParty, partSignedTx.signWithECDSA(serviceHub.legalIdentityKey))
        }
    }
}
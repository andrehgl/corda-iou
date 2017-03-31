package com.iou.kotlin.flow

import co.paralleluniverse.fibers.Suspendable
import com.iou.kotlin.state.IOUState
import net.corda.core.crypto.DigitalSignature
import net.corda.core.crypto.Party
import net.corda.core.flows.FlowLogic
import net.corda.core.transactions.SignedTransaction
import net.corda.flows.ResolveTransactionsFlow

object IOUTransferSubflow {
    class Initiator(val stx: SignedTransaction, val otherParty: Party) : FlowLogic<DigitalSignature.WithKey>() {

        @Suspendable
        override fun call(): DigitalSignature.WithKey {
            // Stage 1 - Retrieving a signature over the transaction from the counterparty.
            return sendAndReceive(DigitalSignature.WithKey::class.java, otherParty, stx).unwrap { tx -> tx }
        }
    }

    class Acceptor(private val otherParty: Party) : FlowLogic<Unit>() {

        @Suspendable
        override fun call() {
            val partSignedTx = receive(SignedTransaction::class.java, otherParty).unwrap { tx ->
                // Stage 2 - Verifying the signatures
                val txOutputState = tx.tx.outputs[0].data as IOUState
                val notaryKey = serviceHub.networkMapCache.notaryNodes.single().notaryIdentity.owningKey

                tx.verifySignatures(txOutputState.sender.owningKey, txOutputState.recipient.owningKey, notaryKey)

                tx
            }

            // Stage 3 - Retrieving the transaction's dependencies.
            // We cannot call subflows within the unwrap() block.
            val dependencyTxIDs = setOf(partSignedTx.tx.inputs.single().txhash)
            subFlow(ResolveTransactionsFlow(dependencyTxIDs, otherParty))

            // Stage 4 - Verifying the transaction
            partSignedTx.tx.toLedgerTransaction(serviceHub).verify()

            // Stage 5 - Sending back a signature over the transaction.
            send(otherParty, partSignedTx.signWithECDSA(serviceHub.legalIdentityKey))
        }
    }
}
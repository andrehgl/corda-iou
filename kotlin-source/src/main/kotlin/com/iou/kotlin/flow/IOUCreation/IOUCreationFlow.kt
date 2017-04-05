package com.iou.kotlin.flow.IOUCreation

import co.paralleluniverse.fibers.Suspendable
import com.iou.kotlin.contract.IOUContract
import com.iou.kotlin.state.IOUState
import net.corda.core.contracts.Command
import net.corda.core.contracts.TransactionType
import net.corda.core.crypto.Party
import net.corda.core.crypto.composite
import net.corda.core.flows.FlowLogic
import net.corda.core.transactions.SignedTransaction
import net.corda.flows.FinalityFlow

object IOUCreationFlow {
    class Initiator(val iou: IOUState, val otherParty: Party) : FlowLogic<SignedTransaction>() {

        @Suspendable
        override fun call(): SignedTransaction {
            // Obtain a reference to the notary and parties we want to use.
            val notary = serviceHub.networkMapCache.notaryNodes.single().notaryIdentity
            val me = serviceHub.legalIdentityKey

            // Stage 1 - Generating an unsigned transaction
            val txCommand = Command(IOUContract.Commands.Create(), iou.participants)
            val unsignedTx = TransactionType.General.Builder(notary).withItems(iou, txCommand)

            // Stage 2 - Verifying the transaction
            unsignedTx.toWireTransaction().toLedgerTransaction(serviceHub).verify()

            // Stage 3 - Signing the transaction
            val partSignedTx = unsignedTx.signWith(serviceHub.legalIdentityKey).toSignedTransaction(false)

            val txFromCounterparty = sendAndReceive(SignedTransaction::class.java, otherParty, partSignedTx).unwrap { tx -> tx }

            return txFromCounterparty
        }
    }

    class Acceptor(val otherParty: Party) : FlowLogic<Unit>() {

        @Suspendable
        override fun call() {
            // Obtain a reference to the notary we want to use.
            val me = serviceHub.legalIdentityKey
            val notary = serviceHub.networkMapCache.notaryNodes.single().notaryIdentity

            val inboundPartSignedTx = receive<SignedTransaction>(otherParty).unwrap {
                tx ->
                    val wireTx = tx.verifySignatures(me.public.composite, notary!!.owningKey)
                    wireTx.toLedgerTransaction(serviceHub).verify()
                tx
            }

            val mySignature = inboundPartSignedTx.signWithECDSA(me)
            val counterpartySignedTxn = inboundPartSignedTx + mySignature

            send(otherParty, counterpartySignedTxn)
            val allParticipants = setOf(serviceHub.myInfo.legalIdentity, otherParty)
            subFlow(FinalityFlow(counterpartySignedTxn, allParticipants))

        }
    }
}
package com.iou.kotlin.flow

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

object IOUFlow {
    class Initiator(val iou: IOUState, val otherParty: Party) : FlowLogic<SignedTransaction>() {

        @Suspendable
        override fun call(): SignedTransaction {
            // Obtain a reference to the notary we want to use.
            val notary = serviceHub.networkMapCache.notaryNodes.single().notaryIdentity

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
            // Obtain a reference to our key pair.
            val keyPair = serviceHub.legalIdentityKey
            // Obtain a reference to the notary we want to use and its public key.
            val notary = serviceHub.networkMapCache.notaryNodes.single().notaryIdentity
            val notaryPubKey = notary.owningKey

            val partSignedTx = receive(SignedTransaction::class.java, otherParty).unwrap { tx ->
                // Stage 6 - Checking the transaction

                // Check that the signature of the other party is valid.
                // Our signature and the notary's signature are allowed to be omitted at this stage as
                // this is only a partially signed transaction.
                val wireTx = tx.verifySignatures(keyPair.public.composite, notaryPubKey)

                // Run the contract's verify function.
                // We want to be sure that the agreed-upon IOU is valid under the rules of the contract.
                // To do this we need to run the contract's verify() function.
                wireTx.toLedgerTransaction(serviceHub).verify()

                tx
            }

            // Stage 7 - Signing the transaction
            // Sign the transaction with our key pair and add it to the transaction.
            // We now have 'validation consensus', but we still need uniqueness consensus (i.e. notarisation).
            val mySig = partSignedTx.signWithECDSA(keyPair)
            // Add our signature to the transaction.
            val signedTx = partSignedTx + mySig

            send(otherParty, signedTx)

            // Stage 8 - Finalizing the transaction
            val participants = setOf(serviceHub.myInfo.legalIdentity, otherParty)
            // FinalityFlow() notarises the transaction and records it in each party's vault.
            subFlow(FinalityFlow(signedTx, participants))
        }
    }
}
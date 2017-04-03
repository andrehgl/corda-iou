package com.iou.kotlin.flow.IOUSettle

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
        override fun call() {}
    }

    class Acceptor(private val otherParty: Party) : FlowLogic<Unit>() {
        @Suspendable
        override fun call() {}
    }
}
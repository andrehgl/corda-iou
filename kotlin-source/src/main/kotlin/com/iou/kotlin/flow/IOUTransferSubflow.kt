package com.iou.kotlin.flow

import co.paralleluniverse.fibers.Suspendable
import com.iou.kotlin.state.IOUState
import net.corda.core.crypto.DigitalSignature
import net.corda.core.crypto.Party
import net.corda.core.flows.FlowLogic
import net.corda.core.transactions.SignedTransaction
import net.corda.flows.ResolveTransactionsFlow

object IOUTransferSubflow {
    class Initiator(val stx: SignedTransaction, val otherParty: Party) : FlowLogic<Unit>() {

        @Suspendable
        override fun call() {}
    }

    class Acceptor(private val otherParty: Party) : FlowLogic<Unit>() {

        @Suspendable
        override fun call() {}
    }
}
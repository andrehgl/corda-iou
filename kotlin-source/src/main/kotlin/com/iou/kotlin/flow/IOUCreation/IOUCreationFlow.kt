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
    class Initiator(val iou: IOUState, val otherParty: Party) : FlowLogic<Unit>() {

        @Suspendable
        override fun call() { }
    }

    class Acceptor(val otherParty: Party) : FlowLogic<Unit>() {

        @Suspendable
        override fun call() { }
    }
}
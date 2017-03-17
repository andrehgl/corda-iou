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
    class Initiator(val stateRef: StateRef, val newRecipient: Party) : FlowLogic<Unit>() {

        @Suspendable
        override fun call() {}
    }
}
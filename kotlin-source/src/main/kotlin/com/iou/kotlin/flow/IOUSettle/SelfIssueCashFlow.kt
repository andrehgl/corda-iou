package com.iou.kotlin.flow.IOUSettle

import co.paralleluniverse.fibers.Suspendable
import net.corda.contracts.asset.Cash
import net.corda.core.contracts.DOLLARS
import net.corda.core.flows.FlowLogic
import net.corda.core.serialization.OpaqueBytes
import net.corda.flows.CashCommand
import net.corda.flows.CashFlow

class SelfIssueCashFlow(val amount: Int) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {}
}
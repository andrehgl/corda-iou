package com.iou.kotlin.flow

import co.paralleluniverse.fibers.Suspendable
import net.corda.contracts.asset.Cash
import net.corda.core.contracts.DOLLARS
import net.corda.core.flows.FlowLogic
import net.corda.core.serialization.OpaqueBytes
import net.corda.flows.CashCommand
import net.corda.flows.CashFlow

class SelfIssueCashFlow(val amount: Int) : FlowLogic<Cash.State>() {
    @Suspendable
    override fun call(): Cash.State {
        /** Create the cash issue command. */
        val issueRef = OpaqueBytes.of(0)
        val notary = serviceHub.networkMapCache.notaryNodes.single().notaryIdentity
        val me = serviceHub.myInfo.legalIdentity
        val issueCashCommand = CashCommand.IssueCash(amount.DOLLARS, issueRef, me, notary)
        /** Create the cash issuance transaction. */
        val cashIssueTransaction = subFlow(CashFlow(issueCashCommand))
        /** Return the cash output. */
        return cashIssueTransaction.tx.outputs.single().data as Cash.State
    }
}
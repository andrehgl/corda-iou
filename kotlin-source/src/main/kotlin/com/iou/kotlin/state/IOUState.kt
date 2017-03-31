package com.iou.kotlin.state

import com.iou.kotlin.contract.IOUContract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.crypto.CompositeKey
import net.corda.core.crypto.Party
import java.security.PublicKey

/**
 * The state object recording IOU agreements between two parties.
 *
 * A state must implement [ContractState] or one of its descendants.
 *
 * @param contract the contract which governs which transactions are valid for this state object.
 */
data class IOUState(val iouValue: Int,
                    val sender: Party,
                    val recipient: Party,
                    override val contract: IOUContract,
                    override val linearId: UniqueIdentifier = UniqueIdentifier(),
                    val paid: Int? = 0) : LinearState {

    override val participants: List<CompositeKey>
        get() = listOf(sender, recipient).map { it.owningKey }

    /**
     * This returns true if the state should be tracked by the vault of a particular node. In this case the logic is
     * simple; track this state if we are one of the involved parties.
     */
    override fun isRelevant(ourKeys: Set<PublicKey>): Boolean {
        return ourKeys.intersect(participants.flatMap {it.keys}).isNotEmpty()
    }

    fun pay(amount: Int) = copy(paid = amount)

    fun withoutPaidAmount(): IOUState = copy(paid = null)
}
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
data class IOUState(
        val iouValue: Int,
        val sender: Party,
        val recipient: Party,
        override val contract: IOUContract
        ) : LinearState {

    override val participants: List<CompositeKey> get() = listOf(sender.owningKey, recipient.owningKey)
    override val linearId: UniqueIdentifier = UniqueIdentifier()
    override fun isRelevant(ourKeys: Set<PublicKey>): Boolean {
//        return ourKeys.contains(participants[0].singleKey) || ourKeys.contains(participants[1].singleKey)
        return ourKeys.intersect(participants.flatMap { it.keys }).isNotEmpty()
    }
}
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
data class IOUState(override val contract: IOUContract) : ContractState {

    override val participants: List<CompositeKey> get() = listOf()
}
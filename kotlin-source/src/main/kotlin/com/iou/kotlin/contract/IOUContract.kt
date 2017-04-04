package com.iou.kotlin.contract

import com.iou.kotlin.state.IOUState
import net.corda.core.contracts.*
import net.corda.core.crypto.SecureHash

class IOUContract : Contract {
    /**
     * The verify() function of all the states' contracts must not throw an exception for a transaction to be
     * considered valid.
     */
    override fun verify(tx: TransactionForContract) {
        tx.commands.requireSingleCommand<Commands.Create>()
        requireThat {
            "No inputs should be consumed when issuing an IOU." by (tx.inputs.isEmpty())
            "Only one output state should be created." by (tx.outputs.size == 1)
            val output = tx.outputs.single() as IOUState
            "The IOU's value must be non-negative." by (output.iouValue > 0)
            "All of the participants must be signers." by (tx.commands.single().signers.containsAll(output.participants))
        }
    }

    interface Commands : CommandData {
        class Create : TypeOnlyCommandData(), Commands
    }

    /** This is a reference to the underlying legal contract template and associated parameters.  */
    override val legalContractReference = SecureHash.sha256("IOU contract template and params")
}
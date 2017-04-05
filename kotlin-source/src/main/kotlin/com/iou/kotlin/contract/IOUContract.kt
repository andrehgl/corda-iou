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
//        tx.commands.requireSingleCommand<Commands.Create>()

        requireThat {
            "Command should be included" by (tx.commands.isNotEmpty())
        }

        tx.commands.forEach {
            val commandData = it.value
            if (commandData is Commands.Create) {
                requireThat {
                    "No inputs should be consumed when issuing an IOU." by (tx.inputs.isEmpty())
                    "Only one output state should be created." by (tx.outputs.size == 1)
                    val output = tx.outputs.single() as IOUState
                    "The IOU's value must be non-negative." by (output.iouValue > 0)
                    "All of the participants must be signers." by (tx.commands.single().signers.containsAll(output.participants))
                }
            }
            else if (commandData is Commands.Transfer) {
                requireThat {
                    "One input should be consumed when transferring an IOU." by (tx.inputs.size == 1)
                    "Failed requirement: Only one output state should be created." by (tx.outputs.size == 1)
                    val output = tx.outputs.single() as IOUState
                    val input = tx.inputs.single() as IOUState
                    "The IOU's value must be non-negative." by (output.iouValue > 0)
                    "The input and output IOUs must have the same value." by (input.iouValue == output.iouValue)
                    "The input and output IOUs must have the same sender." by (input.sender == output.sender)
                    "The input and output IOUs must have different recipients." by (input.recipient != output.recipient)
                    "All of the participants must be signers." by (it.signers.containsAll(tx.inputs.single().participants))
                    "All of the participants must be signers." by (it.signers.containsAll(tx.outputs.single().participants))
                }
            }
        }

    }

    interface Commands : CommandData {
        class Create : TypeOnlyCommandData(), Commands
        class Transfer : TypeOnlyCommandData(), Commands
    }

    /** This is a reference to the underlying legal contract template and associated parameters.  */
    override val legalContractReference = SecureHash.sha256("IOU contract template and params")
}
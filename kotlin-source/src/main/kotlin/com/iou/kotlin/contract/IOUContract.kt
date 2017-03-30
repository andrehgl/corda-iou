package com.iou.kotlin.contract

import com.iou.kotlin.state.IOUState
import net.corda.contracts.asset.Cash
import net.corda.contracts.asset.sumCash
import net.corda.core.contracts.*
import net.corda.core.crypto.SecureHash

class IOUContract : Contract {
    /**
     * This contract's commands.
     */
    interface Commands : CommandData {
        class Create : TypeOnlyCommandData(), Commands
        class Transfer : TypeOnlyCommandData(), Commands
        class Settle : TypeOnlyCommandData(), Commands
    }
    /**
     * The verify() function of all the states' contracts must not throw an exception for a transaction to be
     * considered valid.
     */
    override fun verify(tx: TransactionForContract) {
        val command = tx.commands.requireSingleCommand<Commands>()
        when (command.value) {
            is Commands.Create -> {
                requireThat {
                    // Constraint on the value of the IOU.
                    "The IOU's value must be non-negative." by ((tx.outputs.first() as IOUState).iouValue > 0)

                    // Constraints on the number of input/output states.
                    "No inputs should be consumed when issuing an IOU." by tx.inputs.isEmpty()
                    "Only one output state should be created." by (tx.outputs.size == 1)

                    // Constraint on the signers.
                    "All of the participants must be signers." by command.signers.containsAll(tx.outputs.single().participants)
                }
            }
            is Commands.Transfer -> {
                requireThat {
                    // Constraint on the value of the IOU.
                    "The IOU's value must be non-negative." by ((tx.outputs.first() as IOUState).iouValue > 0)

                    // Constraints on the number of input/output states.
                    "One input should be consumed when transferring an IOU." by (tx.inputs.size == 1)
                    "Only one output state should be created." by (tx.outputs.size == 1)

                    // Constraint on the relationship between the input and output IOUs.
                    val input = tx.inputs.single() as IOUState
                    val output = tx.outputs.single() as IOUState

                    "The input and output IOUs must have the same value." by (input.iouValue == output.iouValue)
                    "The input and output IOUs must have the same sender." by (input.sender == output.sender)
                    "The input and output IOUs must have different recipients." by (input.recipient != output.recipient)

                    // Constraints on the signers.
                    "All of the participants must be signers." by command.signers.containsAll(tx.inputs.single().participants)
                    "All of the participants must be signers." by command.signers.containsAll(tx.outputs.single().participants)
                }
            }
            is Commands.Settle -> {
                // We only want ONE settle command.
                tx.commands.select<Commands.Settle>()
                // There should only be ONE input and output IOU.
                val ious = tx.groupStates<IOUState, UniqueIdentifier> { it.linearId }.single()
                // There should only be one input IOU.
                requireThat { "There must be one input IOU." by (ious.inputs.size == 1) }
                val inputIou = ious.inputs.single()
                // Get a reference to the cash output moduleOne.
                // We don't care about inputs as the Cash contract validates those.
                val cash = tx.outputs.filterIsInstance<Cash.State>()
                // Check that the cash output moduleOne have our public keys as the new owner.
                val acceptableCash = cash.filter { it.owner == inputIou.recipient.owningKey }
                requireThat {
                    "There must be output cash." by (cash.isNotEmpty())
                    "There must be output cash paid to the recipient." by (acceptableCash.isNotEmpty())
                }
                // Sum the cash being sent to us (we don't care about the issuer).
                val sumAcceptableCash = acceptableCash.sumCash().withoutIssuer().quantity.toInt()/100
                val amountOutstanding = inputIou.iouValue - inputIou.paid!!
                requireThat {
                    "The amount settled cannot be more than the amount outstanding." by (amountOutstanding >= sumAcceptableCash)
                }
                if (amountOutstanding == sumAcceptableCash) {
                    // If the IOU has been fully settled then there should be no IOU output state.
                    requireThat {
                        "There must be no output IOU as it has been fully settled." by (ious.outputs.isEmpty())
                    }
                } else {
                    // If the IOU has been partially settled then it should still exist.
                    requireThat { "There must be one output IOU." by (ious.outputs.size == 1) }
                    val outputIou = ious.outputs.single()
                    requireThat {
                        "The only property which may change is 'paid'." by (inputIou.withoutPaidAmount == outputIou.withoutPaidAmount)
                    }
                }
                requireThat {
                    "All of the participants must be signers." by command.signers.containsAll(inputIou.participants)
                }
            }
        }
    }
    /** This is a reference to the underlying legal contract template and associated parameters.  */
    override val legalContractReference = SecureHash.sha256("IOU contract template and params")
}
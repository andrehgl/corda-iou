package com.iou.kotlin.transactions

import com.iou.kotlin.contract.IOUContract
import com.iou.kotlin.state.IOUState
import net.corda.contracts.asset.Cash
import net.corda.core.contracts.Amount
import net.corda.core.contracts.DOLLARS
import net.corda.core.contracts.POUNDS
import net.corda.core.contracts.`issued by`
import net.corda.core.crypto.CompositeKey
import net.corda.core.crypto.Party
import net.corda.core.serialization.OpaqueBytes
import net.corda.testing.*
import org.junit.Test
import java.util.*


class IOUSettleTests {
    val defaultRef = OpaqueBytes(ByteArray(1, { 1 }))
    val defaultIssuer = MEGA_CORP.ref(defaultRef)

    private fun createIOUState(amount: Int, sender: Party, recipient: Party): IOUState {
        return IOUState(amount, sender, recipient, IOUContract())
    }

    private fun createCashState(amount: Amount<Currency>, owner: CompositeKey): Cash.State {
        return Cash.State(amount = amount `issued by` defaultIssuer, owner = owner)
    }

    @Test
    fun mustIncludeSettleCommand() {
        ledger {
            transaction {
                input { createIOUState(10, ALICE, BOB) }
                output { createIOUState(10, ALICE, BOB).pay(10) }
                fails()
            }
        }
    }

    @Test
    fun mustBeOnlyOneGroupOfIOUs() {
        val iouOne = createIOUState(10, ALICE, BOB)
        val iouTwo = createIOUState(5, ALICE, BOB)
        ledger {
            transaction {
                input { iouOne }
                input { iouTwo }
                command(ALICE_PUBKEY, BOB_PUBKEY) { IOUContract.Commands.Settle() }
                output { iouOne.pay(10) }
                output { iouTwo.pay(5) }
                fails()
            }
        }
    }

    @Test
    fun mustHaveOneInputIOU() {
        val iou = createIOUState(10, ALICE, BOB)
        ledger {
            transaction {
                command(ALICE_PUBKEY, BOB_PUBKEY) { IOUContract.Commands.Settle() }
                output { iou }
                failsWith("There must be one input IOU.")
            }
        }
    }

    @Test
    fun mustBeCashOutputStatesPresent() {
        val iou = createIOUState(10, ALICE, BOB)
        val cash = createCashState(5.DOLLARS, ALICE_PUBKEY)
        val cashPayment = cash.withNewOwner(newOwner = BOB_PUBKEY)
        ledger {
            transaction {
                input { iou }
                output { iou.pay(5) }
                command(ALICE_PUBKEY, BOB_PUBKEY) { IOUContract.Commands.Settle() }
                failsWith("There must be output cash.")
            }
            transaction {
                input { iou }
                input { cash }
                output { iou.pay(5) }
                output { cashPayment.second }
                command(ALICE_PUBKEY) { cashPayment.first }
                command(ALICE_PUBKEY, BOB_PUBKEY) { IOUContract.Commands.Settle() }
                verifies()
            }
        }
    }

    @Test
    fun mustBeCashOutputStatesWithRecipientAsOwner() {
        val iou = createIOUState(10, ALICE, BOB)
        val cash = createCashState(5.POUNDS, ALICE_PUBKEY)
        val invalidCashPayment = cash.withNewOwner(newOwner = CHARLIE_PUBKEY)
        val validCashPayment = cash.withNewOwner(newOwner = BOB_PUBKEY)
        ledger {
            transaction {
                input { iou }
                input { cash }
                output { iou.pay(5) }
                output { invalidCashPayment.second }
                command(ALICE_PUBKEY) { invalidCashPayment.first }
                command(ALICE_PUBKEY, BOB_PUBKEY) { IOUContract.Commands.Settle() }
                failsWith("There must be output cash paid to the recipient.")
            }
            transaction {
                input { iou }
                input { cash }
                output { iou.pay(5) }
                output { validCashPayment.second }
                command(ALICE_PUBKEY) { validCashPayment.first }
                command(ALICE_PUBKEY, BOB_PUBKEY) { IOUContract.Commands.Settle() }
                verifies()
            }
        }
    }

    @Test
    fun cashSettlementAmountMustBeLessThanRemainingIOUAmount() {
        val iou = createIOUState(10, ALICE, BOB)
        val elevenDollars = createCashState(11.DOLLARS, ALICE_PUBKEY)
        val tenDollars = createCashState(10.DOLLARS, ALICE_PUBKEY)
        val fiveDollars = createCashState(5.DOLLARS, ALICE_PUBKEY)
        ledger {
            transaction {
                input { iou }
                input { elevenDollars }
                output { iou.pay(11) }
                output { elevenDollars.withNewOwner(newOwner = BOB_PUBKEY).second }
                command(ALICE_PUBKEY) { elevenDollars.withNewOwner(newOwner = BOB_PUBKEY).first }
                command(ALICE_PUBKEY, BOB_PUBKEY) { IOUContract.Commands.Settle() }
                failsWith("The amount settled cannot be more than the amount outstanding.")
            }
            transaction {
                input { iou }
                input { fiveDollars }
                output { iou.pay(5) }
                output { fiveDollars.withNewOwner(newOwner = BOB_PUBKEY).second }
                command(ALICE_PUBKEY) { fiveDollars.withNewOwner(newOwner = BOB_PUBKEY).first }
                command(ALICE_PUBKEY, BOB_PUBKEY) { IOUContract.Commands.Settle() }
                verifies()
            }
            transaction {
                input { iou }
                input { tenDollars }
                output { tenDollars.withNewOwner(newOwner = BOB_PUBKEY).second }
                command(ALICE_PUBKEY) { tenDollars.withNewOwner(newOwner = BOB_PUBKEY).first }
                command(ALICE_PUBKEY, BOB_PUBKEY) { IOUContract.Commands.Settle() }
                verifies()
            }
        }
    }


    @Test
    fun mustOnlyHaveOutputIOUIfNotFullySettling() {
        val iou = createIOUState(10, ALICE, BOB)
        val tenDollars = createCashState(10.DOLLARS, ALICE_PUBKEY)
        val fiveDollars = createCashState(5.DOLLARS, ALICE_PUBKEY)
        ledger {
            transaction {
                input { iou }
                input { fiveDollars }
                output { fiveDollars.withNewOwner(newOwner = BOB_PUBKEY).second }
                command(ALICE_PUBKEY) { fiveDollars.withNewOwner(newOwner = BOB_PUBKEY).first }
                command(ALICE_PUBKEY, BOB_PUBKEY) { IOUContract.Commands.Settle() }
                failsWith("There must be one output IOU.")
            }
            transaction {
                input { iou }
                input { fiveDollars }
                output { fiveDollars.withNewOwner(newOwner = BOB_PUBKEY).second }
                output { iou.pay(5) }
                command(ALICE_PUBKEY) { fiveDollars.withNewOwner(newOwner = BOB_PUBKEY).first }
                command(ALICE_PUBKEY, BOB_PUBKEY) { IOUContract.Commands.Settle() }
                verifies()
            }
            transaction {
                input { tenDollars }
                input { iou }
                output { iou.pay(10) }
                output { tenDollars.withNewOwner(newOwner = BOB_PUBKEY).second }
                command(ALICE_PUBKEY) { tenDollars.withNewOwner(newOwner = BOB_PUBKEY).first }
                command(ALICE_PUBKEY, BOB_PUBKEY) { IOUContract.Commands.Settle() }
                failsWith("There must be no output IOU as it has been fully settled.")
            }
            transaction {
                input { tenDollars }
                input { iou }
                output { tenDollars.withNewOwner(newOwner = BOB_PUBKEY).second }
                command(ALICE_PUBKEY) { tenDollars.withNewOwner(newOwner = BOB_PUBKEY).first }
                command(ALICE_PUBKEY, BOB_PUBKEY) { IOUContract.Commands.Settle() }
                verifies()
            }
        }
    }

//    @Test
//    fun onlyPaidPropertyMayChange() {
//        val iou = createIOUState(10, ALICE, BOB)
//        val fiveDollars = createCashState(5.DOLLARS, ALICE_PUBKEY)
//        ledger {
//            transaction {
//                input { iou }
//                input { fiveDollars }
//                output { fiveDollars.withNewOwner(newOwner = BOB_PUBKEY).second }
//                output { iou.copy(sender = BOB, paid = 5) }
//                command(ALICE_PUBKEY) { fiveDollars.withNewOwner(newOwner = BOB_PUBKEY).first }
//                command(ALICE_PUBKEY, BOB_PUBKEY) { IOUContract.Commands.Settle() }
//                failsWith("The only property which may change is 'paid'.")
//            }
//            transaction {
//                input { iou }
//                input { fiveDollars }
//                output { fiveDollars.withNewOwner(newOwner = BOB_PUBKEY).second }
//                output { iou.copy(iouValue = 0, paid = 5) }
//                command(ALICE_PUBKEY) { fiveDollars.withNewOwner(newOwner = BOB_PUBKEY).first }
//                command(ALICE_PUBKEY, BOB_PUBKEY) { IOUContract.Commands.Settle() }
//                failsWith("The only property which may change is 'paid'.")
//            }
//            transaction {
//                input { iou }
//                input { fiveDollars }
//                output { fiveDollars.withNewOwner(newOwner = BOB_PUBKEY).second }
//                output { iou.copy(recipient = CHARLIE, paid = 5) }
//                command(ALICE_PUBKEY) { fiveDollars.withNewOwner(newOwner = BOB_PUBKEY).first }
//                command(ALICE_PUBKEY, BOB_PUBKEY) { IOUContract.Commands.Settle() }
//                failsWith("The only property which may change is 'paid'.")
//            }
//            transaction {
//                input { iou }
//                input { fiveDollars }
//                output { fiveDollars.withNewOwner(newOwner = BOB_PUBKEY).second }
//                output { iou.pay(5) }
//                command(ALICE_PUBKEY) { fiveDollars.withNewOwner(newOwner = BOB_PUBKEY).first }
//                command(ALICE_PUBKEY, BOB_PUBKEY) { IOUContract.Commands.Settle() }
//                verifies()
//            }
//        }
//    }

    @Test
    fun mustBeSignedByAllParticipants() {
        val iou = createIOUState(10, ALICE, BOB)
        val cash = createCashState(5.DOLLARS, ALICE_PUBKEY)
        val cashPayment = cash.withNewOwner(newOwner = BOB_PUBKEY)
        ledger {
            transaction {
                input { cash }
                input { iou }
                output { cashPayment.second }
                command(ALICE_PUBKEY) { cashPayment.first }
                output { iou.pay(5) }
                command(ALICE_PUBKEY, CHARLIE_PUBKEY) { IOUContract.Commands.Settle() }
                failsWith("All of the participants must be signers.")
            }
            transaction {
                input { cash }
                input { iou }
                output { cashPayment.second }
                command(ALICE_PUBKEY) { cashPayment.first }
                output { iou.pay(5) }
                command(BOB_PUBKEY) { IOUContract.Commands.Settle() }
                failsWith("All of the participants must be signers.")
            }
            transaction {
                input { cash }
                input { iou }
                output { cashPayment.second }
                command(ALICE_PUBKEY) { cashPayment.first }
                output { iou.pay(5) }
                command(ALICE_PUBKEY, BOB_PUBKEY) { IOUContract.Commands.Settle() }
                verifies()
            }
        }
    }
}

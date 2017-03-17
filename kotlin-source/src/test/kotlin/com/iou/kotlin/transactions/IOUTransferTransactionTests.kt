package com.iou.kotlin.transactions

import com.iou.kotlin.contract.IOUContract
import com.iou.kotlin.state.IOUState
import net.corda.testing.*
import org.junit.Test

class IOUTransferTransactionTests {

//    @Test
//    fun mustIncludeCommand() {
//        ledger {
//            transaction {
//                input { IOUState(1, ALICE, BOB, IOUContract()) }
//                output { IOUState(1, ALICE, CHARLIE, IOUContract()) }
//                fails()
//            }
//        }
//    }
//
//    @Test
//    fun cannotCreateNegativeValueIOUs() {
//        ledger {
//            transaction {
//                input { IOUState(-1, ALICE, BOB, IOUContract()) }
//                output { IOUState(-1, ALICE, CHARLIE, IOUContract()) }
//                command (ALICE_PUBKEY, BOB_PUBKEY, CHARLIE_PUBKEY) { IOUContract.Transfer() }
//                failsWith("The IOU's value must be non-negative.")
//            }
//        }
//    }
//
//    @Test
//    fun transferTransactionMustHaveOneInput() {
//        ledger {
//            transaction {
//                output { IOUState(1, ALICE, CHARLIE, IOUContract()) }
//                command(ALICE_PUBKEY, BOB_PUBKEY, CHARLIE_PUBKEY) { IOUContract.Transfer() }
//                failsWith("One input should be consumed when transferring an IOU.")
//            }
//        }
//    }
//
//    @Test
//    fun transferTransactionMustHaveOneOutput() {
//        ledger {
//            transaction {
//                input { IOUState(1, ALICE, BOB, IOUContract()) }
//                output { IOUState(1, ALICE, CHARLIE, IOUContract()) }
//                output { IOUState(1, ALICE, CHARLIE, IOUContract()) }
//                command(ALICE_PUBKEY, BOB_PUBKEY, CHARLIE_PUBKEY) { IOUContract.Transfer() }
//                failsWith("Failed requirement: Only one output state should be created.")
//            }
//        }
//    }
//
//    @Test
//    fun transferTransactionDoesNotChangeIOUValue() {
//        ledger {
//            transaction {
//                input { IOUState(1, ALICE, BOB, IOUContract()) }
//                output { IOUState(2, ALICE, CHARLIE, IOUContract()) }
//                command(ALICE_PUBKEY, BOB_PUBKEY, CHARLIE_PUBKEY) { IOUContract.Transfer() }
//                failsWith("The input and output IOUs must have the same value.")
//            }
//        }
//    }
//
//    @Test
//    fun transferTransactionDoesNotChangeIOUSender() {
//        ledger {
//            transaction {
//                input { IOUState(1, ALICE, BOB, IOUContract()) }
//                output { IOUState(1, BOB, CHARLIE, IOUContract()) }
//                command(ALICE_PUBKEY, BOB_PUBKEY, CHARLIE_PUBKEY) { IOUContract.Transfer() }
//                failsWith("The input and output IOUs must have the same sender.")
//            }
//        }
//    }
//
//    @Test
//    fun transferTransactionChangesIOURecipient() {
//        ledger {
//            transaction {
//                input { IOUState(1, ALICE, BOB, IOUContract()) }
//                output { IOUState(1, ALICE, BOB, IOUContract()) }
//                command(ALICE_PUBKEY, BOB_PUBKEY, CHARLIE_PUBKEY) { IOUContract.Transfer() }
//                failsWith("The input and output IOUs must have different recipients.")
//            }
//        }
//    }
//
//    @Test
//    fun senderMustSignCreateTransaction() {
//        ledger {
//            transaction {
//                input { IOUState(1, ALICE, BOB, IOUContract()) }
//                output { IOUState(1, ALICE, CHARLIE, IOUContract()) }
//                command(BOB_PUBKEY, CHARLIE_PUBKEY) { IOUContract.Transfer() }
//                failsWith("All of the participants must be signers.")
//            }
//        }
//    }
//
//    @Test
//    fun oldRecipientMustSignCreateTransaction() {
//        ledger {
//            transaction {
//                input { IOUState(1, ALICE, BOB, IOUContract()) }
//                output { IOUState(1, ALICE, CHARLIE, IOUContract()) }
//                command(ALICE_PUBKEY, CHARLIE_PUBKEY) { IOUContract.Transfer() }
//                failsWith("All of the participants must be signers.")
//            }
//        }
//    }
//
//    @Test
//    fun newRecipientMustSignCreateTransaction() {
//        ledger {
//            transaction {
//                input { IOUState(1, ALICE, BOB, IOUContract()) }
//                output { IOUState(1, ALICE, CHARLIE, IOUContract()) }
//                command(ALICE_PUBKEY, BOB_PUBKEY) { IOUContract.Transfer() }
//                failsWith("All of the participants must be signers.")
//            }
//        }
//    }
}
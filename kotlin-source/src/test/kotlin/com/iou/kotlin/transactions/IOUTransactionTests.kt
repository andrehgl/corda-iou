package com.iou.kotlin.transactions

import com.iou.kotlin.contract.IOUContract
import com.iou.kotlin.state.IOUState
import net.corda.testing.*
import org.junit.Test

class IOUTransactionTests {

//    @Test
//    fun mustIncludeCommand() {
//        ledger {
//            transaction {
//                output { IOUState(1, MINI_CORP, MEGA_CORP, IOUContract()) }
//                fails()
//            }
//        }
//    }
//
//    @Test
//    fun cannotCreateNegativeValueIOUs() {
//        ledger {
//            transaction {
//                output(IOUState(-1, MINI_CORP, MEGA_CORP, IOUContract()))
//                command(MINI_CORP_PUBKEY, MEGA_CORP_PUBKEY) { IOUContract.Create() }
//                failsWith("The IOU's value must be non-negative.")
//            }
//        }
//    }
//
//    @Test
//    fun createTransactionMustHaveNoInputs() {
//        ledger {
//            transaction {
//                input { IOUState(1, MINI_CORP, MEGA_CORP, IOUContract()) }
//                output { IOUState(1, MINI_CORP, MEGA_CORP, IOUContract()) }
//                command(MINI_CORP_PUBKEY, MEGA_CORP_PUBKEY) { IOUContract.Create() }
//                failsWith("No inputs should be consumed when issuing an IOU.")
//            }
//        }
//    }
//
//    @Test
//    fun createTransactionMustHaveOneOutput() {
//        ledger {
//            transaction {
//                output { IOUState(1, MINI_CORP, MEGA_CORP, IOUContract()) }
//                output { IOUState(1, MINI_CORP, MEGA_CORP, IOUContract()) }
//                command(MINI_CORP_PUBKEY, MEGA_CORP_PUBKEY) { IOUContract.Create() }
//                failsWith("Only one output state should be created.")
//            }
//        }
//    }
//
//    @Test
//    fun senderMustSignCreateTransaction() {
//        ledger {
//            transaction {
//                output { IOUState(1, MINI_CORP, MEGA_CORP, IOUContract()) }
//                command(MEGA_CORP_PUBKEY) { IOUContract.Create() }
//                failsWith("All of the participants must be signers.")
//            }
//        }
//    }
//
//    @Test
//    fun recipientMustSignCreateTransaction() {
//        ledger {
//            transaction {
//                output { IOUState(1, MINI_CORP, MEGA_CORP, IOUContract()) }
//                command(MINI_CORP_PUBKEY) { IOUContract.Create() }
//                failsWith("All of the participants must be signers.")
//            }
//        }
//    }
}
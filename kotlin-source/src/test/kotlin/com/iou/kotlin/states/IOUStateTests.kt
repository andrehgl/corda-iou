package com.iou.kotlin.states

import com.iou.kotlin.contract.IOUContract
import com.iou.kotlin.state.IOUState
import net.corda.core.contracts.LinearState
import net.corda.core.crypto.Party
import net.corda.testing.MEGA_CORP
import net.corda.testing.MEGA_CORP_PUBKEY
import net.corda.testing.MINI_CORP
import net.corda.testing.MINI_CORP_PUBKEY
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class IOUStateTests {
    val miniCorp = MINI_CORP
    val megaCorp = MEGA_CORP
    val miniCorpKey = MINI_CORP_PUBKEY
    val megaCorpKey = MEGA_CORP_PUBKEY

//    @Test
//    fun hasIOUValueFieldOfCorrectType() {
//        // Does the iouValue field exist?
//        IOUState::class.java.getDeclaredField("iouValue")
//        // Is the iouValue field of the correct type?
//        assertEquals(IOUState::class.java.getDeclaredField("iouValue").type, Int::class.java)
//    }
//
//    @Test
//    fun hasSenderFieldOfCorrectType() {
//        // Does the sender field exist?
//        IOUState::class.java.getDeclaredField("sender")
//        // Is the sender field of the correct type?
//        assertEquals(IOUState::class.java.getDeclaredField("sender").type, Party::class.java)
//    }
//
//    @Test
//    fun hasRecipientFieldOfCorrectType() {
//        // Does the recipient field exist?
//        IOUState::class.java.getDeclaredField("recipient")
//        // Is the recipient field of the correct type?
//        assertEquals(IOUState::class.java.getDeclaredField("recipient").type, Party::class.java)
//    }
//
//    @Test
//    fun senderIsParticipant() {
//        val iouState = IOUState(1, miniCorp, megaCorp, IOUContract())
//        assertNotEquals(iouState.participants.indexOf(miniCorpKey), -1)
//    }
//
//    @Test
//    fun recipientIsParticipant() {
//        val iouState = IOUState(1, miniCorp, megaCorp, IOUContract())
//        assertNotEquals(iouState.participants.indexOf(megaCorpKey), -1)
//    }
//
//    @Test
//    fun isLinearState() {
//        assert(LinearState::class.java.isAssignableFrom(IOUState::class.java))
//    }
//
//    @Test
//    fun isRelevantToSender() {
//        val iouState = IOUState(1, miniCorp, megaCorp, IOUContract())
//        assert(iouState.isRelevant(setOf(miniCorpKey.singleKey)))
//    }
//
//    @Test
//    fun isRelevantToRecipient() {
//        val iouState = IOUState(1, miniCorp, megaCorp, IOUContract())
//        assert(iouState.isRelevant(setOf(megaCorpKey.singleKey)))
//    }
}
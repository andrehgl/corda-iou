package com.iou.kotlin.flows

import com.iou.kotlin.contract.IOUContract
import com.iou.kotlin.flow.IOUTransferFlow
import com.iou.kotlin.state.IOUState
import net.corda.core.contracts.*
import net.corda.core.getOrThrow
import net.corda.core.utilities.DUMMY_NOTARY
import net.corda.core.utilities.DUMMY_NOTARY_KEY
import net.corda.testing.node.MockNetwork
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class IOUTransferFlowTests {
    lateinit var net: MockNetwork
    lateinit var a: MockNetwork.MockNode
    lateinit var b: MockNetwork.MockNode
    lateinit var c: MockNetwork.MockNode

    @Before
    fun setup() {
        net = MockNetwork()
        val nodes = net.createSomeNodes(3)
        a = nodes.partyNodes[0]
        b = nodes.partyNodes[1]
        c = nodes.partyNodes[2]
        net.runNetwork(-1)
    }

    @After
    fun tearDown() {
        net.stopNodes()
    }

//    fun insertStandardIOUCreationTransaction(): StateRef {
//        val state = IOUState(1, a.info.legalIdentity, b.info.legalIdentity, IOUContract())
//
//        return insertIOUCreationTransaction(state)
//    }
//
//    fun insertIOUCreationTransaction(state: IOUState): StateRef {
//        val keysAB = listOf(a.info.legalIdentity.owningKey, b.info.legalIdentity.owningKey)
//        val txCommand = Command(IOUContract.Create(), keysAB)
//
//        val tb =  TransactionType.General.Builder(DUMMY_NOTARY).withItems(state, txCommand)
//
//        val tx = tb
//                .signWith(a.services.legalIdentityKey)
//                .signWith(b.services.legalIdentityKey)
//                .signWith(DUMMY_NOTARY_KEY)
//                .toSignedTransaction(true)
//
//        for (node in listOf(a, b))
//            node.services.recordTransactions(listOf(tx))
//
//        return StateRef(tx.id, 0)
//    }
//
//    @Test
//    fun flowReturnsTransactionSignedByTheInitiator() {
//        val stateRef = insertStandardIOUCreationTransaction()
//        val flow = IOUTransferFlow.Initiator(stateRef, c.info.legalIdentity)
//        val future = b.services.startFlow(flow).resultFuture
//        net.runNetwork(-1)
//
//        val signedTx = future.get()
//        signedTx.verifySignatures(a.info.legalIdentity.owningKey, c.info.legalIdentity.owningKey, DUMMY_NOTARY.owningKey)
//    }
//
//    @Test
//    fun onlyCurrentIOURecipientCanInitiateFlow() {
//        val stateRef = insertStandardIOUCreationTransaction()
//        val flow = IOUTransferFlow.Initiator(stateRef, b.info.legalIdentity)
//        val future = a.services.startFlow(flow).resultFuture
//
//        assertFailsWith<IllegalArgumentException> { future.getOrThrow() }
//    }
//
//    @Test
//    fun flowRejectsInvalidIOUStates() {
//        val state = IOUState(-1, a.info.legalIdentity, b.info.legalIdentity, IOUContract())
//        val stateRef = insertIOUCreationTransaction(state)
//        val flow = IOUTransferFlow . Initiator (stateRef, c.info.legalIdentity)
//        val future = b.services.startFlow(flow).resultFuture
//        net.runNetwork(-1)
//
//        assertFailsWith<TransactionVerificationException> { future.getOrThrow() }
//    }
//
//    @Test
//    fun flowReturnsTransactionSignedByTheOldRecipient() {
//        val stateRef = insertStandardIOUCreationTransaction()
//        val flow = IOUTransferFlow.Initiator(stateRef, c.info.legalIdentity)
//        val future = b.services.startFlow(flow).resultFuture
//        net.runNetwork(-1)
//
//        val signedTx = future.get()
//        signedTx.verifySignatures(a.info.legalIdentity.owningKey, c.info.legalIdentity.owningKey, DUMMY_NOTARY.owningKey)
//    }
//
//    @Test
//    fun flowReturnsTransactionSignedByTheNewRecipient() {
//        val stateRef = insertStandardIOUCreationTransaction()
//        val flow = IOUTransferFlow.Initiator(stateRef, c.info.legalIdentity)
//        val future = b.services.startFlow(flow).resultFuture
//        net.runNetwork(-1)
//
//        val signedTx = future.get()
//        signedTx.verifySignatures(a.info.legalIdentity.owningKey, b.info.legalIdentity.owningKey, DUMMY_NOTARY.owningKey)
//    }
//
//    @Test
//    fun flowRecordsATransactionInAllPartiesVaults() {
//        val stateRef = insertStandardIOUCreationTransaction()
//        val flow = IOUTransferFlow.Initiator(stateRef, c.info.legalIdentity)
//        val future = b.services.startFlow(flow).resultFuture
//        net.runNetwork(-1)
//        val signedTx = future.get()
//
//        for (node in listOf(a, b, c)) {
//            val recordedTxA = node.storage.validatedTransactions.getTransaction(signedTx.id)
//            assertEquals(signedTx.id, recordedTxA?.id)
//        }
//    }
//
//    @Test
//    fun recordedTransactionHasNoInputsAndASingleOutputTheInputIOU() {
//        val inputState = IOUState(1, a.info.legalIdentity, b.info.legalIdentity, IOUContract())
//        val stateRef = insertIOUCreationTransaction(inputState)
//        val flow = IOUTransferFlow.Initiator(stateRef, c.info.legalIdentity)
//        val future = b.services.startFlow(flow).resultFuture
//        net.runNetwork(-1)
//        val signedTx = future.get()
//
//        for (node in listOf(a, b, c)) {
//            val recordedTx = node.storage.validatedTransactions.getTransaction(signedTx.id)
//
//            // Test on the number of inputs.
//            val txInputs = recordedTx?.tx?.inputs
//            assert(txInputs?.size == 1)
//
//            // Test on the number of outputs.
//            val txOutputs = recordedTx?.tx?.outputs
//            assert(txOutputs?.size == 1)
//
//            // Tests on the output's attributes.
//            val outputState = txOutputs?.first()?.data as IOUState
//            assertEquals(inputState.iouValue, outputState.iouValue)
//            assertEquals(inputState.sender, outputState.sender)
//            assertEquals(inputState.linearId, outputState.linearId)
//            assertNotEquals(inputState.recipient, outputState.recipient)
//        }
//    }
}
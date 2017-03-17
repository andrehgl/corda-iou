package com.iou.kotlin.flows

import com.iou.kotlin.contract.IOUContract
import com.iou.kotlin.flow.IOUFlow
import com.iou.kotlin.state.IOUState
import net.corda.core.contracts.TransactionVerificationException
import net.corda.core.getOrThrow
import net.corda.core.utilities.DUMMY_NOTARY
import net.corda.testing.node.MockNetwork
import net.corda.core.transactions.SignedTransaction
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class IOUFlowTests {
    lateinit var net: MockNetwork
    lateinit var a: MockNetwork.MockNode
    lateinit var b: MockNetwork.MockNode

    @Before
    fun setup() {
        net = MockNetwork()
        val nodes = net.createSomeNodes(2)
        a = nodes.partyNodes[0]
        b = nodes.partyNodes[1]
        net.runNetwork()
    }

    @After
    fun tearDown() {
        net.stopNodes()
    }

//    @Test
//    fun flowReturnsTransactionSignedByTheInitiator() {
//        val state = IOUState(
//                1,
//                a.info.legalIdentity,
//                b.info.legalIdentity,
//                IOUContract())
//        val flow = IOUFlow.Initiator(state, b.info.legalIdentity)
//        val future = a.services.startFlow(flow).resultFuture
//        net.runNetwork()
//
//        val signedTx: SignedTransaction = future.getOrThrow()
//        signedTx.verifySignatures(b.info.legalIdentity.owningKey, DUMMY_NOTARY.owningKey)
//    }
//
//    @Test
//    fun flowRejectsInvalidIOUStates() {
//        // The IOUContract specifies that an IOU cannot have a negative value.
//        val state = IOUState(
//                -1,
//                a.info.legalIdentity,
//                b.info.legalIdentity,
//                IOUContract())
//        val flow = IOUFlow.Initiator(state, b.info.legalIdentity)
//        val future = a.services.startFlow(flow).resultFuture
//        net.runNetwork()
//
//        assertFailsWith<TransactionVerificationException> { future.getOrThrow() }
//    }
//
//    @Test
//    fun flowReturnsTransactionSignedByTheAcceptor() {
//        val state = IOUState(
//                1,
//                a.info.legalIdentity,
//                b.info.legalIdentity,
//                IOUContract())
//        val flow = IOUFlow.Initiator(state, b.info.legalIdentity)
//        val future = a.services.startFlow(flow).resultFuture
//        net.runNetwork()
//
//        val signedTx = future.getOrThrow()
//        signedTx.verifySignatures(a.info.legalIdentity.owningKey, DUMMY_NOTARY.owningKey)
//    }
//
//    @Test
//    fun flowRecordsATransactionInBothPartiesVaults() {
//        val state = IOUState(
//                1,
//                a.info.legalIdentity,
//                b.info.legalIdentity,
//                IOUContract())
//        val flow = IOUFlow.Initiator(state, b.info.legalIdentity)
//        val future = a.services.startFlow(flow).resultFuture
//        net.runNetwork()
//        val signedTx = future.getOrThrow()
//
//        for (node in listOf(a, b)) {
//            val recordedTxA = node.storage.validatedTransactions.getTransaction(signedTx.id)
//            assertEquals(signedTx.id, recordedTxA!!.id)
//        }
//    }
//
//    @Test
//    fun recordedTransactionHasNoInputsAndASingleOutputTheInputIOU() {
//        val inputState = IOUState(
//                1,
//                a.info.legalIdentity,
//                b.info.legalIdentity,
//                IOUContract())
//        val flow = IOUFlow.Initiator(inputState, b.info.legalIdentity)
//        val future = a.services.startFlow(flow).resultFuture
//        net.runNetwork()
//        val signedTx = future.getOrThrow()
//
//        for (node in listOf(a, b)) {
//            val recordedTx = node.storage.validatedTransactions.getTransaction(signedTx.id)
//            val txOutputs = recordedTx!!.tx.outputs
//            assert(txOutputs.size == 1)
//
//            val recordedState = txOutputs.first().data as IOUState
//            assertEquals(recordedState.iouValue, inputState.iouValue)
//            assertEquals(recordedState.sender, inputState.sender)
//            assertEquals(recordedState.recipient, inputState.recipient)
//            assertEquals(recordedState.linearId, inputState.linearId)
//        }
//    }
}
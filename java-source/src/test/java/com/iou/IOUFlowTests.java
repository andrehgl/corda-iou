package com.iou;

import com.google.common.util.concurrent.ListenableFuture;
import com.iou.contract.IOUContract;
import com.iou.flow.IOUFlow;
import com.iou.state.IOUState;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.TransactionState;
import net.corda.core.contracts.TransactionVerificationException;
import net.corda.core.crypto.CryptoUtilities;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.TestConstants;
import net.corda.testing.node.MockNetwork;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class IOUFlowTests {
    private MockNetwork net;
    private MockNetwork.MockNode a;
    private MockNetwork.MockNode b;

    @Before
    public void setup() {
        net = new MockNetwork();
        MockNetwork.BasketOfNodes nodes = net.createSomeNodes(
                2,
                MockNetwork.DefaultFactory.INSTANCE,
                TestConstants.getDUMMY_NOTARY_KEY());
        a = nodes.getPartyNodes().get(0);
        b = nodes.getPartyNodes().get(1);
        net.runNetwork(-1);
    }

    @After
    public void tearDown() {
        net.stopNodes();
    }

//    @Test
//    public void flowReturnsTransactionSignedByTheInitiator() throws Exception {
//        IOUState state = new IOUState(
//                1,
//                a.info.getLegalIdentity(),
//                b.info.getLegalIdentity(),
//                new IOUContract());
//        IOUFlow.Initiator flow = new IOUFlow.Initiator(state, b.info.getLegalIdentity());
//        ListenableFuture<SignedTransaction> future = a.getServices().startFlow(flow).getResultFuture();
//        net.runNetwork(-1);
//
//        SignedTransaction signedTx = future.get();
//        signedTx.verifySignatures(CryptoUtilities.getComposite(b.getServices().getLegalIdentityKey().getPublic()));
//    }
//
//    @Test
//    public void flowRejectsInvalidIOUStates() throws InterruptedException {
//        IOUState state = new IOUState(
//                -1,
//                a.info.getLegalIdentity(),
//                b.info.getLegalIdentity(),
//                new IOUContract());
//        IOUFlow.Initiator flow = new IOUFlow.Initiator(state, b.info.getLegalIdentity());
//        ListenableFuture<SignedTransaction> future = a.getServices().startFlow(flow).getResultFuture();
//        net.runNetwork(-1);
//
//        // The IOUContract specifies that an IOU's value cannot be negative.
//        try {
//            future.get();
//            fail();
//        } catch (ExecutionException e) {
//            assertTrue(e.getCause() instanceof TransactionVerificationException.ContractRejection);
//        }
//    }
//
//    @Test
//    public void flowReturnsTransactionSignedByTheAcceptor() throws Exception {
//        IOUState state = new IOUState(
//                1,
//                a.info.getLegalIdentity(),
//                b.info.getLegalIdentity(),
//                new IOUContract());
//        IOUFlow.Initiator flow = new IOUFlow.Initiator(state, b.info.getLegalIdentity());
//        ListenableFuture<SignedTransaction> future = a.getServices().startFlow(flow).getResultFuture();
//        net.runNetwork(-1);
//
//        SignedTransaction signedTx = future.get();
//        signedTx.verifySignatures(CryptoUtilities.getComposite(a.getServices().getLegalIdentityKey().getPublic()));
//    }
//
//    @Test
//    public void flowRecordsATransactionInBothPartiesVaults() throws Exception {
//        IOUState state = new IOUState(
//                1,
//                a.info.getLegalIdentity(),
//                b.info.getLegalIdentity(),
//                new IOUContract());
//        IOUFlow.Initiator flow = new IOUFlow.Initiator(state, b.info.getLegalIdentity());
//        ListenableFuture<SignedTransaction> future = a.getServices().startFlow(flow).getResultFuture();
//        net.runNetwork(-1);
//        SignedTransaction signedTx = future.get();
//
//        // Checks on A's vault.
//        SignedTransaction recordedTxA = a.storage.getValidatedTransactions().getTransaction(signedTx.getId());
//        assertEquals(signedTx.getId(), recordedTxA.getId());
//
//        // Checks on B's vault.
//        SignedTransaction recordedTxB = b.storage.getValidatedTransactions().getTransaction(signedTx.getId());
//        assertEquals(signedTx.getId(), recordedTxB.getId());
//    }
//
//    @Test
//    public void recordedTransactionHasNoInputsAndASingleOutputTheInputIOU() throws Exception {
//        IOUState inputState = new IOUState(
//                1,
//                a.info.getLegalIdentity(),
//                b.info.getLegalIdentity(),
//                new IOUContract());
//        IOUFlow.Initiator flow = new IOUFlow.Initiator(inputState, b.info.getLegalIdentity());
//        ListenableFuture<SignedTransaction> future = a.getServices().startFlow(flow).getResultFuture();
//        net.runNetwork(-1);
//        SignedTransaction signedTx = future.get();
//
//        // Checks on A's vault.
//        SignedTransaction recordedTxA = a.storage.getValidatedTransactions().getTransaction(signedTx.getId());
//        List<TransactionState<ContractState>> txOutputsA = recordedTxA.getTx().getOutputs();
//        assert (txOutputsA.size() == 1);
//
//        IOUState recordedStateA = (IOUState) txOutputsA.get(0).getData();
//        assertEquals(recordedStateA.getIOUValue(), inputState.getIOUValue());
//        assertEquals(recordedStateA.getSender(), inputState.getSender());
//        assertEquals(recordedStateA.getRecipient(), inputState.getRecipient());
//        assertEquals(recordedStateA.getLinearId(), inputState.getLinearId());
//
//        // Checks on B's vault.
//        SignedTransaction recordedTxB = b.storage.getValidatedTransactions().getTransaction(signedTx.getId());
//        List<TransactionState<ContractState>> txOutputsB = recordedTxB.getTx().getOutputs();
//        assert (txOutputsB.size() == 1);
//
//        IOUState recordedStateB = (IOUState) txOutputsB.get(0).getData();
//        assertEquals(recordedStateB.getIOUValue(), inputState.getIOUValue());
//        assertEquals(recordedStateB.getSender(), inputState.getSender());
//        assertEquals(recordedStateB.getRecipient(), inputState.getRecipient());
//        assertEquals(recordedStateB.getLinearId(), inputState.getLinearId());
//    }
}
package com.iou;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.iou.contract.IOUContract;
import com.iou.flow.IOUFlow;
import com.iou.state.IOUState;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.TransactionState;
import net.corda.core.contracts.TransactionVerificationException;
import net.corda.core.crypto.CryptoUtilities;
import net.corda.core.flows.FlowException;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.TestConstants;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.MockNetwork.MockNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;


public class IOUFlowTests {
    private MockNetwork net;
    private MockNode a;
    private MockNode b;

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

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void flowReturnsTransactionSignedByTheInitiator() throws Exception {
        IOUState state = new IOUState(
                1,
                a.info.getLegalIdentity(),
                b.info.getLegalIdentity(),
                new IOUContract());
        IOUFlow.Initiator flow = new IOUFlow.Initiator(state, b.info.getLegalIdentity());
        ListenableFuture<SignedTransaction> future = a.getServices().startFlow(flow).getResultFuture();
        net.runNetwork(-1);

        SignedTransaction signedTx = future.get();
        signedTx.verifySignatures(CryptoUtilities.getComposite(b.getServices().getLegalIdentityKey().getPublic()));
    }

    @Test
    public void flowRejectsInvalidIOUStates() throws Exception {
        // The IOUContract specifies that an IOU's sender and recipient cannot be the same.
        IOUState state = new IOUState(
                -1,
                a.info.getLegalIdentity(),
                a.info.getLegalIdentity(),
                new IOUContract());

        IOUFlow.Initiator flow = new IOUFlow.Initiator(state, b.info.getLegalIdentity());
        ListenableFuture<SignedTransaction> future = a.getServices().startFlow(flow).getResultFuture();
        net.runNetwork(-1);

        exception.expectCause(instanceOf(TransactionVerificationException.class));
        future.get();
    }

    @Test
    public void flowReturnsTransactionSignedByTheAcceptor() throws Exception {
        IOUState state = new IOUState(
                1,
                a.info.getLegalIdentity(),
                b.info.getLegalIdentity(),
                new IOUCont
        IOUFlow.Initiator flow = new IOUFlow.Initiator(state, b.info.getLegalIdentity());
        ListenableFuture<SignedTransaction> future = a.getServices().startFlow(flow).getResultFuture();
        net.runNetwork(-1);

        SignedTransaction signedTx = future.get();
        signedTx.verifySignatures(CryptoUtilities.getComposite(a.getServices().getLegalIdentityKey().getPublic()));
    }

    @Test
    public void flowRecordsATransactionInBothPartiesVaults() throws Exception {
        IOUState state = new IOUState(
                1,
                a.info.getLegalIdentity(),
                b.info.getLegalIdentity(),
                new IOUContract());
        IOUFlow.Initiator flow = new IOUFlow.Initiator(state, b.info.getLegalIdentity());
        ListenableFuture<SignedTransaction> future = a.getServices().startFlow(flow).getResultFuture();
        net.runNetwork(-1);
        SignedTransaction signedTx = future.get();

        for (MockNode node : ImmutableList.of(a, b)) {
            SignedTransaction recordedTxA = node.storage.getValidatedTransactions().getTransaction(signedTx.getId());
            assertEquals(signedTx.getId(), recordedTxA.getId());
        }
    }

    @Test
    public void recordedTransactionHasNoInputsAndASingleOutputTheInputIOU() throws Exception {
        IOUState inputState = new IOUState(
                1,
                a.info.getLegalIdentity(),
                b.info.getLegalIdentity(),
                new IOUContract());
        IOUFlow.Initiator flow = new IOUFlow.Initiator(inputState, b.info.getLegalIdentity());
        ListenableFuture<SignedTransaction> future = a.getServices().startFlow(flow).getResultFuture();
        net.runNetwork(-1);
        SignedTransaction signedTx = future.get();

        for (MockNode node : ImmutableList.of(a, b)) {
            SignedTransaction recordedTx = node.storage.getValidatedTransactions().getTransaction(signedTx.getId());
            List<TransactionState<ContractState>> txOutputs = recordedTx.getTx().getOutputs();
            assert(txOutputs.size() == 1);

            IOUState recordedState = (IOUState) txOutputs.get(0).getData();
            assertEquals(recordedState.getIOUValue(), inputState.getIOUValue());
            assertEquals(recordedState.getSender(), inputState.getSender());
            assertEquals(recordedState.getRecipient(), inputState.getRecipient());
            assertEquals(recordedState.getLinearId(), inputState.getLinearId());
        }
    }
}
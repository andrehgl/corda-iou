package com.iou.java.flows;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.iou.java.contract.IOUContract;
import com.iou.java.state.IOUState;
import com.iou.java.flow.IOUTransferFlow;
import net.corda.core.contracts.*;
import net.corda.core.crypto.CompositeKey;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.MockNetwork.MockNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static net.corda.core.utilities.TestConstants.getDUMMY_NOTARY;
import static net.corda.core.utilities.TestConstants.getDUMMY_NOTARY_KEY;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.jgroups.util.Util.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class IOUFlowTestsExtra {
    private MockNetwork net;
    private MockNode a;
    private MockNode b;
    private MockNode c;

    @Before
    public void setup() {
        net = new MockNetwork();
        MockNetwork.BasketOfNodes nodes = net.createSomeNodes(
                3,
                MockNetwork.DefaultFactory.INSTANCE,
                getDUMMY_NOTARY_KEY());
        a = nodes.getPartyNodes().get(0);
        b = nodes.getPartyNodes().get(1);
        c = nodes.getPartyNodes().get(2);
        net.runNetwork(-1);
    }

    @After
    public void tearDown() {
        net.stopNodes();
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private StateRef insertStandardIOUCreationTransaction() {
        IOUState state = new IOUState(
                1,
                a.info.getLegalIdentity(),
                b.info.getLegalIdentity(),
                new IOUContract(),
                new UniqueIdentifier());

        return insertIOUCreationTransaction(state);
    }

    private StateRef insertIOUCreationTransaction(IOUState state) {
        final List<CompositeKey> keysAB = ImmutableList.of(
                a.info.getLegalIdentity().getOwningKey(),
                b.info.getLegalIdentity().getOwningKey());
        final Command txCommand = new Command(new IOUContract.Create(), keysAB);

        final TransactionBuilder tb =  new TransactionType.General
                .Builder(getDUMMY_NOTARY())
                .withItems(state, txCommand);

        final SignedTransaction tx = tb
                .signWith(a.getServices().getLegalIdentityKey())
                .signWith(b.getServices().getLegalIdentityKey())
                .signWith(getDUMMY_NOTARY_KEY())
                .toSignedTransaction(true);

        for (MockNode node : ImmutableList.of(a, b)) {
            node.getServices().recordTransactions(ImmutableList.of(tx));
        }

        return new StateRef(tx.getId(), 0);
    }

    @Test
    public void flowReturnsTransactionSignedByTheInitiator() throws Exception {
        final StateRef stateRef = insertStandardIOUCreationTransaction();
        IOUTransferFlow.Initiator flow = new IOUTransferFlow.Initiator(stateRef, c.info.getLegalIdentity());
        ListenableFuture<SignedTransaction> future = b.getServices().startFlow(flow).getResultFuture();
        net.runNetwork(-1);

        SignedTransaction signedTx = future.get();
        signedTx.verifySignatures(a.info.getLegalIdentity().getOwningKey(), c.info.getLegalIdentity().getOwningKey(), getDUMMY_NOTARY().getOwningKey());
    }

    @Test
    public void onlyCurrentIOURecipientCanInitiateFlow() throws Exception {
        final StateRef stateRef = insertStandardIOUCreationTransaction();
        IOUTransferFlow.Initiator flow = new IOUTransferFlow.Initiator(stateRef, b.info.getLegalIdentity());
        ListenableFuture<SignedTransaction> future = a.getServices().startFlow(flow).getResultFuture();

        exception.expectCause(instanceOf(IllegalArgumentException.class));
        future.get();
    }

    @Test
    public void flowRejectsInvalidIOUStates() throws Exception {
        IOUState state = new IOUState(
                -1,
                a.info.getLegalIdentity(),
                b.info.getLegalIdentity(),
                new IOUContract(),
                new UniqueIdentifier());
        final StateRef stateRef = insertIOUCreationTransaction(state);
        IOUTransferFlow.Initiator flow = new IOUTransferFlow.Initiator(stateRef, c.info.getLegalIdentity());
        ListenableFuture<SignedTransaction> future = b.getServices().startFlow(flow).getResultFuture();
        net.runNetwork(-1);

        exception.expectCause(instanceOf(TransactionVerificationException.class));
        future.get();
    }

    @Test
    public void flowReturnsTransactionSignedByTheOldRecipient() throws Exception {
        final StateRef stateRef = insertStandardIOUCreationTransaction();
        IOUTransferFlow.Initiator flow = new IOUTransferFlow.Initiator(stateRef, c.info.getLegalIdentity());
        ListenableFuture<SignedTransaction> future = b.getServices().startFlow(flow).getResultFuture();
        net.runNetwork(-1);

        SignedTransaction signedTx = future.get();
        signedTx.verifySignatures(a.info.getLegalIdentity().getOwningKey(), c.info.getLegalIdentity().getOwningKey(), getDUMMY_NOTARY().getOwningKey());
    }

    @Test
    public void flowReturnsTransactionSignedByTheNewRecipient() throws Exception {
        final StateRef stateRef = insertStandardIOUCreationTransaction();
        IOUTransferFlow.Initiator flow = new IOUTransferFlow.Initiator(stateRef, c.info.getLegalIdentity());
        ListenableFuture<SignedTransaction> future = b.getServices().startFlow(flow).getResultFuture();
        net.runNetwork(-1);

        SignedTransaction signedTx = future.get();
        signedTx.verifySignatures(a.info.getLegalIdentity().getOwningKey(), b.info.getLegalIdentity().getOwningKey(), getDUMMY_NOTARY().getOwningKey());
    }

    @Test
    public void flowRecordsATransactionInAllPartiesVaults() throws Exception {
        final StateRef stateRef = insertStandardIOUCreationTransaction();
        IOUTransferFlow.Initiator flow = new IOUTransferFlow.Initiator(stateRef, c.info.getLegalIdentity());
        ListenableFuture<SignedTransaction> future = b.getServices().startFlow(flow).getResultFuture();
        net.runNetwork(-1);
        SignedTransaction signedTx = future.get();

        for (MockNode node : ImmutableList.of(a, b, c)) {
            SignedTransaction recordedTxA = node.storage.getValidatedTransactions().getTransaction(signedTx.getId());
            assertEquals(signedTx.getId(), recordedTxA.getId());
        }
    }

    @Test
    public void recordedTransactionHasNoInputsAndASingleOutputTheInputIOU() throws Exception {
        final IOUState inputState = new IOUState(
                1,
                a.info.getLegalIdentity(),
                b.info.getLegalIdentity(),
                new IOUContract(),
                new UniqueIdentifier());
        final StateRef stateRef = insertIOUCreationTransaction(inputState);
        IOUTransferFlow.Initiator flow = new IOUTransferFlow.Initiator(stateRef, c.info.getLegalIdentity());
        ListenableFuture<SignedTransaction> future = b.getServices().startFlow(flow).getResultFuture();
        net.runNetwork(-1);
        SignedTransaction signedTx = future.get();

        for (MockNode node : ImmutableList.of(a, b, c)) {
            final SignedTransaction recordedTx = node.storage.getValidatedTransactions().getTransaction(signedTx.getId());

            // Test on the number of inputs.
            final List<StateRef> txInputs = recordedTx.getTx().getInputs();
            assert(txInputs.size() == 1);

            // Test on the number of outputs.
            List<TransactionState<ContractState>> txOutputs = recordedTx.getTx().getOutputs();
            assert(txOutputs.size() == 1);

            // Tests on the output's attributes.
            IOUState outputState = (IOUState) txOutputs.get(0).getData();
            assertEquals(inputState.getIOUValue(), outputState.getIOUValue());
            assertEquals(inputState.getSender(), outputState.getSender());
            assertEquals(inputState.getLinearId(), outputState.getLinearId());
            assertNotEquals(inputState.getRecipient(), outputState.getRecipient());
        }
    }
}
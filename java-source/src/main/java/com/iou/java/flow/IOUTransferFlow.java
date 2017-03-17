package com.iou.java.flow;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.iou.java.contract.IOUContract;
import com.iou.java.state.IOUState;
import net.corda.core.contracts.*;
import net.corda.core.crypto.CompositeKey;
import net.corda.core.crypto.DigitalSignature;
import net.corda.core.crypto.Party;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.flows.FinalityFlow;

import java.io.FileNotFoundException;
import java.security.KeyPair;
import java.util.List;
import java.util.Set;

import static kotlin.collections.CollectionsKt.single;

public class IOUTransferFlow {
    public static class Initiator extends FlowLogic<SignedTransaction> {
        private final StateRef stateRef;
        private final Party newRecipient;

        public Initiator(StateRef stateRef, Party newRecipient) {
            this.stateRef = stateRef;
            this.newRecipient = newRecipient;
        }

        @Suspendable
        public SignedTransaction call() throws FlowException {
            return null;
        }
    }
}
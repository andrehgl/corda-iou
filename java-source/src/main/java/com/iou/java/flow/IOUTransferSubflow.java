package com.iou.java.flow;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableSet;
import com.iou.java.state.IOUState;
import net.corda.core.contracts.TransactionResolutionException;
import net.corda.core.crypto.CompositeKey;
import net.corda.core.crypto.DigitalSignature;
import net.corda.core.crypto.Party;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.transactions.SignedTransaction;
import net.corda.flows.ResolveTransactionsFlow;

import java.io.FileNotFoundException;
import java.security.SignatureException;
import java.util.Set;

import static kotlin.collections.CollectionsKt.single;

public class IOUTransferSubflow {
    public static class Initiator extends FlowLogic<DigitalSignature.WithKey> {
        private final SignedTransaction stx;
        private final Party otherParty;

        public Initiator(SignedTransaction stx, Party otherParty) {
            this.stx = stx;
            this.otherParty = otherParty;
        }

        @Suspendable
        public DigitalSignature.WithKey call() throws FlowException {
            return null;
        }
    }

    public static class Acceptor extends FlowLogic<Void> {
        private final Party otherParty;

        public Acceptor(Party otherParty) {
            this.otherParty = otherParty;
        }

        @Suspendable
        @Override
        public Void call() throws FlowException {
            return null;
        }
    }
}
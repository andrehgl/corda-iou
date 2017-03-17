package com.iou.java.flow;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableSet;
import com.iou.java.contract.IOUContract;
import com.iou.java.state.IOUState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.TransactionType;
import net.corda.core.crypto.CompositeKey;
import net.corda.core.crypto.CryptoUtilities;
import net.corda.core.crypto.DigitalSignature;
import net.corda.core.crypto.Party;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.transactions.WireTransaction;
import net.corda.flows.FinalityFlow;

import java.security.KeyPair;
import java.security.SignatureException;
import java.util.Set;

import static kotlin.collections.CollectionsKt.single;

public class IOUFlow {
    public static class Initiator extends FlowLogic<SignedTransaction> {
        private final IOUState iou;
        private final Party otherParty;

        public Initiator(IOUState iou, Party otherParty) {
            this.iou = iou;
            this.otherParty = otherParty;
        }

        @Suspendable
        public SignedTransaction call() {
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
        public Void call() {
            return null;
        }
    }
}
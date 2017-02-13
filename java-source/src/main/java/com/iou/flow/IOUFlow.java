package com.iou.flow;

import co.paralleluniverse.fibers.Suspendable;
import com.iou.state.IOUState;
import net.corda.core.crypto.Party;
import net.corda.core.flows.FlowLogic;
import net.corda.core.transactions.SignedTransaction;

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
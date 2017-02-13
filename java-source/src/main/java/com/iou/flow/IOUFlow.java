package com.iou.flow;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.crypto.Party;
import net.corda.core.flows.FlowLogic;

public class IOUFlow {
    public static class Initiator {

        @Suspendable
        public Void call() {
            return null;
        }
    }

    public static class Acceptor extends FlowLogic<Void> {

        public Acceptor(Party otherParty) {}

        @Suspendable
        @Override
        public Void call() {
            return null;
        }
    }
}
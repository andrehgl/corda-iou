package com.iou.java.transactions;

import com.iou.java.contract.IOUContract;
import com.iou.java.state.IOUState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.CompositeKey;
import net.corda.core.crypto.Party;
import org.junit.BeforeClass;
import org.junit.Test;

import static net.corda.testing.CoreTestUtils.*;

public class IOUTransactionTestsExtra {
    static private final Party alice = getALICE();
    static private final Party bob = getBOB();
    static private final Party charlie = getCHARLIE();

    static private final CompositeKey[] keysAB = new CompositeKey[2];
    static private final CompositeKey[] keysABC = new CompositeKey[3];

    @BeforeClass
    public static void setUpClass() {
        keysAB[0] = getALICE_PUBKEY();
        keysAB[1] = getBOB_PUBKEY();

        keysABC[0] = getALICE_PUBKEY();
        keysABC[1] = getBOB_PUBKEY();
        keysABC[2] = getCHARLIE_PUBKEY();
    }

//    @Test
//    public void mustIncludeCommand() {
//        ledger(ledgerDSL -> {
//            ledgerDSL.transaction(txDSL -> {
//                txDSL.input(new IOUState(1, alice, bob, new IOUContract(), new UniqueIdentifier()));
//                txDSL.output(new IOUState(1, alice, charlie, new IOUContract(), new UniqueIdentifier()));
//                txDSL.fails();
//                return null;
//            });
//            return null;
//        });
//    }
//
//    @Test
//    public void cannotCreateNegativeValueIOUs() {
//        ledger(ledgerDSL -> {
//            ledgerDSL.transaction(txDSL -> {
//                txDSL.input(new IOUState(1, alice, bob, new IOUContract(), new UniqueIdentifier()));
//                txDSL.output(new IOUState(-1, alice, charlie, new IOUContract(), new UniqueIdentifier()));
//                txDSL.command(keysABC, IOUContract.Transfer::new);
//                txDSL.failsWith("The IOU's value must be non-negative.");
//                return null;
//            });
//            return null;
//        });
//    }
//
//    @Test
//    public void transferTransactionMustHaveOneInput() {
//        ledger(ledgerDSL -> {
//            ledgerDSL.transaction(txDSL -> {
//                txDSL.output(new IOUState(1, alice, charlie, new IOUContract(), new UniqueIdentifier()));
//                txDSL.command(keysABC, IOUContract.Transfer::new);
//                txDSL.failsWith("One input should be consumed when transferring an IOU.");
//                return null;
//            });
//
//            return null;
//        });
//    }
//
//    @Test
//    public void transferTransactionMustHaveOneOutput() {
//        ledger(ledgerDSL -> {
//            ledgerDSL.transaction(txDSL -> {
//                txDSL.input(new IOUState(1, alice, bob, new IOUContract(), new UniqueIdentifier()));
//                txDSL.output(new IOUState(1, alice, charlie, new IOUContract(), new UniqueIdentifier()));
//                txDSL.output(new IOUState(1, alice, charlie, new IOUContract(), new UniqueIdentifier()));
//                txDSL.command(keysABC, IOUContract.Transfer::new);
//                txDSL.failsWith("Failed requirement: Only one output state should be created.");
//                return null;
//            });
//            return null;
//        });
//    }
//
//    @Test
//    public void transferTransactionDoesNotChangeIOUValue() {
//        ledger(ledgerDSL -> {
//            ledgerDSL.transaction(txDSL -> {
//                txDSL.input(new IOUState(1, alice, bob, new IOUContract(), new UniqueIdentifier()));
//                txDSL.output(new IOUState(2, alice, charlie, new IOUContract(), new UniqueIdentifier()));
//                txDSL.command(keysABC, IOUContract.Transfer::new);
//                txDSL.failsWith("The input and output IOUs must have the same value.");
//                return null;
//            });
//            return null;
//        });
//    }
//
//    @Test
//    public void transferTransactionDoesNotChangeIOUSender() {
//        ledger(ledgerDSL -> {
//            ledgerDSL.transaction(txDSL -> {
//                txDSL.input(new IOUState(1, alice, bob, new IOUContract(), new UniqueIdentifier()));
//                txDSL.output(new IOUState(1, bob, charlie, new IOUContract(), new UniqueIdentifier()));
//                txDSL.command(keysABC, IOUContract.Transfer::new);
//                txDSL.failsWith("The input and output IOUs must have the same sender.");
//                return null;
//            });
//            return null;
//        });
//    }
//
//    @Test
//    public void transferTransactionChangesIOURecipient() {
//        ledger(ledgerDSL -> {
//            ledgerDSL.transaction(txDSL -> {
//                txDSL.input(new IOUState(1, alice, bob, new IOUContract(), new UniqueIdentifier()));
//                txDSL.output(new IOUState(1, alice, bob, new IOUContract(), new UniqueIdentifier()));
//                txDSL.command(keysABC, IOUContract.Transfer::new);
//                txDSL.failsWith("The input and output IOUs must have different recipients.");
//                return null;
//            });
//            return null;
//        });
//    }
//
//    @Test
//    public void senderMustSignCreateTransaction() {
//        ledger(ledgerDSL -> {
//            ledgerDSL.transaction(txDSL -> {
//                txDSL.input(new IOUState(1, alice, bob, new IOUContract(), new UniqueIdentifier()));
//                txDSL.output(new IOUState(1, alice, charlie, new IOUContract(), new UniqueIdentifier()));
//                CompositeKey[] keys = new CompositeKey[2];
//                keys[0] = getBOB_PUBKEY();
//                keys[1] = getCHARLIE_PUBKEY();
//                txDSL.command(keys, IOUContract.Transfer::new);
//                txDSL.failsWith("All of the participants must be signers.");
//                return null;
//            });
//            return null;
//        });
//    }
//
//    @Test
//    public void oldRecipientMustSignCreateTransaction() {
//        ledger(ledgerDSL -> {
//            ledgerDSL.transaction(txDSL -> {
//                txDSL.input(new IOUState(1, alice, bob, new IOUContract(), new UniqueIdentifier()));
//                txDSL.output(new IOUState(1, alice, charlie, new IOUContract(), new UniqueIdentifier()));
//                CompositeKey[] keys = new CompositeKey[2];
//                keys[0] = getALICE_PUBKEY();
//                keys[1] = getCHARLIE_PUBKEY();
//                txDSL.command(keys, IOUContract.Transfer::new);
//                txDSL.failsWith("All of the participants must be signers.");
//                return null;
//            });
//            return null;
//        });
//    }
//
//    @Test
//    public void newRecipientMustSignCreateTransaction() {
//        ledger(ledgerDSL -> {
//            ledgerDSL.transaction(txDSL -> {
//                txDSL.input(new IOUState(1, alice, bob, new IOUContract(), new UniqueIdentifier()));
//                txDSL.output(new IOUState(1, alice, charlie, new IOUContract(), new UniqueIdentifier()));
//                CompositeKey[] keys = new CompositeKey[2];
//                keys[0] = getALICE_PUBKEY();
//                keys[1] = getBOB_PUBKEY();
//                txDSL.command(keys, IOUContract.Transfer::new);
//                txDSL.failsWith("All of the participants must be signers.");
//                return null;
//            });
//            return null;
//        });
//    }
}
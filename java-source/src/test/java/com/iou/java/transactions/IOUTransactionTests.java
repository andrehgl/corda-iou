package com.iou.java.transactions;

import com.iou.java.contract.IOUContract;
import com.iou.java.state.IOUState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.CompositeKey;
import net.corda.core.crypto.Party;
import org.junit.BeforeClass;
import org.junit.Test;

import static net.corda.testing.CoreTestUtils.*;

public class IOUTransactionTests {
    static private final Party miniCorp = getMINI_CORP();
    static private final Party megaCorp = getMEGA_CORP();
    static private final CompositeKey[] keys = new CompositeKey[2];

    @BeforeClass
    public static void setUpClass() {
        keys[0] = getMINI_CORP_PUBKEY();
        keys[1] = getMEGA_CORP_PUBKEY();
    }

//    @Test
//    public void mustIncludeCommand() {
//        ledger(ledgerDSL -> {
//            ledgerDSL.transaction(txDSL -> {
//                txDSL.output(new IOUState(1, miniCorp, megaCorp, new IOUContract(), new UniqueIdentifier()));
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
//                txDSL.output(new IOUState(-1, miniCorp, megaCorp, new IOUContract(), new UniqueIdentifier()));
//                txDSL.command(keys, IOUContract.Create::new);
//                txDSL.failsWith("The IOU's value must be non-negative.");
//                return null;
//            });
//            return null;
//        });
//    }
//
//    @Test
//    public void createTransactionMustHaveNoInputs() {
//        ledger(ledgerDSL -> {
//            ledgerDSL.transaction(txDSL -> {
//                txDSL.input(new IOUState(1, miniCorp, megaCorp, new IOUContract(), new UniqueIdentifier()));
//                txDSL.output(new IOUState(1, miniCorp, megaCorp, new IOUContract(), new UniqueIdentifier()));
//                txDSL.command(keys, IOUContract.Create::new);
//                txDSL.failsWith("No inputs should be consumed when issuing an IOU.");
//                return null;
//            });
//            return null;
//        });
//    }
//
//    @Test
//    public void createTransactionMustHaveOneOutput() {
//        ledger(ledgerDSL -> {
//            ledgerDSL.transaction(txDSL -> {
//                txDSL.output(new IOUState(1, miniCorp, megaCorp, new IOUContract(), new UniqueIdentifier()));
//                txDSL.output(new IOUState(1, miniCorp, megaCorp, new IOUContract(), new UniqueIdentifier()));
//                txDSL.command(keys, IOUContract.Create::new);
//                txDSL.failsWith("Only one output state should be created.");
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
//                txDSL.output(new IOUState(1, miniCorp, megaCorp, new IOUContract(), new UniqueIdentifier()));
//                CompositeKey[] keys = new CompositeKey[1];
//                keys[0] = getMEGA_CORP_PUBKEY();
//                txDSL.command(keys, IOUContract.Create::new);
//                txDSL.failsWith("All of the participants must be signers.");
//                return null;
//            });
//            return null;
//        });
//    }
//
//    @Test
//    public void recipientMustSignCreateTransaction() {
//        ledger(ledgerDSL -> {
//            ledgerDSL.transaction(txDSL -> {
//                txDSL.output(new IOUState(1, miniCorp, megaCorp, new IOUContract(), new UniqueIdentifier()));
//                CompositeKey[] keys = new CompositeKey[1];
//                keys[0] = getMINI_CORP_PUBKEY();
//                txDSL.command(keys, IOUContract.Create::new);
//                txDSL.failsWith("All of the participants must be signers.");
//                return null;
//            });
//            return null;
//        });
//    }
}
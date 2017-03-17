package com.iou.java.states;

import com.google.common.collect.ImmutableSet;
import com.iou.java.contract.IOUContract;
import com.iou.java.state.IOUState;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.CompositeKey;
import net.corda.core.crypto.Party;
import org.junit.Test;

import static net.corda.testing.CoreTestUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class IOUStateTests {
    static private final Party miniCorp = getMINI_CORP();
    static private final Party megaCorp = getMEGA_CORP();
    static private final CompositeKey miniCorpKey = getMINI_CORP_PUBKEY();
    static private final CompositeKey megaCorpKey = getMEGA_CORP_PUBKEY();

//    @Test
//    public void hasIOUValueFieldOfCorrectType() throws NoSuchFieldException {
//        // Does the iouValue field exist?
//        IOUState.class.getDeclaredField("iouValue");
//        // Is the iouValue field of the correct type?
//        assertEquals(IOUState.class.getDeclaredField("iouValue").getType(), Integer.class);
//    }
//
//    @Test
//    public void hasSenderFieldOfCorrectType() throws NoSuchFieldException {
//        // Does the sender field exist?
//        IOUState.class.getDeclaredField("sender");
//        // Is the sender field of the correct type?
//        assertEquals(IOUState.class.getDeclaredField("sender").getType(), Party.class);
//    }
//
//    @Test
//    public void hasRecipientFieldOfCorrectType() throws NoSuchFieldException {
//        // Does the recipient field exist?
//        IOUState.class.getDeclaredField("recipient");
//        // Is the recipient field of the correct type?
//        assertEquals(IOUState.class.getDeclaredField("recipient").getType(), Party.class);
//    }
//
//    @Test
//    public void senderIsParticipant() {
//        IOUState iouState = new IOUState(1, miniCorp, megaCorp, new IOUContract(), new UniqueIdentifier());
//        assertNotEquals(iouState.getParticipants().indexOf(miniCorpKey), -1);
//    }
//
//    @Test
//    public void recipientIsParticipant() {
//        IOUState iouState = new IOUState(1, miniCorp, megaCorp, new IOUContract(), new UniqueIdentifier());
//        assertNotEquals(iouState.getParticipants().indexOf(megaCorpKey), -1);
//    }
//
//    @Test
//    public void isLinearState() {
//        assert(LinearState.class.isAssignableFrom(IOUState.class));
//    }
//
//    @Test
//    public void isRelevantToSender() {
//        IOUState iouState = new IOUState(1, miniCorp, megaCorp, new IOUContract(), new UniqueIdentifier());
//        assert(iouState.isRelevant(ImmutableSet.of(miniCorpKey.getSingleKey())));
//    }
//
//    @Test
//    public void isRelevantToRecipient() {
//        IOUState iouState = new IOUState(1, miniCorp, megaCorp, new IOUContract(), new UniqueIdentifier());
//        assert(iouState.isRelevant(ImmutableSet.of(megaCorpKey.getSingleKey())));
//    }
}
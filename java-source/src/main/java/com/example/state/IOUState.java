package com.example.state;

import com.example.contract.IOUContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.CompositeKey;
import net.corda.core.crypto.Party;

import java.security.PublicKey;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

// TODO: Implement QueryableState and add ORM code (to match Kotlin example).

/**
 * The state object recording IOU agreements between two parties.
 *
 * A state must implement [ContractState] or one of its descendants.
 *
 * @param iouValue details of the IOU.
 * @param sender the party issuing the IOU.
 * @param recipient the party receiving and approving the IOU.
 * @param contract the contract which governs which transactions are valid for this state object.
 */
public class IOUState implements LinearState {
    private final Integer iouValue;
    private final Party sender;
    private final Party recipient;
    private final IOUContract contract;
    private final UniqueIdentifier linearId;

    public IOUState(Integer iouValue,
                    Party sender,
                    Party recipient,
                    IOUContract contract)
    {
        this.iouValue = iouValue;
        this.sender = sender;
        this.recipient = recipient;
        this.contract = contract;
        this.linearId = new UniqueIdentifier();
    }

    public Integer getIOUValue() { return iouValue; }
    public Party getSender() { return sender; }
    public Party getRecipient() { return recipient; }
    @Override public IOUContract getContract() { return contract; }
    @Override public UniqueIdentifier getLinearId() { return linearId; }
    @Override public List<CompositeKey> getParticipants() {
        return Stream.of(sender, recipient)
                .map(Party::getOwningKey)
                .collect(toList());
    }

    /**
     * This returns true if the state should be tracked by the vault of a particular node. In this case the logic is
     * simple; track this state if we are one of the involved parties.
     */
    @Override public boolean isRelevant(Set<? extends PublicKey> ourKeys) {
        final List<PublicKey> partyKeys = Stream.of(sender, recipient)
                .flatMap(party -> party.getOwningKey().getKeys().stream())
                .collect(toList());
        return ourKeys
                .stream()
                .anyMatch(partyKeys::contains);

    }
}
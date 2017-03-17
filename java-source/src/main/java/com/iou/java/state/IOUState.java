package com.iou.java.state;

import com.iou.java.contract.IOUContract;
import kotlin.collections.EmptyList;
import net.corda.core.contracts.ContractState;
import net.corda.core.crypto.CompositeKey;
import net.corda.core.crypto.Party;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * The state object recording IOU agreements between two parties.
 *
 * A state must implement [ContractState] or one of its descendants.
 *
 *  * @param contract the contract which governs which transactions are valid for this state object.
 */
public class IOUState implements ContractState {
    private final IOUContract contract;

    public IOUState(IOUContract contract) {
        this.contract = contract;
    }

    @Override public IOUContract getContract() { return contract; }
    @Override public List<CompositeKey> getParticipants() {
        return emptyList();
    }
}
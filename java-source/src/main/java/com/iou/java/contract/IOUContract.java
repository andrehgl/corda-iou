package com.iou.java.contract;

import com.iou.java.state.IOUState;
import net.corda.core.contracts.AuthenticatedObject;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.TransactionForContract;
import net.corda.core.crypto.SecureHash;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class IOUContract implements Contract {
    /**
     * The verify() function of all the states' contracts must not throw an exception for a transaction to be
     * considered valid.
     */
    @Override
    public void verify(TransactionForContract tx) {}

    /** This is a reference to the underlying legal contract template and associated parameters. */
    private final SecureHash legalContractReference = SecureHash.sha256("IOU contract template and params");
    @Override public final SecureHash getLegalContractReference() { return legalContractReference; }
}
package com.iou.contract;

import com.iou.state.IOUState;
import net.corda.core.contracts.AuthenticatedObject;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.TransactionForContract;
import net.corda.core.crypto.SecureHash;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class IOUContract {}
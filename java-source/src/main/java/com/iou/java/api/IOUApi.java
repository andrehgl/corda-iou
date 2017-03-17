package com.iou.java.api;

import com.iou.java.contract.IOUContract;
import com.iou.java.flow.IOUFlow;
import com.iou.java.state.IOUState;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.crypto.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.transactions.SignedTransaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.ExecutionException;

// This API is accessible from /api/iou. All paths you specify are relative to this root.
@Path("iou")
public class IOUApi {
    private final CordaRPCOps services;

    public IOUApi(CordaRPCOps services) {
        this.services = services;
    }
}
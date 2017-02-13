package com.iou.api;

import net.corda.core.messaging.CordaRPCOps;

import javax.ws.rs.Path;

// This API is accessible from /api/iou. All paths you specify are relative to this root.
@Path("iou")
public class IOUApi {

    public IOUApi(CordaRPCOps services) {}
}
package com.iou.kotlin.api

import com.iou.kotlin.contract.IOUContract
import com.iou.kotlin.flow.IOUFlow
import com.iou.kotlin.state.IOUState
import net.corda.core.messaging.CordaRPCOps
import java.util.concurrent.ExecutionException
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

// This API is accessible from /api/iou. All paths you specify are relative to this root.
@javax.ws.rs.Path("iou")
class IOUApi(val services: CordaRPCOps)
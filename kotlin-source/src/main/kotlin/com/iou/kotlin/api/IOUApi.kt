package com.iou.kotlin.api

import com.iou.kotlin.contract.IOUContract
import com.iou.kotlin.flow.IOUFlow
import com.iou.kotlin.flow.IOUSettleFlow
import com.iou.kotlin.flow.SelfIssueCashFlow
import com.iou.kotlin.state.IOUState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.messaging.startFlow
import java.util.concurrent.ExecutionException
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

// This API is accessible from /api/iou. All paths you specify are relative to this root.
@Path("iou")
class IOUApi(private val services: CordaRPCOps) {
    /**
     * Displays all IOU states that exist in the node's vault.
     */
    @GET
    @Path("ious")
    @Produces(MediaType.APPLICATION_JSON)
    fun getIOUs() = services.vaultAndUpdates().first

    /**
     * Initiates a flow to agree an IOU between two parties.
     */
    @GET
    @Path("create-iou")
    fun createIOU(
            @QueryParam(value = "value") iouValue: Int,
            @QueryParam(value = "party") partyName: String): Response {

        val thisParty = services.nodeIdentity().legalIdentity
        val otherParty = services.partyFromName(partyName)!!

        val state = IOUState(iouValue, thisParty, otherParty, IOUContract())

        // The line below blocks and waits for the flow to return.
        val result = services
                .startFlowDynamic(IOUFlow.Initiator::class.java, state, otherParty)
                .returnValue
                .get()

        return Response
                .status(Response.Status.CREATED)
                .entity("Transaction id ${result.id} sent to counterparty.").build()
    }

    @GET
    @Path("self-issue-cash")
    @Produces(MediaType.APPLICATION_JSON)
    fun selfIssueCash(@QueryParam(value = "amount") amount: Int): Response {
        val cashState = services.startFlowDynamic(SelfIssueCashFlow::class.java, amount).returnValue.get()
        return Response.status(Response.Status.CREATED).entity("Transaction id $cashState sent to counterparty.").build()
    }

    @GET
    @Path("settle-iou")
    @Produces(MediaType.APPLICATION_JSON)
    fun settleIOU(@QueryParam(value = "id") id: String,
                  @QueryParam(value = "amount") amount: Int): Response {
        val linearId = UniqueIdentifier.fromString(id)
        val result = services.startFlowDynamic(IOUSettleFlow.Initiator::class.java, linearId, amount).returnValue.get()
        return Response.status(Response.Status.CREATED).entity("Blah").build()
    }
}
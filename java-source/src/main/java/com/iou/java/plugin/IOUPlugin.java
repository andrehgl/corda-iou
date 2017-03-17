package com.iou.java.plugin;

import com.esotericsoftware.kryo.Kryo;
import com.iou.java.api.IOUApi;
import com.iou.java.contract.IOUContract;
import com.iou.java.service.IOUService;
import com.iou.java.flow.IOUFlow;
import com.iou.java.state.IOUState;
import net.corda.core.contracts.AuthenticatedObject;
import net.corda.core.contracts.Timestamp;
import net.corda.core.contracts.TransactionType;
import net.corda.core.contracts.TransactionVerificationException;
import net.corda.core.crypto.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.CordaPluginRegistry;
import net.corda.core.node.PluginServiceHub;
import net.corda.core.transactions.LedgerTransaction;

import java.util.*;
import java.util.function.Function;

public class IOUPlugin extends CordaPluginRegistry {
    /**
     * A list of classes that expose web APIs.
     */
    private final List<Function<CordaRPCOps, ?>> webApis = Collections.singletonList(IOUApi::new);

    /**
     * A list of flows required for this CorDapp. Any flow which is invoked from from the web API needs to be
     * registered as an entry into this map. The map takes the form:
     *
     * Name of the flow to be invoked -> Set of the parameter types passed into the flow.
     *
     * E.g. In the case of this CorDapp:
     *
     * "ExampleFlow.Initiator" -> Set(IOUState, Party)
     *
     * This map also acts as a white list. If a flow is invoked via the API and not registered correctly
     * here, then the flow state machine will _not_ invoke the flow. Instead, an exception will be raised.
     */
    private final Map<String, Set<String>> requiredFlows = Collections.singletonMap(
            IOUFlow.Initiator.class.getName(),
            new HashSet<>(Arrays.asList(
                    IOUState.class.getName(),
                    Party.class.getName()
            )));

    /**
     * A list of long lived services to be hosted within the node. Typically you would use these to register flow
     * factories that would be used when an initiating party attempts to communicate with our node using a particular
     * flow. See the [ExampleService.Service] class for an implementation which sets up a
     */
    private final List<Function<PluginServiceHub, ?>> servicePlugins = Collections.singletonList(IOUService::new);

    /**
     * A list of directories in the resources directory that will be served by Jetty under /web.
     */
    private final Map<String, String> staticServeDirs = Collections.emptyMap();

    @Override public List<Function<CordaRPCOps, ?>> getWebApis() { return webApis; }
    @Override public Map<String, Set<String>> getRequiredFlows() { return requiredFlows; }
    @Override public List<Function<PluginServiceHub, ?>> getServicePlugins() { return servicePlugins; }
    @Override public Map<String, String> getStaticServeDirs() { return staticServeDirs; }

    /**
     * Register required types with Kryo (our serialisation framework).
     */
    @Override public boolean registerRPCKryoTypes(Kryo kryo) {
        kryo.register(IOUState.class);
        kryo.register(IOUContract.class);
        kryo.register(TransactionVerificationException.ContractRejection.class);
        kryo.register(LedgerTransaction.class);
        kryo.register(AuthenticatedObject.class);
        // Uncomment once you've added the Create command constraint.
        // kryo.register(IOUContract.Create.class);
        kryo.register(Timestamp.class);
        kryo.register(TransactionType.General.class);
        return true;
    }
}
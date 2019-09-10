package jetbrains.buildServer.serverProxy;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.version.ServerVersionHolder;
import org.jetbrains.annotations.NotNull;
import java.util.*;

public class RemoteServerFacadeImpl extends jetbrains.buildServer.serverProxy.ClientXmlRpcExecutorFacade implements RemoteServerFacade {
    private final SessionXmlRpcTarget mySession;
    private static final Logger Log = Logger.getInstance("jetbrains.buildServer." + RemoteServerFacadeImpl.class.getName());
    private final Map<String, RemoteHandlerFacade> myRemoteFacades = new HashMap<String, RemoteHandlerFacade>();

    public RemoteServerFacadeImpl(SessionXmlRpcTarget target) {
        super(target, new ApplicationFacadeStub(), "PoolMovementXmlRpc", new VersionCheckerStub());
        mySession = target;
        Log.info(RemoteServerFacadeImpl.class.getName() + " initialized");
    }

    public Boolean changeAgentPoolTo(int agentId, String newPoolName) {
        return this.callXmlRpc("changeAgentPoolTo", agentId, newPoolName);
    }

    @Override
    public boolean changeAgentPoolToX(final int agentId, final String newPoolName) {
        boolean movedAgents = remoteCall(new ServerCommand<Boolean>() {
            public Boolean execute() {
                return changeAgentPoolTo(agentId, newPoolName);
            }

            public String describe() {
                return "changeAgentPoolTo";
            }
        });
        return movedAgents;
    }

    private <T> T remoteCall(@NotNull ServerCommand<T> command){
        try {
            return command.execute();
        } catch (Exception e) {
            Log.error("Remote call failed: " + command.describe(), e);
            try {
                final String remote = getRemoteProtocolVersion();
                final String local = getLocalProtocolVersion();
                Log.debug(String.format("Checking protocol compatibility. Found local=%s, remote=%s", local, remote));
            } catch (Exception e1) {
                Log.error("Error checking server version", e1);
            }
            throw new RuntimeException(e);
        }
    }

    private String getLocalProtocolVersion() {
        return ServerVersionHolder.getVersion().getPluginProtocolVersion();
    }

    @NotNull
    public String getRemoteProtocolVersion() {
        return getRemoteHandlerFacade(RemoteAuthenticationServer.REMOTE_AUTH_SERVER).callXmlRpc("getServerVersion");
    }

    @NotNull
    private synchronized RemoteHandlerFacade getRemoteHandlerFacade(@NotNull String handlerName) {
        RemoteHandlerFacade facade = myRemoteFacades.get(handlerName);
        if (facade == null) {
            facade = new RemoteHandlerFacade(mySession, handlerName);
            myRemoteFacades.put(handlerName, facade);
        }
        return facade;
    }
}

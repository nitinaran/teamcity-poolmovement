package com.teamcity.poolmovement;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.BuildAgent;
import jetbrains.buildServer.serverProxy.RemoteServerFacade;
import jetbrains.buildServer.serverProxy.RemoteServerFacadeImpl;
import jetbrains.buildServer.serverProxy.SessionXmlRpcTarget;
import jetbrains.buildServer.serverProxy.impl.SessionXmlRpcTargetImpl;
import jetbrains.buildServer.version.ServerVersionHolder;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.util.Date;

public class PMScheduledTask implements Runnable {
    private static final Logger Log = Logger.getInstance("jetbrains.buildServer." + PMScheduledTask.class.getName());

    private static int count = 0;
    private BuildAgent myBuildAgent;
    private PMConfiguration myPMConfiguration;
    // private final String myUser = "teamcitymaster";
    // private final String myPassword = "";
    private RemoteServerFacade myServerFacade;
    private SessionXmlRpcTarget mySession;
    private final int CONNECTION_TIMEOUT = 1000 * 60 * 10; // 10 mins
    private Date myAgentIdleDate;
    private volatile boolean myAgentIdleState;

    PMScheduledTask(@NotNull BuildAgent myBuildAgent, PMConfiguration myPMConfiguration) {
        this.myBuildAgent = myBuildAgent;
        this.myPMConfiguration = myPMConfiguration;
        String myServerUrl = myBuildAgent.getConfiguration().getServerUrl();
        Log.debug("Server URL: " + myServerUrl);
        try {
            mySession = new SessionXmlRpcTargetImpl(myServerUrl, getUserAgent(), CONNECTION_TIMEOUT);
            // mySession.setCredentials(myUser, myPassword);
            // mySession.authenticate(new XmlRpcTarget.Cancelable() {
            //     @Override
            //     public boolean isCanceled() {
            //         return false;
            //     }

            //     @Override
            //     public long sleepingPeriod() {
            //         return 0;
            //     }
            // });
        } catch (MalformedURLException e) {
            Log.error("Malformed URL" + e);
        }
        Log.info(PMScheduledTask.class.getName() + " initialized");
    }

    @Override
    public void run() {
        if (myAgentIdleState) {
            Log.info("Trying to change agent pool (count: " + ++count + ")");
            long timeDiff = new Date().getTime() - myAgentIdleDate.getTime();
            if (timeDiff > myPMConfiguration.getMyConfigIdleTimeout() * 60 * 1000) {
                Log.debug("Sending changeAgentPool command to server");
                boolean result = this.changeAgentPool();
                Log.info("Result for changeAgentPool command to server: " + result);
            } else {
                Log.info("Agent is not idle for enough time (timeDiff: " + timeDiff +
                        ", idleTimeout: " + myPMConfiguration.getMyConfigIdleTimeout() * 60 * 1000 + ")");
            }
        } else {
            Log.info("Agent is not currently idle (myAgentIdleState: " + myAgentIdleState + ")");
        }
    }

    private RemoteServerFacade getServerFacade() {
        if (myServerFacade == null) {
            myServerFacade = new RemoteServerFacadeImpl(mySession);
        }
        return myServerFacade;
    }

    private Boolean changeAgentPool() {
        return getServerFacade().changeAgentPoolToX(myBuildAgent.getId(), myPMConfiguration.getMyConfigIdlePoolName());
    }

    @NotNull
    private static String getUserAgent() {
        final String version = ServerVersionHolder.getVersion().getDisplayVersion();
        return "Pool Movement Plugin/" + version;
    }

    void setMyAgentIdleDate(Date myAgentIdleDate) {
        this.myAgentIdleDate = myAgentIdleDate;
    }

    void setMyAgentIdleState(boolean myAgentIdleState) {
        this.myAgentIdleState = myAgentIdleState;
    }
}


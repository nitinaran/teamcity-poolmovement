package com.teamcity.poolmovement;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.XmlRpcHandlerManager;
import jetbrains.buildServer.remoteHandlers.ServerModelXStreamHolder;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.agentPools.*;
import jetbrains.buildServer.serverSide.agentTypes.AgentTypeFinder;
import jetbrains.buildServer.serverSide.mute.ProblemMutingService;
import jetbrains.buildServer.serverSide.tracker.EventTracker;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class XmlRpcHandlerImpl implements XmlRpcHandler {
    @NotNull
    private final BuildServerEx myServer;
    @NotNull
    private final ServerModelXStreamHolder myXstreamHolder;
    @NotNull
    private final BuildAgentManager myBuildAgentManager;
    @NotNull
    private final AgentPoolManager myAgentPoolManager;
    @NotNull
    private final ProjectManager myProjectManager;
    @NotNull
    private final BuildCustomizerFactory myBuildCustomizerFactory;
    @NotNull
    private final EventTracker myEventTracker;
    @NotNull
    private final ProblemMutingService myProblemMutingService;
    @NotNull
    private final STestManager myTestManager;
    @NotNull
    private final SuitableConfigurationsProvider mySuitableConfigurationsProvider;
    @NotNull
    private final CurrentProblemsManager myCurrentProblemsManager;
    @NotNull
    private final TestName2Index myTestName2Index;
    @NotNull
    private final AgentTypeFinder myAgentTypeFinder;
    @NotNull
    private XmlRpcHandlerManager myXmlRpcHandlerManager;

    private static final Logger Log = Logger.getInstance(XmlRpcHandlerImpl.class.getName());

    public XmlRpcHandlerImpl (@NotNull XmlRpcHandlerManager myXmlRpcHandlerManager,
                              @NotNull BuildServerEx buildServerEx,
                              @NotNull BuildAgentManager buildAgentManager,
                              @NotNull AgentPoolManager agentPoolManager,
                              @NotNull ServerModelXStreamHolder serverModelXStreamHolder,
                              @NotNull ProjectManager projectManager,
                              @NotNull BuildCustomizerFactory buildCustomizerFactory,
                              @NotNull EventTracker eventTracker,
                              @NotNull ProblemMutingService problemMutingService,
                              @NotNull STestManager sTestManager,
                              @NotNull SuitableConfigurationsProvider suitableConfigurationsProvider,
                              @NotNull CurrentProblemsManager currentProblemsManager,
                              @NotNull TestName2Index testName2Index,
                              @NotNull AgentTypeFinder agentTypeFinder) {
        this.myXmlRpcHandlerManager = myXmlRpcHandlerManager;
        this.myServer = buildServerEx;
        this.myXstreamHolder = serverModelXStreamHolder;
        this.myBuildAgentManager = buildAgentManager;
        this.myAgentPoolManager = agentPoolManager;
        this.myProjectManager = projectManager;
        this.myBuildCustomizerFactory = buildCustomizerFactory;
        this.myEventTracker = eventTracker;
        this.myProblemMutingService = problemMutingService;
        this.myTestManager = sTestManager;
        this.mySuitableConfigurationsProvider = suitableConfigurationsProvider;
        this.myCurrentProblemsManager = currentProblemsManager;
        this.myTestName2Index = testName2Index;
        this.myAgentTypeFinder = agentTypeFinder;
        addHandler("PoolMovementXmlRpc", this);
        Log.info(XmlRpcHandlerImpl.class.getName() + " initialized");
    }

    @Override
    public boolean changeAgentPoolTo(int agentId, @NotNull String newPoolName) {
        // String agentName = "LTN3-EUD-D00167";
        // String newPoolName = "LTE_RAV_MK4";
        Log.info("changeAgentPoolTo agentId: " + agentId + " newPoolName: " + newPoolName);
        SBuildAgent agent = myBuildAgentManager.findAgentById(agentId, true);
        if (agent == null) {
            Log.error("Unable to find agent with id: " + agentId);
            return false;
        }
        String agentName = agent.getName();
        Log.debug("Found agent with id: " + agentId + " " + agentName);
        int agentPoolId;
        try {
            agentPoolId = agent.getAgentPoolId();
        } catch (NullPointerException e) {
            Log.error(e);
            return false;
        }
        List<AgentPool> agentPoolList = myAgentPoolManager.getAllAgentPools();
        boolean newPoolFound = false;
        int newPoolId = 0;
        for (AgentPool agentPool : agentPoolList) {
            if (agentPool.getName().equals(newPoolName)) {
                newPoolId = agentPool.getAgentPoolId();
                newPoolFound = true;
                break;
            }
        }
        Log.debug("New pool id: " + newPoolId);
        if (!newPoolFound) {
            Log.error("New pool id not found");
            return false;
        }

        if (agentPoolId == newPoolId) {
            Log.info("Already in the right pool, so nothing to do");
            return true;
        }
        try {
            Log.info("Moving the agent: " + agentName + " to the new pool: " + newPoolName);
            myAgentPoolManager.moveAgentToPool(newPoolId, (BuildAgentEx) agent);
            Log.info("Moved the agent: " + agentName + " to the new pool: " + newPoolName);
        } catch (NoSuchAgentPoolException | PoolQuotaExceededException | AgentTypeCannotBeMovedException e) {
            Log.error(e);
            return false;
        }
        return true;
    }

    @Override
    public void addHandler(@NotNull String name, @NotNull Object handler) {
        myXmlRpcHandlerManager.addHandler(name, handler);
    }
}

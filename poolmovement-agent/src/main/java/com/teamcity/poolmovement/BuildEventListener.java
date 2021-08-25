package com.teamcity.poolmovement;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.*;
import org.jetbrains.annotations.NotNull;
import java.util.Date;

public class BuildEventListener extends AgentLifeCycleAdapter {

    private static final Logger Log = Logger.getInstance("jetbrains.buildServer." + BuildEventListener.class.getName());

    private PMScheduledTask myPMScheduledTask;

    BuildEventListener(@NotNull PMScheduledTask myPMScheduledTask) {
        this.myPMScheduledTask = myPMScheduledTask;
    }

    @Override
    public void agentStarted(@NotNull BuildAgent agent) {
        //This is to track the agent status even if no build has been run on the agent
        Log.info("Agent started - setting idle to true in the scheduled task, and setting the idle date to current date");
        myPMScheduledTask.setMyAgentIdleState(true);
        myPMScheduledTask.setMyAgentIdleDate(new Date());
    }

    @Override
    public void buildStarted(@NotNull AgentRunningBuild runningBuild) {
        Log.info("Build started, setting idle to false in the scheduled task");
        myPMScheduledTask.setMyAgentIdleState(false);
    }

    @Override
    public void buildFinished(@NotNull AgentRunningBuild build, @NotNull BuildFinishedStatus buildStatus) {
        Log.info("Build finished, setting idle to true in the scheduled task, and setting the idle date to current date\n");
        myPMScheduledTask.setMyAgentIdleState(true);
        myPMScheduledTask.setMyAgentIdleDate(new Date());
    }
}

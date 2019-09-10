package com.teamcity.poolmovement;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.messages.BuildMessage1;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.Date;

public class BuildEventListener implements AgentLifeCycleListener {

    private static final Logger Log = Logger.getInstance("jetbrains.buildServer." + BuildEventListener.class.getName());

    private PMScheduledTask myPMScheduledTask;

    BuildEventListener(@NotNull PMScheduledTask myPMScheduledTask) {
        this.myPMScheduledTask = myPMScheduledTask;
    }

    @Override
    public void beforeAgentConfigurationLoaded(@NotNull BuildAgent agent) {

    }

    @Override
    public void afterAgentConfigurationLoaded(@NotNull BuildAgent agent) {

    }

    @Override
    public void pluginsLoaded() {

    }

    @Override
    public void agentInitialized(@NotNull BuildAgent agent) {

    }

    @Override
    public void agentStarted(@NotNull BuildAgent agent) {
        //This is to track the agent status even if no build has been run on the agent
        Log.info("Agent started - setting idle to true in the scheduled task, and setting the idle date to current date");
        myPMScheduledTask.setMyAgentIdleState(true);
        myPMScheduledTask.setMyAgentIdleDate(new Date());
    }

    @Override
    public void agentShutdown() {

    }

    @Override
    public void checkoutModeResolved(@NotNull AgentCheckoutMode agentCheckoutMode) {

    }

    @Override
    public void buildStarted(@NotNull AgentRunningBuild runningBuild) {
        Log.info("Build started, setting idle to false in the scheduled task");
        myPMScheduledTask.setMyAgentIdleState(false);
    }

    @Override
    public void sourcesUpdated(@NotNull AgentRunningBuild runningBuild) {

    }

    @Override
    public void personalPatchApplied(@NotNull AgentRunningBuild runningBuild) {

    }

    @Override
    public void preparationFinished(@NotNull AgentRunningBuild runningBuild) {

    }

    @Override
    public void beforeRunnerStart(@NotNull AgentRunningBuild runningBuild) {

    }

    @Override
    public void beforeRunnerStart(@NotNull BuildRunnerContext runner) {

    }

    @Override
    public void runnerFinished(@NotNull BuildRunnerContext runner, @NotNull BuildFinishedStatus status) {

    }

    @Override
    public void beforeBuildInterrupted(@NotNull AgentRunningBuild runningBuild, @NotNull BuildInterruptReason reason) {

    }

    @Override
    public void beforeBuildFinish(@NotNull BuildFinishedStatus buildStatus) {

    }

    @Override
    public void beforeBuildFinish(@NotNull AgentRunningBuild build, @NotNull BuildFinishedStatus buildStatus) {

    }

    @Override
    public void personalPatchReverted(@NotNull AgentRunningBuild runningBuild) {

    }

    @Override
    public void afterAtrifactsPublished(@NotNull AgentRunningBuild runningBuild, @NotNull BuildFinishedStatus status) {

    }

    @Override
    public void buildFinished(@NotNull BuildFinishedStatus buildStatus) {
        Log.info("Build finished, setting idle to true in the scheduled task, and setting the idle date to current date\n");
        myPMScheduledTask.setMyAgentIdleState(true);
        myPMScheduledTask.setMyAgentIdleDate(new Date());
    }

    @Override
    public void buildFinished(@NotNull AgentRunningBuild build, @NotNull BuildFinishedStatus buildStatus) {

    }

    @Override
    public void messageLogged(@NotNull BuildMessage1 buildMessage) {

    }

    @Override
    public void messageLogged(@NotNull AgentRunningBuild build, @NotNull BuildMessage1 buildMessage) {

    }

    @Override
    public void checkoutDirectoryRemoved(@NotNull File checkoutDir) {

    }

    @Override
    public void dependenciesDownloaded(@NotNull AgentRunningBuild runningBuild) {

    }
}

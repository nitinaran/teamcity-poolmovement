package com.teamcity.poolmovement;

import com.intellij.openapi.diagnostic.Logger;

import jetbrains.buildServer.agent.BuildAgent;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import java.util.Map;

public class PMMain {
    private static final Logger Log = Logger.getInstance("jetbrains.buildServer." + PMMain.class.getName());

    private BuildAgent myBuildAgent;
    private final String CONFIG_ENABLED = "com.teamcity.poolmovement.enabled";
    private final String CONFIG_IDLE_TIMEOUT = "com.teamcity.poolmovement.idle.timeout";
    private final String CONFIG_IDLE_POOLNAME = "com.teamcity.poolmovement.idle.poolname";
    private final String CONFIG_IDLE_CRON = "com.teamcity.poolmovement.idle.cron";

    public PMMain(@NotNull AgentEventDispatcher agentEventDispatcher,
                  @NotNull BuildAgent buildAgent) {
        myBuildAgent = buildAgent;
        PMConfiguration myPMConfiguration = parseAgentConfiguration();
        Log.info("Pool Movement Configuration:\n" + myPMConfiguration);
        if (myPMConfiguration.isMyConfigEnabled()) {
            //Task scheduler
            ThreadPoolTaskScheduler myTaskScheduler = new ThreadPoolTaskScheduler();
            myTaskScheduler.initialize();
            PMScheduledTask myPMScheduledTask = new PMScheduledTask(myBuildAgent, myPMConfiguration);
            myTaskScheduler.schedule(myPMScheduledTask, new CronTrigger(myPMConfiguration.getMyConfigIdleCron()));

            //Add event listener
            BuildEventListener buildEventListener = new BuildEventListener(myPMScheduledTask);
            agentEventDispatcher.AddListener(buildEventListener);
        } else
            Log.info("Configuration for pool movement is disabled, so nothing to do");
        Log.info(PMMain.class.getName() + " initialized");
    }

    private PMConfiguration parseAgentConfiguration() {
        PMConfiguration myPMConfiguration = new PMConfiguration();
        BuildAgentConfiguration configuration = myBuildAgent.getConfiguration();
        Map<String, String> configParameters = configuration.getConfigurationParameters();
        for (String key: configParameters.keySet()) {
            if (key.equals(CONFIG_ENABLED) &&
                    (configParameters.get(key).toLowerCase().equals("true")))
                myPMConfiguration.setMyConfigEnabled(true);
            if (key.equals(CONFIG_IDLE_TIMEOUT))
                myPMConfiguration.setMyConfigIdleTimeout(Integer.parseInt(configParameters.get(key)));
            if (key.equals(CONFIG_IDLE_POOLNAME))
                myPMConfiguration.setMyConfigIdlePoolName(configParameters.get(key));
            if (key.equals(CONFIG_IDLE_CRON))
                if (!configParameters.get(key).isEmpty())
                    myPMConfiguration.setMyConfigIdleCron(configParameters.get(key));
        }
        return myPMConfiguration;
    }
}

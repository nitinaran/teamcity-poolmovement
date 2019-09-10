package com.teamcity.poolmovement;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.AgentLifeCycleListener;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

public class AgentEventDispatcher {

    private static final Logger Log = Logger.getInstance("jetbrains.buildServer." + AgentEventDispatcher.class.getName());

    private final EventDispatcher<AgentLifeCycleListener> myEventDispatcher;

    public AgentEventDispatcher(@NotNull EventDispatcher<AgentLifeCycleListener> eventDispatcher) {
        myEventDispatcher = eventDispatcher;
        Log.info(AgentEventDispatcher.class.getName() + " initialized");
    }

    void AddListener(BuildEventListener buildEventListener) {
        Log.debug("Registering the event listener");
        myEventDispatcher.addListener(buildEventListener);
    }
}

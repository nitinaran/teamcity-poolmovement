package com.teamcity.poolmovement;

import org.jetbrains.annotations.NotNull;

public interface XmlRpcHandler {
    boolean changeAgentPoolTo(int agentId, @NotNull String newPoolName);
    void addHandler(@NotNull String name, @NotNull Object handler);
}

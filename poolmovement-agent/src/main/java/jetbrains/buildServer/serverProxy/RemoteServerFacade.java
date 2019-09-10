package jetbrains.buildServer.serverProxy;

public interface RemoteServerFacade {
  boolean changeAgentPoolToX(int agentId, String newPoolName);
}

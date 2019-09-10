package jetbrains.buildServer.serverProxy;

public interface ServerCommand<T> {
    T execute();

    String describe();
}

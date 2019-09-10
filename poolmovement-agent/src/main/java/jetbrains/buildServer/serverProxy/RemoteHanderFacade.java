package jetbrains.buildServer.serverProxy;

import jetbrains.buildServer.xmlrpc.XmlRpcTarget;
import org.jetbrains.annotations.NotNull;

class RemoteHandlerFacade extends ClientXmlRpcExecutorFacade {
    public RemoteHandlerFacade(@NotNull XmlRpcTarget target, @NotNull String handlerName) {
        super(target, new ApplicationFacadeStub(), handlerName, new VersionCheckerStub());
    }
}

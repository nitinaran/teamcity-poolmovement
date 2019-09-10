package jetbrains.buildServer.serverProxy;

import jetbrains.buildServer.xmlrpc.XmlRpcTarget;

public class ApplicationFacadeStub implements ApplicationFacade {
    public XmlRpcTarget.Cancelable createCancelable() {
        return null;
    }

    public void onProcessCanceled() {
    }
}

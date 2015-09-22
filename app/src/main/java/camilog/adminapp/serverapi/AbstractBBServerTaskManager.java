package camilog.adminapp.serverapi;

/**
 * Created by stefano on 22-09-15.
 */
public abstract class AbstractBBServerTaskManager {
    protected BBServer _server;
    public AbstractBBServerTaskManager(BBServer server){
        _server = server;
    }
}

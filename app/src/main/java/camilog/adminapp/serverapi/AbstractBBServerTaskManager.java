package camilog.adminapp.serverapi;

/**
 * Created by stefano on 22-09-15.
 */
public abstract class AbstractBBServerTaskManager {
    /**
     * Wrapper class for server utilities
     */
    protected BBServer _server;
    public AbstractBBServerTaskManager(BBServer server){
        _server = server;
    }
}

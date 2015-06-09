package cn.edu.sjtu.se.dclab.oss;

import cn.edu.sjtu.se.dclab.oss.util.Constants;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OSSDaemon implements Daemon {
    private static final Logger LOG = LoggerFactory.getLogger(OSSDaemon.class);

    private OnlineStatusServer onlineStatusServer;
    /**
     *
     * @param daemonContext
     * @throws DaemonInitException
     */
    @Override
    public void init(DaemonContext daemonContext) throws DaemonInitException {
        LOG.info("Initializing daemon...");
        Constants.init(System.getenv("CONF_DIR") + "/application.properties");
    }

    @Override
    public void start() throws Exception {
        onlineStatusServer = new OnlineStatusServer();
    }

    @Override
    public void stop() throws Exception {
        LOG.info("Stopping daemon...");
        onlineStatusServer.shutdown();
    }

    @Override
    public void destroy() {
        LOG.info("Destroying daemon...");
        onlineStatusServer.shutdown();
    }
}
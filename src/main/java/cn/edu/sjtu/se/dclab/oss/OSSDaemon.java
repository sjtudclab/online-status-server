package cn.edu.sjtu.se.dclab.oss;

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
     * @throws Exception
     */
    @Override
    public void init(DaemonContext daemonContext) throws DaemonInitException, Exception {
        LOG.info("Initializing daemon...");
    }

    @Override
    public void start() throws Exception {
        LOG.info("Starting daemon...");
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
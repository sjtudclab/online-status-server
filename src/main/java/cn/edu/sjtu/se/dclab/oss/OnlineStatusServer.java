package cn.edu.sjtu.se.dclab.oss;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnlineStatusServer implements Daemon {
    private static final Logger LOG = LoggerFactory.getLogger(OnlineStatusServer.class);

    private final int THREAD_NUMBER = 3;



    /**
     *
     * @param daemonContext
     * @throws DaemonInitException
     * @throws Exception
     */
    @Override
    public void init(DaemonContext daemonContext) throws DaemonInitException, Exception {
        LOG.info("Initializing online status server...");
    }

    @Override
    public void start() throws Exception {
        LOG.info("Starting online status server...");
    }

    @Override
    public void stop() throws Exception {
        LOG.info("Stopping online status server...");
    }

    @Override
    public void destroy() {
        LOG.info("Destroying online status server...");
    }
}
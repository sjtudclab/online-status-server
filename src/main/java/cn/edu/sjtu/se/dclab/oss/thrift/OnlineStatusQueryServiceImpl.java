package cn.edu.sjtu.se.dclab.oss.thrift;

import cn.edu.sjtu.se.dclab.oss.OnlineStatusServer;
import org.apache.thrift.TException;

/**
 * Created by francis on 6/8/15.
 */
public class OnlineStatusQueryServiceImpl implements OnlineStatusQueryService.Iface {

    private final OnlineStatusServer onlineStatusServer;

    public OnlineStatusQueryServiceImpl(OnlineStatusServer onlineStatusServer) {
        this.onlineStatusServer = onlineStatusServer;
    }

    @Override
    public String checkOnline(String userId) throws TException {
        return onlineStatusServer.query(userId);
    }
}

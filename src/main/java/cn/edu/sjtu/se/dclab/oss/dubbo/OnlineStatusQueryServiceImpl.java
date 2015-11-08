package cn.edu.sjtu.se.dclab.oss.dubbo;


import cn.edu.sjtu.se.dclab.oss.OnlineStatusServer;


/**
 * Created by francis on 6/8/15.
 */
public class OnlineStatusQueryServiceImpl implements OnlineStatusQueryService{

    private final OnlineStatusServer onlineStatusServer;

    public OnlineStatusQueryServiceImpl(OnlineStatusServer onlineStatusServer) {
        this.onlineStatusServer = onlineStatusServer;
    }

    @Override
    public String checkOnline(String userId) {
        return onlineStatusServer.query(userId);
    }
}

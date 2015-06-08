package cn.edu.sjtu.se.dclab.oss.thrift;

import org.apache.thrift.TException;

/**
 * Created by francis on 6/8/15.
 */
public class OnlineStatusQueryServiceImpl implements OnlineStatusQueryService.Iface {

    public OnlineStatusQueryServiceImpl() {}

    @Override
    public String checkOnline(long userId) throws TException {
        return "[ \"1111111111\", \"2222222222\" ]";
    }
}

package cn.edu.sjtu.se.dclab.oss;

import cn.edu.sjtu.se.dclab.oss.util.Constants;
import org.junit.Test;

/**
 * Created by francis on 6/9/15.
 */
public class OnlineStatusServerTest {

    @Test
    public void testInitServer() throws Exception {
        OnlineStatusServer server = new OnlineStatusServer();
        System.out.println("Redis port: " + Constants.REDIS_SERVER_HOST);
    }
}

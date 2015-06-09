package cn.edu.sjtu.se.dclab.oss.thrift;

import cn.edu.sjtu.se.dclab.service_management.Content;

/**
 * Created by francis on 6/9/15.
 */
public class OSSContent extends Content {

    private String ip;
    private int port;

    public OSSContent(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String getStr() {
        return ip + ":" + port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

package cn.edu.sjtu.se.dclab.oss.redis;

import java.util.Date;

/**
 * Created by francis on 6/12/15.
 */
public class ClientLastSeenTime {
    private String clientId;
    private Date date;

    public ClientLastSeenTime() {}

    public ClientLastSeenTime(String clientId, Date date) {
        this.clientId = clientId;
        this.date = date;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

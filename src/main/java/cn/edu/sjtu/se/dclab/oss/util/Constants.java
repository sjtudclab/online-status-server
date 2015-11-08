package cn.edu.sjtu.se.dclab.oss.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by francis on 6/7/15.
 */
public class Constants {
    private static final Logger LOG = LoggerFactory.getLogger(Constants.class);

    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    private String redisServerHost;
    private String redisServerPort;
    private int redisConnectionPoolSize;
    private int redisKeyExpireTime;

    private String rabbitmqHost;
    private int rabbitmqPort;
    private String rabbitmqUsername;
    private String rabbitmqPassword;
    private String rabbitmqVhost;
    private String rabbitmqQueueName;

    public String getRedisServerHost() {
        return redisServerHost;
    }

    public void setRedisServerHost(String redisServerHost) {
        this.redisServerHost = redisServerHost;
    }

    public String getRedisServerPort() {
        return redisServerPort;
    }

    public void setRedisServerPort(String redisServerPort) {
        this.redisServerPort = redisServerPort;
    }

    public int getRedisConnectionPoolSize() {
        return redisConnectionPoolSize;
    }

    public void setRedisConnectionPoolSize(int redisConnectionPoolSize) {
        this.redisConnectionPoolSize = redisConnectionPoolSize;
    }

    public int getRedisKeyExpireTime() {
        return redisKeyExpireTime;
    }

    public void setRedisKeyExpireTime(int redisKeyExpireTime) {
        this.redisKeyExpireTime = redisKeyExpireTime;
    }

    public String getRabbitmqHost() {
        return rabbitmqHost;
    }

    public void setRabbitmqHost(String rabbitmqHost) {
        this.rabbitmqHost = rabbitmqHost;
    }

    public int getRabbitmqPort() {
        return rabbitmqPort;
    }

    public void setRabbitmqPort(int rabbitmqPort) {
        this.rabbitmqPort = rabbitmqPort;
    }

    public String getRabbitmqUsername() {
        return rabbitmqUsername;
    }

    public void setRabbitmqUsername(String rabbitmqUsername) {
        this.rabbitmqUsername = rabbitmqUsername;
    }

    public String getRabbitmqPassword() {
        return rabbitmqPassword;
    }

    public void setRabbitmqPassword(String rabbitmqPassword) {
        this.rabbitmqPassword = rabbitmqPassword;
    }

    public String getRabbitmqVhost() {
        return rabbitmqVhost;
    }

    public void setRabbitmqVhost(String rabbitmqVhost) {
        this.rabbitmqVhost = rabbitmqVhost;
    }

    public String getRabbitmqQueueName() {
        return rabbitmqQueueName;
    }

    public void setRabbitmqQueueName(String rabbitmqQueueName) {
        this.rabbitmqQueueName = rabbitmqQueueName;
    }



    /*
    public static void init(String path) {
        Properties props = new Properties();
        FileInputStream file = null;
        try {
            //file = new FileInputStream(System.getenv("CONF_DIR") + "/application.properties");
            //file = new FileInputStream("/home/francis/projects/online-status-server/src/main/conf/application.properties");
            file = new FileInputStream(path);
            props.load(file);
            file.close();

            REDIS_SERVER_HOST = props.getProperty("redis.host", "192.168.1.254");
            REDIS_SERVER_PORT = props.getProperty("redis.port", "6379");
            REDIS_CONNECTION_POOL_SIZE = Integer.parseInt(props.getProperty("redis.pool", "10"));
            REDIS_KEY_EXPIRE_TIME = Integer.parseInt(props.getProperty("redis.key.expire.time", "600"));

            RABBITMQ_HOST = props.getProperty("rabbitmq.host", "192.168.1.254");
            RABBITMQ_PORT = Integer.parseInt(props.getProperty("rabbitmq.port", "5672"));
            RABBITMQ_USERNAME = props.getProperty("rabbitmq.username", "test");
            RABBITMQ_PASSWORD = props.getProperty("rabbitmq.password", "test");
            RABBITMQ_VHOST = props.getProperty("rabbitmq.vhost", "/");
            RABBITMQ_QUEUE_NAME = props.getProperty("rabbitmq.queue.name", "ONLINE_STATUS_QUEUE");

            THRIFT_SERVER_PORT = Integer.parseInt(props.getProperty("thrift.server.port", "6666"));
            THRIFT_SERVER_NAME = props.getProperty("thrift.server.name", "online-status-server");
            THRIFT_SERVER_IP = props.getProperty("thrift.server.ip", "192.168.1.3");

            LOG.info("Redis: " + REDIS_SERVER_HOST + ":" + REDIS_SERVER_PORT);
        } catch (FileNotFoundException ex) {
            LOG.debug("application.properties not found.\n" + ex.getMessage());
            try {
                if (file != null) {
                    file.close();
                }
            } catch (IOException ioe) {
                LOG.debug(ex.getMessage());
            }
        } catch (IOException ex) {
            LOG.debug(ex.getMessage());
        } finally {
            file = null;
        }
    }
    */
}

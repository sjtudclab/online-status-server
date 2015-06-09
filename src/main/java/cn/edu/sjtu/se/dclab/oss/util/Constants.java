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

    public static String REDIS_SERVER_HOST;
    public static String REDIS_SERVER_PORT;
    public static int REDIS_CONNECTION_POOL_SIZE;
    public static int REDIS_KEY_EXPIRE_TIME;

    public static String RABBITMQ_SERVER_HOST;
    public static int RABBITMQ_SERVER_PORT;

    public static int THRIFT_SERVER_PORT;
    public static String THRIFT_SERVER_NAME;
    public static String THRIFT_SERVER_IP;

    public static void init(String path) {
        Properties props = new Properties();
        FileInputStream file = null;
        try {
            LOG.debug(System.getenv("CONF_DIR"));
            //file = new FileInputStream(System.getenv("CONF_DIR") + "/application.properties");
            //file = new FileInputStream("/home/francis/projects/online-status-server/src/main/conf/application.properties");
            file = new FileInputStream(path);
            props.load(file);
            file.close();

            REDIS_SERVER_HOST = props.getProperty("redis.host", "192.168.1.254");
            REDIS_SERVER_PORT = props.getProperty("redis.port", "6379");
            REDIS_CONNECTION_POOL_SIZE = Integer.parseInt(props.getProperty("redis.pool", "10"));
            REDIS_KEY_EXPIRE_TIME = Integer.parseInt(props.getProperty("redis.key.expire.time", "600"));

            RABBITMQ_SERVER_HOST = props.getProperty("rabbitmq.host", "192.168.1.254");
            RABBITMQ_SERVER_PORT = Integer.parseInt(props.getProperty("rabbitmq.port", "5672"));

            THRIFT_SERVER_PORT = Integer.parseInt(props.getProperty("thrift.server.port", "6666"));
            THRIFT_SERVER_NAME = props.getProperty("thrift.server.name", "online-status-server");
            THRIFT_SERVER_IP = props.getProperty("thrift.server.ip", "192.168.0.108");

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
}

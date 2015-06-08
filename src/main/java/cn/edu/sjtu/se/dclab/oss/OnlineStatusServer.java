package cn.edu.sjtu.se.dclab.oss;

import cn.edu.sjtu.se.dclab.oss.thrift.OnlineStatusQueryService;
import cn.edu.sjtu.se.dclab.oss.thrift.OnlineStatusQueryServiceImpl;
import cn.edu.sjtu.se.dclab.oss.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.core.RBucket;
import org.redisson.core.RMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static cn.edu.sjtu.se.dclab.oss.util.TimeUtil.now;

/**
 * Created by francis on 6/7/15.
 */
public class OnlineStatusServer {
    private static Logger LOG = LoggerFactory.getLogger(OnlineStatusServer.class);

    // Redis client
    private Redisson redisson;

    // thrift server
    TServer server;
    OnlineStatusQueryServiceImpl queryService;

    public OnlineStatusServer() {
        LOG.info("Initializing online status server...");
        initRedisClient();
        initThriftServer();
        LOG.info("Initializing online status completed...");
    }

    private void initRedisClient() {
        LOG.info("Initializing redis client...");
        Config conf = new Config();
        conf.useSingleServer()
            .setAddress("192.168.1.254:6379")
            .setConnectionPoolSize(Constants.REDIS_CONNECTION_POOL_SIZE);

        redisson = Redisson.create(conf);
        LOG.info("Initializing redis client completed...");
    }

    private void initThriftServer() {
        try {
            // init & start thrift server
            TServerSocket serverSocket = new TServerSocket(Constants.THRIFT_SERVER_PORT);
            TProcessor processor = new OnlineStatusQueryService.Processor<>(queryService);
            server = new TThreadPoolServer(new TThreadPoolServer.Args(serverSocket).processor(processor));
            LOG.info("Starting server on port " + Constants.THRIFT_SERVER_PORT);
            server.serve();

            // register service to zookeeper
            String localIp = InetAddress.getLocalHost().getHostAddress();

        } catch (UnknownHostException ex) {
            LOG.debug(ex.getMessage());
        } catch (TTransportException ex) {
            LOG.debug(ex.getMessage());
        }
    }

    public String query(String content) {
        String userId;
        try {
            JsonNode root = new ObjectMapper().readTree(content);
            userId = root.asText();
        } catch (JsonProcessingException ex) {
            LOG.debug("error parsing input json. " + content);
            return Constants.ERROR;
        } catch (IOException ex) {
            LOG.debug("unexpected IOException.");
            return Constants.ERROR;
        }

        RMap<String, RBucket<Date>> map = redisson.getMap(userId);
        Collection<String> clients = map.keySet();

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode node = mapper.createArrayNode();
        for (String client : clients) {
            node.add(client);
        }

        return node.toString();
    }

    private boolean deserialize(String content, String userId, String clientId) {
        try {
            JsonNode node = new ObjectMapper().readTree(content);
            userId = node.get("userId").asText();
            clientId = node.get("clientId").asText();
        } catch(JsonProcessingException ex){
            LOG.debug("error parsing input json. " + content);
            return false;
        } catch(IOException ex){
            LOG.debug("unexpected IOException.");
            return false;
        }

        return true;
    }

    public String notifyOnline(String content) {
        String userId = "";
        String clientId = "";

        if (!deserialize(content, userId, clientId)) {
            return Constants.ERROR;
        } else {
            return notifyOnline(userId, clientId);
        }
    }

    private String notifyOnline(String userId, String clientId) {
        RBucket<Date> bucket = redisson.getBucket(clientId);
        bucket.set(now());

        RMap<String, RBucket<Date>> map = redisson.getMap(userId);
        removeExpiredClients(map, now());
        map.putIfAbsent(userId, bucket);
        map.clearExpire();
        map.expire(Constants.REDIS_KEY_EXPIRE_TIME, TimeUnit.SECONDS);
        return Constants.SUCCESS;
    }

    public String notifyOffline(String content) {
        String userId = "";
        String clientId = "";
        if (!deserialize(content, userId, clientId)) {
            return Constants.ERROR;
        } else {
            return notifyOffline(userId, clientId);
        }
    }

    private String notifyOffline(String userId, String clientId) {
        RMap<String, RBucket<Date>> map = redisson.getMap(userId);
        removeExpiredClients(map, now());
        map.remove(clientId);
        map.clearExpire();
        map.expire(Constants.REDIS_KEY_EXPIRE_TIME, TimeUnit.SECONDS);

        return Constants.SUCCESS;
    }

    private void removeExpiredClients(RMap<String, RBucket<Date>> map, Date currentDate) {
        for (Map.Entry<String, RBucket<Date>> entry : map.entrySet()) {
            boolean expired = checkExpires(entry.getValue(), currentDate);
            if (expired) {
                map.remove(entry.getKey());
            }
        }
    }

    private boolean checkExpires(RBucket<Date> bucket, Date currentDate) {
        long diff = currentDate.getTime() - bucket.get().getTime();
        return (diff / 1000) >= Constants.REDIS_KEY_EXPIRE_TIME;
    }

    /**
     * shutdown procedure of Online status server
     */
    public void shutdown() {
        LOG.info("Shutting down online status server...");
        shutdownThriftServer();
        shutdownRedisClient();
    }

    private void shutdownRedisClient() {
        LOG.info("Shutting down redis connection...");
        redisson.shutdown();
    }

    private void shutdownThriftServer() {
        LOG.info("Shutting down thrift server...");
        server.stop();
    }
}

package cn.edu.sjtu.se.dclab.oss;

import cn.edu.sjtu.se.dclab.oss.rabbitmq.WorkQueueConsumer;
import cn.edu.sjtu.se.dclab.oss.thrift.OSSContent;
import cn.edu.sjtu.se.dclab.oss.thrift.OnlineStatusQueryService;
import cn.edu.sjtu.se.dclab.oss.thrift.OnlineStatusQueryServiceImpl;
import cn.edu.sjtu.se.dclab.oss.util.Constants;
import cn.edu.sjtu.se.dclab.service_management.Content;
import cn.edu.sjtu.se.dclab.service_management.ServiceManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.core.RMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
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

    // rabbitmq client
    private WorkQueueConsumer consumer;
    private Thread consumerThread;


    public OnlineStatusServer() throws Exception {
        LOG.info("Initializing online status server...");
        initRedisClient();
        initRabbitmqClient();
        initThriftServer();
        LOG.info("Initializing online status completed...");
    }

    private void initRedisClient() {
        LOG.info("Initializing redis client...");
        Config conf = new Config();
        conf.useSingleServer()
            .setAddress(Constants.REDIS_SERVER_HOST + ":" + Constants.REDIS_SERVER_PORT)
            .setConnectionPoolSize(Constants.REDIS_CONNECTION_POOL_SIZE);

        redisson = Redisson.create(conf);
        LOG.info("Initializing redis client completed...");
    }

    private void initThriftServer() throws Exception {
        LOG.info("Initializing thrift server...");
        // init & start thrift server
        queryService = new OnlineStatusQueryServiceImpl(this);
        TServerSocket serverSocket = new TServerSocket(Constants.THRIFT_SERVER_PORT);
        OnlineStatusQueryService.Processor<OnlineStatusQueryServiceImpl> processor =
            new OnlineStatusQueryService.Processor<>(queryService);
        server = new TThreadPoolServer(new TThreadPoolServer.Args(serverSocket).processor(processor));
        LOG.info("Starting server on port " + Constants.THRIFT_SERVER_PORT);

        // register service to zookeeper
        Content content = new OSSContent(Constants.THRIFT_SERVER_IP, Constants.THRIFT_SERVER_PORT);
        ServiceManager manager = ServiceManager.getInstance();
        manager.registe(Constants.THRIFT_SERVER_NAME, UUID.randomUUID().toString().replace("-", ""), content, null, null);

        server.serve();
    }

    private void initRabbitmqClient() throws Exception {
        LOG.info("Initializing rabbitmq connection...");
        consumer = new WorkQueueConsumer(this);
        consumerThread = new Thread(consumer);
        consumerThread.start();
    }

    public String query(String userId) {
        RMap<String, Date> map = redisson.getMap(userId);
        Collection<String> clients = map.keySet();

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode node = mapper.createArrayNode();
        for (String client : clients) {
            node.add(client);
        }

        return node.toString();
    }

    public String notifyOnline(String userId, String clientId) {
        Date now = now();
        RMap<String, Date> map = redisson.getMap(userId);
        removeExpiredClients(map, now);
        map.put(clientId, now());
        map.clearExpire();
        map.expire(Constants.REDIS_KEY_EXPIRE_TIME, TimeUnit.SECONDS);
        return Constants.SUCCESS;
    }

    public String notifyOffline(String userId, String clientId) {
        RMap<String, Date> map = redisson.getMap(userId);
        removeExpiredClients(map, now());
        map.remove(clientId);
        map.clearExpire();
        map.expire(Constants.REDIS_KEY_EXPIRE_TIME, TimeUnit.SECONDS);

        return Constants.SUCCESS;
    }

    private void removeExpiredClients(RMap<String, Date> map, Date date) {
        for (Map.Entry<String, Date> entry : map.entrySet()) {
            boolean expired = checkExpires(entry.getValue(), date);
            if (expired) {
                map.remove(entry.getKey());
            }
        }
    }

    private boolean checkExpires(Date date, Date currentDate) {
        long diff = currentDate.getTime() - date.getTime();
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

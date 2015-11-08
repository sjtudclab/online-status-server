package cn.edu.sjtu.se.dclab.oss;

import cn.edu.sjtu.se.dclab.oss.rabbitmq.WorkQueueConsumer;
import cn.edu.sjtu.se.dclab.oss.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.core.RMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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

    // rabbitmq client
    private WorkQueueConsumer consumer;
    private Thread consumerThread;
    private Constants constants;

    public OnlineStatusServer(Constants constants) throws Exception {
        LOG.info("Initializing online status server...");
        this.constants = constants;
        initRedisClient();
        initRabbitmqClient();
        LOG.info("Initializing online status completed...");
    }

    private void initRedisClient() {
        LOG.info("Initializing redis client...");
        Config conf = new Config();
        conf.useSingleServer()
            .setAddress(constants.getRedisServerHost() + ":" + constants.getRedisServerPort())
            .setConnectionPoolSize(constants.getRedisConnectionPoolSize());

        redisson = Redisson.create(conf);
        LOG.info("Initializing redis client completed...");
    }

    private void initRabbitmqClient() throws Exception {
        LOG.info("Initializing rabbitmq connection...");
        consumer = new WorkQueueConsumer(this, constants);
        LOG.info("rabbitmq init finished!");
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
        map.expire(constants.getRedisKeyExpireTime(), TimeUnit.SECONDS);
        LOG.info("notifyOnline userId:" + userId + " clientId: " + clientId);
//        redisson.flushdb();
        LOG.info("notifyOnline date:" + redisson.getMap(userId).get(clientId));
        return Constants.SUCCESS;
    }

    public String notifyOffline(String userId, String clientId) {
        RMap<String, Date> map = redisson.getMap(userId);
        removeExpiredClients(map, now());
        map.remove(clientId);
        map.clearExpire();
        map.expire(constants.getRedisKeyExpireTime(), TimeUnit.SECONDS);
        LOG.info("notifyOffline userId:" + userId + " clientId: " + clientId);
//        redisson.flushdb();
        LOG.info("notifyOffline date:" + redisson.getMap(userId).get(clientId));
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
        return (diff / 1000) >= constants.getRedisKeyExpireTime();
    }
}

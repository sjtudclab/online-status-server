package cn.edu.sjtu.se.dclab.oss.rabbitmq;

import cn.edu.sjtu.se.dclab.oss.OnlineStatusServer;
import cn.edu.sjtu.se.dclab.oss.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by francis on 6/12/15.
 */
public class WorkQueueConsumer implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(WorkQueueConsumer.class);
    private static final ConnectionFactory factory = new ConnectionFactory();

    static {
        factory.setHost(Constants.RABBITMQ_HOST);
        factory.setPort(Constants.RABBITMQ_PORT);
        factory.setUsername(Constants.RABBITMQ_USERNAME);
        factory.setPassword(Constants.RABBITMQ_PASSWORD);
        factory.setVirtualHost(Constants.RABBITMQ_VHOST);
    }

    private final OnlineStatusServer server;
    private Connection connection;
    private Channel channel;
    private QueueingConsumer consumer;

    public WorkQueueConsumer(OnlineStatusServer server) throws Exception {
        this.server = server;

        Connection connection = factory.newConnection();
        channel = connection.createChannel();
        LOG.info("Task queue name is: " + Constants.RABBITMQ_QUEUE_NAME);

        channel.queueDeclare(Constants.RABBITMQ_QUEUE_NAME, true, false, false, null);
        channel.basicQos(1);

        consumer = new QueueingConsumer(channel);
        channel.basicConsume(Constants.RABBITMQ_QUEUE_NAME, false, consumer);
    }

    @Override
    public void run() {
        while (true) {
            try {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                String message = new String(delivery.getBody());

                LOG.info("Message received: " + message);
                processOnlineStatusMessage(message);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (Exception ex) {
                LOG.error(ex.getMessage());
            }
        }
    }

    private void processOnlineStatusMessage(String message) {
        String clientId, userId, command;
        try {
            JsonNode node = new ObjectMapper().readTree(message);
            clientId = node.get("clientId").asText();
            userId = node.get("userId").asText();
            command = node.get("command").asText();

            switch (command) {
                case "online":
                    server.notifyOnline(userId, clientId);
                    break;
                case "offline":
                    server.notifyOffline(userId, clientId);
                    break;
                default:
                    break;
            }
        } catch (JsonProcessingException jpe) {
            LOG.error(jpe.getMessage());
        } catch (IOException ioe) {
            LOG.error(ioe.getMessage());
        }
    }
}

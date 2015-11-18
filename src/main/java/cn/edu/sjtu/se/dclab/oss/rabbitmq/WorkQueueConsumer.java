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

    private final OnlineStatusServer server;
    private Connection connection;
    private Channel channel;
    private QueueingConsumer consumer;

    public WorkQueueConsumer(OnlineStatusServer server, Constants constants) throws Exception {
        this.server = server;
        factory.setUsername(constants.getRabbitmqUsername());
        factory.setPassword(constants.getRabbitmqPassword());
        factory.setVirtualHost(constants.getRabbitmqVhost());
        factory.setHost(constants.getRabbitmqHost());
        factory.setPort(constants.getRabbitmqPort());

        try {
            Connection connection = factory.newConnection();
            channel = connection.createChannel();
            LOG.info("Task queue name is: " + constants.getRabbitmqQueueName());

            channel.queueDeclare(constants.getRabbitmqQueueName(), true, false, false, null);
            channel.basicQos(1);
            LOG.info("WorkQueueConsumer Channel finished.");
            consumer = new QueueingConsumer(channel);
            LOG.info("WorkQueueConsumer QueueingConsumer init.");
            channel.basicConsume(constants.getRabbitmqQueueName(), false, consumer);
            LOG.info("WorkQueueConsumer QueueingConsumer finished.");
        } catch(Exception e) {
            LOG.info(e.getStackTrace().toString());
        }

    }

    @Override
    public void run() {
        while (true) {
            try {
                LOG.info("WorkQueueConsumer run");
                QueueingConsumer.Delivery delivery = null;
                try{
                    delivery = consumer.nextDelivery();
                }catch(Throwable e){
                    LOG.info("throwable : " + e);
                }

                LOG.info("WorkQueueConsumer delivery body : " + delivery);
                String message = new String(delivery.getBody());

                LOG.info("Message received: " + message);
                processOnlineStatusMessage(message);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                LOG.info("WorkQueueConsumer delivery body : " );
            } catch (Exception ex) {
                LOG.info("WorkQueueConsumer Exception: "+ ex.getStackTrace().toString());
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
        }catch(Exception e){
            LOG.info("processOnlineStatusMessage" + e);
        }
    }
}

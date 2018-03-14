package com.sap.bulletinboard.statistics.resources;

import javax.inject.Inject;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.sap.bulletinboard.statistics.config.SystemEnvironment;
import com.sap.bulletinboard.statistics.models.Statistics;
import com.sap.bulletinboard.statistics.util.StatisticsCounter;
import com.sap.bulletinboard.statistics.util.UTF8StringConverter;

@EnableScheduling
public class SendStatistics {
    private static final int FIVE_SECONDS = 5000;
    private final String routingKey;

    @Inject
    private AmqpTemplate amqpTemplate;

    @Inject
    private StatisticsCounter statisticsCounter;

    @Inject
    private UTF8StringConverter utf8StringConverter;

    @Inject
    public SendStatistics(AmqpAdmin amqpAdmin, SystemEnvironment systemEnvironment) {
        routingKey = systemEnvironment.getSendStatisticsQueueName();
        amqpAdmin.declareQueue(new Queue(routingKey));
    }

    @Scheduled(fixedDelay = FIVE_SECONDS)
    public void run() {
        Statistics statisticsForOne = statisticsCounter.get(1);
        String message = statisticsForOne.toString();
        amqpTemplate.send(routingKey, getMessage(message));
    }

    private Message getMessage(String messageString) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setExpiration(String.valueOf(3 * FIVE_SECONDS));
        return new Message(utf8StringConverter.toByteArray(messageString), messageProperties);
    }

}

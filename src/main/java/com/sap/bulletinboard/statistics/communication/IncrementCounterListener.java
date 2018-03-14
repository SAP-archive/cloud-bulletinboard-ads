package com.sap.bulletinboard.statistics.communication;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;

import com.sap.bulletinboard.statistics.config.SystemEnvironment;
import com.sap.bulletinboard.statistics.util.StatisticsCounter;
import com.sap.bulletinboard.statistics.util.UTF8StringConverter;
import com.sap.hcp.cf.logging.common.LogContext;

public class IncrementCounterListener implements MessageListener {

    @Inject
    private StatisticsCounter statisticsCounter;

    @Inject
    private UTF8StringConverter utf8StringConverter;

    private Logger logger;

    @Inject
    public IncrementCounterListener(AmqpAdmin amqpAdmin, ConnectionFactory rabbitConnectionFactory,
            SystemEnvironment systemEnvironment) {
        String queueName = systemEnvironment.getCounterIncrementQueueName();
        amqpAdmin.declareQueue(new Queue(queueName));
        logger = LoggerFactory.getLogger(getClass());
        logger.info("registering as listener for for queue '{}'", queueName);
        SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
        listenerContainer.setConnectionFactory(rabbitConnectionFactory);
        listenerContainer.setQueueNames(queueName);
        listenerContainer.setMessageListener(this);
        
        /* Workaround till spring-rabbit 2.0 release */
        DefaultMessagePropertiesConverter messagePropertiesConverter = new DefaultMessagePropertiesConverter();
        messagePropertiesConverter
                .setCorrelationIdAsString(DefaultMessagePropertiesConverter.CorrelationIdPolicy.STRING);
        listenerContainer.setMessagePropertiesConverter(messagePropertiesConverter);

        listenerContainer.start();
    }

    @Override
    public void onMessage(Message message) {
        logger.info(message.toString());
        initializeLogging(message);
        Long id = convertMessage(message.getBody());
        if (id != null) {
            logger.info("received increment for ID: {}", id);
            statisticsCounter.increment(id);
        }
    }

    private Long convertMessage(byte[] message) {
        Long id = null;

        String messageString = utf8StringConverter.toString(message);
        try {
            id = Long.valueOf(messageString);
        } catch (NumberFormatException ex) {
            logger.info("received message can not be processed as it is not a Number: ", message);
        }
        return id;
    }

    private void initializeLogging(Message message) {
        String correlationId = getCorrelationId(message);
        LogContext.initializeContext(correlationId);
    }

    private String getCorrelationId(Message message) {
        MessageProperties messageProperties = message.getMessageProperties();
        String correlationId = messageProperties.getCorrelationIdString();
        return correlationId;
    }
}

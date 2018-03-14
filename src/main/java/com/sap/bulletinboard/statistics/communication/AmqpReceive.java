package com.sap.bulletinboard.statistics.communication;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import com.sap.bulletinboard.statistics.util.UTF8StringConverter;
import com.sap.hcp.cf.logging.common.LogContext;

public class AmqpReceive {
    @Inject
    private ConnectionFactory rabbitConnectionFactory;

    @Inject
    private AmqpAdmin amqpAdmin;

    @Inject
    Logger logger;

    @Inject
    private UTF8StringConverter utf8StringConverter;

    public void registerListener(String queueName, AmqpStringMessageListener listener) {
        amqpAdmin.declareQueue(new Queue(queueName));

        SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
        listenerContainer.setConnectionFactory(rabbitConnectionFactory);
        listenerContainer.setQueueNames(queueName);
        logger.info("registering '{}' as listener for for queue '{}'", listener, queueName);
        listenerContainer.setMessageListener(new InternalListener(listener));
        listenerContainer.start();
    }

    private class InternalListener implements MessageListener {
        private AmqpStringMessageListener listener;

        public InternalListener(AmqpStringMessageListener listener) {
            this.listener = listener;
        }

        @Override
        public void onMessage(Message message) {
            initializeLogging(message);
            String messageString = utf8StringConverter.toString(message.getBody());
            listener.onMessage(messageString);
        }

        private void initializeLogging(Message message) {
            String correlationId = getCorrelationId(message);
            LogContext.initializeContext(correlationId);
        }

        private String getCorrelationId(Message message) {
            MessageProperties messageProperties = message.getMessageProperties();
            byte[] correlationId = messageProperties.getCorrelationId();
            if (correlationId == null) {
                return null;
            }
            return utf8StringConverter.toString(correlationId);
        }
    }
}

package com.sap.bulletinboard.ads.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("cloud")
public class CloudRabbitConfig extends AbstractCloudConfig {

    public static final String STATISTICS_ROUTING_KEY = "statistics.adIsShown";
    public static final String PERIODIC_QUEUE_NAME = "statistics.periodicalStatistics";

    /**
     * Parses the local environment variable VCAP_SERVICES (containing cloud information) and provides a
     * ConnectionFactory. The superclass {@link AbstractCloudConfig}, part of the Spring Cloud plugin, is used for this.
     */
    @Bean
    public ConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory factory = (CachingConnectionFactory) (connectionFactory().rabbitConnectionFactory());
        factory.setPublisherConfirms(true);
        factory.setPublisherReturns(true);
        // When using publisher confirms, the cache size needs to be large enough
        // otherwise channels can be closed before confirms are received.
        factory.setChannelCacheSize(100);
        return factory;
    }

    /**
     * Using the ConnectionFactory, provide an AmqpAdmin implementation. This can be used, for example, to declare new
     * queues.
     */
    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.declareQueue(new Queue(STATISTICS_ROUTING_KEY)); // creates queue, if not existing
        rabbitAdmin.declareQueue(new Queue(PERIODIC_QUEUE_NAME));
        return rabbitAdmin;
    }

    /**
     * Using the ConnectionFactory, provide an AmqpTemplate implementation. This can be used, for example, to send
     * messages.
     */
    @Bean
    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true); // otherwise we get no info whether message could not be routed
        return rabbitTemplate;
    }

    @Bean
    public SimpleMessageListenerContainer pushMessageContainer(
            @Qualifier("statisticsListener") final MessageListener messageListener,
            ConnectionFactory connectionFactory) {

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);

        container.setQueueNames(PERIODIC_QUEUE_NAME);
        container.setPrefetchCount(20);// avoid backpressure: limit number of unacknowledged messages for a particular
                                       // channel
        container.setDefaultRequeueRejected(false); // to prevent requeuing in case of exception
        container.setMessageListener(messageListener);
        container.setAutoStartup(true);

        return container;
    }

}
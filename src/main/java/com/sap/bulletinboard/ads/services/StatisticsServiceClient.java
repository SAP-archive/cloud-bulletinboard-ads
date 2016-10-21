package com.sap.bulletinboard.ads.services;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.stereotype.Component;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.sap.bulletinboard.ads.config.CloudRabbitConfig;
import com.sap.hcp.cf.logging.common.LogContext;

@Component
public class StatisticsServiceClient {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private AmqpTemplate rabbitTemplate;

    @Inject
    public StatisticsServiceClient(AmqpTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void advertisementIsShown(long id) {
        new IncrementCounterCommand(id).queue(); // queue calls the run() asynchronously
    }

    private class IncrementCounterCommand extends HystrixCommand<Void> {
        protected final String correlationId;
        private String id;

        IncrementCounterCommand(long id) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(StatisticsServiceClient.class.getName()))
                    .andCommandKey(HystrixCommandKey.Factory.asKey(CloudRabbitConfig.STATISTICS_ROUTING_KEY)));
            this.id = String.valueOf(id);
            this.correlationId = LogContext.getCorrelationId();
        }

        @Override
        protected Void run() throws Exception {
            LogContext.initializeContext(correlationId);

            logger.info("sending message '{}' for routing key '{}'", id, CloudRabbitConfig.STATISTICS_ROUTING_KEY);

            rabbitTemplate.convertAndSend(null, CloudRabbitConfig.STATISTICS_ROUTING_KEY, id,
                    new MessagePostProcessor() {
                        public Message postProcessMessage(Message message) {
                            message.getMessageProperties().setCorrelationId(correlationId);
                            return message;
                        }
                    });
            return null;
        }

        @Override
        protected Void getFallback() {
            logger.warn("Failure to send message to statistics service");
            return null;
        }
    }

}

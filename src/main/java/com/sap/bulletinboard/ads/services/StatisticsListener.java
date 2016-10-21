package com.sap.bulletinboard.ads.services;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component // defines a Spring Bean with name "statisticsListener"
@Profile("cloud")
public class StatisticsListener implements MessageListener {
    private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onMessage(Message message) {
        logger.info("got statistics: {}", toString(message.getBody()));
    }

    private String toString(byte[] byteArray) {
        return new String(byteArray, CHARSET_UTF8);
    }
}
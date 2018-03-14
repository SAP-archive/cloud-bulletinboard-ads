package com.sap.bulletinboard.statistics.config;

import javax.inject.Inject;

import org.slf4j.Logger;

public class SystemEnvironment {
    private static final String QUEUE_INCREMENT = "QUEUE_INCREMENT";
    private static final String QUEUE_SEND_STATISTICS = "QUEUE_SEND_STATISTICS";

    @Inject
    private Logger logger;

    public String getCounterIncrementQueueName() {
        String errorMessage = "Queue name for counter increment must be set in environment variable " + QUEUE_INCREMENT;
        return getMandatory(QUEUE_INCREMENT, errorMessage);
    }

    public String getSendStatisticsQueueName() {
        String errorMessage = "Queue name for sending out statistics must be set in environment variable "
                + QUEUE_SEND_STATISTICS;
        return getMandatory(QUEUE_SEND_STATISTICS, errorMessage);
    }

    private String getMandatory(String variableName, String errorMessage) {
        String result = System.getenv(variableName);
        if (result == null) {
            logger.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
        return result;
    }
}
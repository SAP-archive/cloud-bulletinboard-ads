package com.sap.bulletinboard.statistics.communication;

public interface AmqpStringMessageListener {
    void onMessage(String message);
}

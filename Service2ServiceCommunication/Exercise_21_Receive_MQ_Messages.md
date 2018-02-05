[Optional] Exercise 21: Receive MQ Messages
===============================

## Learning Goal

The task of this exercise is to consume Advanced Message Queueing Protocol (AMQP) messages from the RabbitMQ message queue that are sent out by the Statistics service periodically.

## Prerequisite
Continue with your solution of the last exercise. If this does not work, you can checkout the branch [`origin/solution-20-Use-Message-Queues`](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-20-Use-Message-Queues).

Ensure that the `bulletinboard-statistics` application is deployed in your CF space and that you have created a `rabbitmq` service with name `mq-bulletinboard`.

## Step 1: Define a Message Listener

The Statistics service periodically sends out messages containing its statistics for the advertisement with ID="1".

In this step you want to implement a listener that handles the messages from the Statistics service.

Create a `StatisticsListener` class as part of the `com.sap.bulletinboard.ads.services` package that implements the `MessageListener` interface and annotate the class with `@Component` and `@Profile("cloud")`.

Implement the `onMessage(message message)` method, that the received message gets logged:
```
logger.info("got statistics: {}", new String(message.getBody(), Charset.forName("UTF-8")));
```

## Step 2: Register the Message Listener for the Queue

First of all let's make sure that the queue, we would like to listen to, does exist. Therefore in the `CloudRabbitConfig` class as part of the `amqpAdmin` Bean definition we declare another Queue with `statistics.periodicalStatistics` as routing key.

In order to register the `StatisticsListener` as listener for the queue you can enhance the `CloudRabbitConfig` with the following Bean definition:

```java
@Bean
public SimpleMessageListenerContainer pushMessageContainer(
        @Qualifier("statisticsListener") final MessageListener messageListener,
        ConnectionFactory connectionFactory) {

    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);

    container.setQueueNames();
    container.setPrefetchCount(20);// avoid backpressure: limit number of unacknowledged messages for a particular channel
    container.setDefaultRequeueRejected(false); // to prevent requeuing in case of exception
    container.setMessageListener(messageListener); // registers StatisticsListener
    container.setAutoStartup(true);

    return container;
}
```

## Step 3: Deploy and Test
Deploy your microservice and check if messages similar to `got statistics: Statistics [id=1, viewCount=<ViewCount>]` are logged. The Statistics service sends out messages every five seconds.

## Used Frameworks and Tools
- [RabbitMQ](https://www.rabbitmq.com/)
- [AMQP - Advanced Message Queuing Protocol](https://www.amqp.org/)
- [Spring AMQP](http://projects.spring.io/spring-amqp/)

***
<dl>
  <dd>
  <div class="footer">&copy; 2018 SAP SE</div>
  </dd>
</dl>
<hr>
<a href="Exercise_20_Use_Message_Queues.md">
  <img align="left" alt="Previous Exercise">
</a>
<a href="/Security/Exercise_22_DeployApplicationRouter.md">
  <img align="right" alt="Next Exercise">
</a>

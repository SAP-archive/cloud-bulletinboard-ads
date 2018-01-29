[Optional] Exercise 21: Receive MQ Messages
===============================

## Learning Goal

The task of this exercise is to consume Advanced Message Queueing Protocol (AMQP) messages from the RabbitMQ message queue that are sent out by the Statistics service periodically.

## Prerequisite
Continue with your solution of the last exercise. If this does not work, you can checkout the branch [`origin/solution-20-Use-Message-Queues`](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-20-Use-Message-Queues).

Ensure that the `bulletinboard-statistics` application is deployed in your CF space and that you have created a `rabbitmq` service with name `mq-bulletinboard`.

## Step 1: Define a Message Listener

The Statistics service periodically sends out messages containing its statistics for the advertisement with ID="1".
In this step you want to implement and register a listener for the corresponding queue to receive messages from the Statistics service.

Create a `StatisticsListener` class that implements the `MessageListener` interface and annotate the class with `@Component` and `@Profile("cloud")`.
- In the constructor, declare a queue with name "statistics.periodicalStatistics" similar like in the [`StatisticsServiceClient` constructor](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-20-Use-Message-Queues/src/main/java/com/sap/bulletinboard/ads/services/StatisticsServiceClient.java). In addition to an instance of `AmqpAdmin` also inject an instance of `org.springframework.amqp.rabbit.connection.ConnectionFactory`. Then create an instance of `SimpleMessageListenerContainer` and register the `StatisticsListener`:
```
  SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
  listenerContainer.setConnectionFactory(connectionFactory);
  listenerContainer.setQueueNames("statistics.periodicalStatistics");
  listenerContainer.setMessageListener(this);
  listenerContainer.start();
```
**Note:** This ensures that the `onMessage` method is invoked whenever a message is delivered to the specified queue.

## Step 2: Log Received Messages
Implement the `onMessage(message message)` method, that the received message gets logged:

```
logger.info("got statistics: {}", new String(message.getBody(), Charset.forName("UTF-8")));
```

## Step 3: Deploy and Test
Deploy your microservice and check if a messages similar to `got statistics: Statistics [id=1, viewCount=<ViewCount>]` are logged. The Statistics service sends out messages every five seconds.

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

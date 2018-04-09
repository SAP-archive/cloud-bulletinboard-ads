[Optional] Exercise 20: Add Message into Message Queue
======================================================

## Learning Goal

Asynchronous communication by publish-subscribe / messaging systems is a very important element of high performance and resilient cloud applications. Instead of calling a client service and waiting for a response, you just send a message and can immediately continue. There are different levels of loose coupling. In the extreme case the sender of an event does not know or care how many other services (if any) subscribed to their event.

The goal of this exercise is that you learn how to send a message to inform other services about a specific event. Specifically, the task is to send an AMQP message to the RabbitMQ Message Queue Service (part of the standard CF backing services) whenever your Advertisement Service receives a request for a specific advertisement. This event (AMQP message) will then be picked up by a **Statistics Service** that counts how many times an advertisement was viewed. 

**Note:** In this exercise we will only send messages. Receiving and processing is handled in the next excercise.

## Prerequisite
Continue with your solution of the last exercise. If this does not work, you can checkout the branch [`origin/solution-19-Transfer-CorrelationID`](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-19-Transfer-CorrelationID).

## Step 1: Add Maven Dependency
Add the `spring-rabbit` dependency to your `pom.xml`:
```xml
<!-- AMQP / RabbitMQ messaging -->
<dependency>
    <groupId>org.springframework.amqp</groupId>
    <artifactId>spring-rabbit</artifactId>
    <version>2.0.1.RELEASE</version>
    <exclusions>
        <exclusion>
            <!-- We need a more recent version of spring-context than the one included in spring-rabbit -->
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```
**Note:** After you've changed the Maven settings, don't forget to update your Eclipse project (`Alt+F5`)!

## Step 2: Add `CloudRabbitConfig`

Create a new class `CloudRabbitConfig` in package `com.sap.bulletinboard.ads.config` and copy the code from [here](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-20-Use-Message-Queues/src/main/java/com/sap/bulletinboard/ads/config/CloudRabbitConfig.java).

## Step 3: Implement `StatisticsServiceClient`

- Create a new class `StatisticsServiceClient` in package `com.sap.bulletinboard.ads.services` and annotate it with `@Component`.
**Note:** the classes we will be using in the following are from `org.springframework.amqp.core`, so when you're fixing the import errors pick classes from this package.
- In the constructor of `StatisticsServiceClient` you expect that the `RabbitTemplate` gets injected. 
- Create a method `advertisementIsShown(long id)`. The responsibility of this method is to send out messages to the queue named "statistics.adIsShown":

```java
public void advertisementIsShown(long id) {
    rabbitTemplate.convertAndSend(null, "statistics.adIsShown", String.valueOf(id));
}
```
Note: In our setup, as we are not specifying any Exchange, we bind to the **Direct Exchange** so we bind to a Queue 
via a fixed routing key, which currently relates to the queue named "statistics.adIsShown". 

**Other implementation hints:** 
  - Emit a log message whenever a message is sent.
  - Think about providing the `Correlation-ID` as part of the message ([messageProperties.setCorrelationId()](http://docs.spring.io/spring-amqp/api/org/springframework/amqp/core/MessageProperties.html) via a [MessagePostProcessor](http://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jms/core/MessagePostProcessor.html)). Retrieve the `Correlation-ID` using `LogContext.getCorrelationId()`.

## Step 4: Use `StatisticsServiceClient` in `AdvertisementController`

Inject the `StatisticsServiceClient` into your `AdvertisementController` class as part of the constructor and notify the Statistics Service whenever an individual advertisement is requested.

## Step 5: Test Locally
The virtual machine also provides a RabbitMQ service that you can use for local testing. 

#### Prepare `VCAP_SERVICES`
Based on the `VCAP_SERVICES` environment variable  the `spring-cloud` connector instantiates as part of the `CloudRabbitConfig` class the `AmqpAdmin` and `AmqpTemplate` instance that are used to communicate with the bound RabbitMQ service. Locally this environment variable needs to be set:

- In Eclipse, open the Tomcat server settings (by double-clicking on the server) and then open the launch configuration. In the Environment tab edit the `VCAP_SERVICES` variable. Replace the value with the following:
```javascript
{"rabbitmq-lite":[{"credentials":{"hostname":"127.0.0.1","password":"guest","uri":"amqp://guest:guest@127.0.0.1:5672","username":"guest"},"name":"rabbitmq-lite","label":"rabbitmq-lite","tags":["rabbitmq33","rabbitmq","amqp"]}],"postgresql-9.3":[{"name":"postgresql-lite","label":"postgresql-9.3","credentials":{"dbname":"test","hostname":"127.0.0.1","password":"test123!","port":"5432","uri":"postgres://testuser:test123!@localhost:5432/test","username":"testuser"},"tags":["relational","postgresql"],"plan":"free"}]}
```

- If you want to view / edit `VCAP_SERVICES`or JSON structures in general, you can e.g. use the Chrome [JSON Editor](https://chrome.google.com/webstore/detail/json-editor/lhkmoheomjbkfloacpgllgjcamhihfaj?utm_source=chrome-ntp-icon) plugin.
- If you run the application from the command line, update your `localEnvironmentSetup` script: [`localEnvironmentSetup.sh`](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-20-Use-Message-Queues/localEnvironmentSetup.sh)([`localEnvironmentSetup.bat`](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-20-Use-Message-Queues/localEnvironmentSetup.bat)) with the new value of `VCAP_SERVICES`.

#### Run and test the application

You can run the application using Eclipse or on the command line:
```
$ source localEnvironmentSetup.sh
$ mvn tomcat7:run
```
- Send some GET-By-Id requests to the Advertisement Service like [localhost:8080/api/v1/ads/1](http://localhost:8080/api/v1/ads/1).
- Then, in the browser, visit the **RabbitMQ Admin UI** at [http://localhost:15672/](http://localhost:15672/) (login with guest:guest).
Observe the information shown in the `Totals` view, and find out how many messages your application has sent.

## Step 6: Fix Tests
In the current state the tests fail, as the `statisticsServiceClient` bean can't be created, because the beans like the AMQPTemplate to be injected are undefined.
In previous exercises when we needed to connect to a (PostgreSQL) database, we used an embedded database (H2) in the tests.
In the case of RabbitMQ a similar approach is not easily possible.
Instead, we just provide mock instances of every queue-related bean that is defined in the `CloudRabbitConfig` class.

#### Create `MockRabbitConfig`
As part of the `src/test/java` source folder create a new class `MockRabbitConfig` in the package `com.sap.bulletinboard.ads.config` and copy the code from [here](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-20-Use-Message-Queues/src/test/java/com/sap/bulletinboard/ads/config/MockRabbitConfig.java). With that there is no need to mock the `StatisticsServiceClient`. 

After that the tests should run again.

## Step 7: Create and Bind CF RabbitMQ Service
A Message Queue is provided as backing service. There must be a RabbitMQ service instance on Cloud Foundry the application can connect to:

#### Creating the service
In the terminal, run the following command: 
```
cf create-service rabbitmq v3.6-dev mq-bulletinboard
```
Note you can get the exact names of the available services and its plans with `cf marketplace`.

#### Bind service
In the `manifest.yml` file, make sure the `mq-bulletinboard` service is bound to your application: 

```
services:
 - mq-bulletinboard
```
With this the application should start when deployed in the cloud.
Deploy your Advertisement Service and check in Kibana (via log messages) if the expected notifications are sent.

## Step 8: Deploy Statistics Service as Message Consumer
In order to test whether the messages in the queue can be processed by a consumer, you can deploy the Statistics Service as consuming application into your CF space. It is bound to the `mq-bulletinboard` service and listens to it:
 - Checkout the branch [Statistics Service](https://github.com/SAP/cloud-bulletinboard-ads/tree/statistic-service) branch
 - In order to deploy the statistics application enter on the command line:
```
# Ensure that you are in the project root e.g. ~/git/cloud-bulletinboard-ads
$ mvn clean verify
$ cf push -n bulletinboard-statistics-<<your user id>>
```
- Whenever you request an advertisment the Statistics Service should increase the counter for the same. To test this interaction you can call in the browser for example `[bulletinboard-statistics-<<<your user id>>>.cfapps.<<region>>.hana.ondemand.com/api/v1/statistics/1]`(`https://bulletinboard-statistics-<<your user id>>.cfapps.<<region>>.hana.ondemand.com/api/v1/statistics/1`) - where "1" is the advertisment ID.

## [Optional] Step 9: Use Hystrix

For HTTP/REST communication we used Hystrix so that failures of external dependencies can be mitigated.
For message queue systems like RabbitMQ we do not notice failures of applications receiving the messages.
However, the queue itself might fail for various reasons: e.g. the queue accepts no more messages.

Adapt your configuration so that the `amqpTemplate.send` method is wrapped in a `HystrixCommand`, similar as you've done in [Exercise 17](../Service2ServiceCommunication/Exercise_17_Introduce_Hystrix.md). Make sure to store the Correlation ID in a field, as the `run` method will be called in a different thread, and use this field to initialize the log context using `LogContext.initializeContext`.

Have a look at our sample [branch exercise 20](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-20-Use-Message-Queues).

## Used Frameworks and Tools
- [RabbitMQ](https://www.rabbitmq.com/)
- [AMQP - Advanced Message Queuing Protocol](https://www.amqp.org/)
- [Spring AMQP](http://projects.spring.io/spring-amqp/)
- [JSDoc AMQP Template (package org.springframework.amqp.core and interface AmqpTemplate)](https://docs.spring.io/spring-amqp/docs/latest-ga/api/)

***
<dl>
  <dd>
  <div class="footer">&copy; 2018 SAP SE</div>
  </dd>
</dl>
<hr>
<a href="Exercise_19_Transfer_CorrelationID.md">
  <img align="left" alt="Previous Exercise">
</a>
<a href="Exercise_21_Receive_MQ_Messages.md">
  <img align="right" alt="Next Exercise">
</a>

Exercise 12: Setup SLF4J-Logger
===============================

## Learning Goal
After this exercise you know how to setup a logger instance and how to write structured information into the log, which can be interpreted using tools available in the Cloud Foundry environment. 

The task of this exercise is to create a logger using the SLF4J `LoggerFactory` and to write several messages into the log using the [Java Logging library](https://github.com/SAP/cf-java-logging-support). Get familiar with the default configuration and learn how to overrule them via system environment variables. 

The Java logging library we are using
- when run in the cloud, formats the log into a machine-readable JSON format which is understood by the used ELK stack instance (otherwise the message is printed as plain text)
- uses [Logback](http://logback.qos.ch/) as SLF4J API implementation
- lets you configure the log level and other settings in `src/main/resources/logback.xml`
- enriches each log message with additional information, e.g. the timestamp or the class emitting the log message

## Prerequisite
Continue with your solution of the last exercise. If this does not work, you can checkout the branch [`origin/solution-11-Develop-Custom-Queries`](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-11-Develop-Custom-Queries).
## Step 1: Initialize the Logger
In order to initialize features provided by the SAP library [Logging Support for Cloud Foundry](https://github.com/SAP/cf-java-logging-support) you need to add a servlet filter within the `onStartup` method of the `AppInitializer` class (import from `com.sap.hcp.cf.logging.servlet.filter`):

```java
// register logging servlet filter which logs HTTP request processing details
servletContext.addFilter("RequestLoggingFilter", RequestLoggingFilter.class).addMappingForUrlPatterns(null, false, "/*");
```

A servlet filter intercepts the requests and, among other things, initializes the SLF4J Mapped Diagnostic Context (MDC), which will be introduced in the [next exercise](/LoggingTracing/Exercise_13_Use_SLF4J_Features.md).

## Step 2: Instantiate Logger in Your `AdvertisementController` 
The [SLF4J API](http://www.slf4j.org) provides a simple interface that can be used to access logger objects.
In the context of a class you would retrieve a SLF4J logger instance by passing the name of the class or, better, the corresponding class object. 

```java
Logger logger = LoggerFactory.getLogger(getClass());
```

We recommend to provide the class object as shown above, so that the logger name is correct even if you copy-paste the code into another class.

## Step 3: Log GET Request
Whenever a GET request for a single advertisement is sent, a message including the ID of the requested advertisement should be logged with log level INFO.

Note: For **performance** reasons you should avoid string concatenation, especially for debug/trace messages.
As an example, `logger.info("logging in " + user)` should be avoided in favor of `logger.info("logging in {}", user)`.

Ensure that the console output shows a message when a GET request is sent via `Postman`.

## Step 4: Change Log Format

Open the `src/main/resources/logback.xml` file in order to view the current settings for the logback configuration. Find the line `<appender-ref ref="${APPENDER:-STDOUT}" />`. This configures the logging framework to use the appender which is given in the `$APPENDER` environment variable and, if this is not set, defaults to `STDOUT`.

The appender named `STDOUT` references the `ConsoleAppender` (part of Logback) which prints out human-readable log messages.
In the cloud we would like to format the log messages as JSON. For that, the `STDOUT-JSON` appender (which internally references the `JsonEncoder`, part of the logging library) should be used. Modify the configuration default accordingly.

>**Tip for local setup**: As the JSON logs are cumbersome to read in the command line, you want to overrule the log format setting locally. You can do that by applying the system environment variable `APPENDER=STDOUT` to your Tomcat configuration as explained below. After restarting the application trigger another GET request to see that the logs are again shown in a readable format. 

>**Tip for JUnit tests**: In order to have readable log messages in the JUnit tests, you can just copy the `src/main/resources/logback.xml` into the `src/test/resources/` folder and rename it to `logback-test.xml`(!) and ensure that the settings are defaulted to STDOUT: `<appender-ref ref="${APPENDER:-STDOUT}" />`.

>**When using the `sap_java_buildpack`** instead of the community buildpack and package our application as `war` file, the deployed application makes use of the `logback.xml` configuration file, which is centrally provided by the `sap_java_buildpack`. 
That means the `logback.xml` that is provided in the `src/main/resources/` folder is used for local execution only, unless you explicitly override the centrally provided configuration as described [here](/LoggingTracing/Exercise_14_GettingStarted_With_ELK_Stack.md#step-13-provide-a-custom-logbackxml).

### Eclipse Tomcat configuration
Before you (re-)start your Tomcat webserver within Eclipse, you need to adapt the Tomcat configuration. To do so, double-click the server instance in the `Servers` view and select the `Open launch configuration` link. In the `Edit configuration` dialog switch to the `Environment` tab and add the environment variable `APPENDER` with value `STDOUT`.

### Maven Tomcat configuration
Make sure that `localEnvironmentSetup.sh` (on Linux) and `localEnvironmentSetup.bat` (on Windows) sets the environment variable `APPENDER=STDOUT`.

If updated, then run `source localEnvironmentSetup.sh` (`localEnvironmentSetup.bat`) in the terminal.

## Step 5: Log GET response
Whenever a GET request for a single advertisement returns the found `Advertisement` instance, this should be logged with log level INFO. Ensure that the console output shows the properties of the advertisement when the GET request is sent.

Note: As long as `Advertisement` does not override the `toString` method, only `Advertisement@hashcode` will be displayed in the log. In order to generate the `toString` method go to the `Advertisement` class and in the context menu select `Source` - `Generate toString()...`.

## Step 6: Log Not-Found Exception
Whenever a GET request for a single advertisement is sent for a non-existing ID, this should be logged with the log level WARN. Ensure that the console output shows the call stack of the `NotFoundException` when the invalid GET request is sent.

## Step 7: Use TRACE Level
The INFO messages you added in the previous steps do not contain a lot of information. Thus, change the log level of the messages from INFO to TRACE.

The current configuration of the project sets the log level for `com.sap.bulletinboard` messages to INFO, meaning TRACE messages are not part of the output. To see the TRACE messages, you have two options:
- update the default configuration in `logback.xml` by changing the default level for `com.sap.bulletinboard` to `${LOG_APP_LEVEL:-TRACE}`.
- or you can apply the system environment variable `LOG_APP_LEVEL=TRACE`. Note: The syntax `${LOG_APP_LEVEL:-INFO}` is from bash and means that the resulting value is the value of the environment variable `LOG_APP_LEVEL` if set, and otherwise `INFO`.

Ensure that the console output shows the TRACE messages when a GET request is sent.

**Note:** when you start your Tomcat server from within Eclipse you may receive a timeout due to the amount of log statements being logged. In order to change the default timeout of 45s, go to the *Servers* tab in Eclipse, double-click *Tomcat*, open the *Timeouts* section and enter a custom timeout for *Start*, e.g. `120` for 120 seconds / 2 minutes.



## Used Frameworks and Tools
- [Simple Logging Facade for Java (SLF4J)](http://www.slf4j.org/)
- [Logging Library](https://github.com/SAP/cf-java-logging-support) 

## Further Reading
- [Logging Performance](http://www.slf4j.org/faq.html#logging_performance)
- [Log Levels](http://www.slf4j.org/api/org/apache/log4j/Level.html)
 
***
<dl>
  <dd>
  <div class="footer">&copy; 2018 SAP SE</div>
  </dd>
</dl>
<hr>
<a href="/ConnectDatabase/Exercise_11_Develop_Custom_Queries.md">
  <img align="left" alt="Previous Exercise">
</a>
<a href="/LoggingTracing/Exercise_13_Use_SLF4J_Features.md">
  <img align="right" alt="Next Exercise">
</a>

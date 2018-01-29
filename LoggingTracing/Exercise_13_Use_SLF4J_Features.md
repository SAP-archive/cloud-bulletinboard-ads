Exercise 13: Use SLF4J Features
===============================

## Learning Goal
After this exercise you know how to use the mapped diagnostic context (MDC) and other SLF4J features. 

The task here is to enrich the logs written in the `AdvertisementController` class by making use of SLF4J features.

## Prerequisite
Continue with your solution of the last exercise. If this does not work, you can checkout the branch [`origin/solution-12-Setup-Logger`](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-12-Setup-Logger).
## Step 1: Add Endpoint Information to the Thread Context
To demonstrate how the MDC can be used, we now add information about the called endpoint to each log message. Instead of adding this information to all three log statements, we store it into the MDC once.

For this, use `MDC.put` to store endpoint information (i.e. "api/v1.0/ads/XXX" where XXX is the requested ID using the key `endpoint` for example.

Make sure that the `endpoint` information is included in all messages that are shown in the console. It should be the same output for all messages corresponding to the same request. 

**Note:** The logging library stores as part of the `RequestLoggingFilter.class` Servlet Filter the `correlation_id` to the MDC for each incoming request (in addition to other information). Using this `correlation_id` one can easily connect (correlate) log messages emitted as part of a single request. 

## [Optional] Step 2: Using _Markers_ and _Filters_
_Markers_ can be used to attach categories (or tags) to log messages, which then can be used to filter the log messages in Kibana.
For example, one can attach markers for all SQL related log messages, and then during analysis only show messages with or without such _Markers_.

You can also use _Markers_ to disable logging of certain messages directly on application level. By making use of a so-called _Filter_ you can filter out messages of dedicated _Markers_ **even if their log level is higher** than the configured threshold. 
**Note:** Using this approach the corresponding log messages are never printed to STDOUT and, thus, cannot be proccessed in Kibana.

In this exercise we extend our application to use the _Markers_ TECHNICAL for technical log messages.

First introduce a marker named `TECHNICAL`:
```java
Marker technicalMarker = MarkerFactory.getMarker("TECHNICAL");
```

When creating or updating advertisement, log a message showing the version of the created/updated advertisement. Here make use of this _Markers_:
```
logger.info(technicalMarker, "Created advertisement, version {}", createdAd.getVersion());
```

Relaunch the application on Tomcat, create a new advertisement, and ensure that the console output shows the new log message.
In the JSON output, you should already see the _Markers_ in the `categories` JSON field: `{ [...], "categories":["TECHNICAL"],"msg":"Created advertisement, version 0" }`

Now extend the `src/main/resources/logback.xml` configuration file by adding a _Filter_:
```
<!-- do not log messages with TECHNICAL marker -->
<configuration>
...
   <turboFilter class="ch.qos.logback.classic.turbo.MarkerFilter">
     <Marker>TECHNICAL</Marker>
     <OnMatch>DENY</OnMatch>
   </turboFilter>
...
</configuration>
```
Restart your application, create a new advertisement and notice that the INFO log message is not shown in the Console anymore as it gets filtered by `TurboFilter` whose `OnMatch` option is set to `DENY`. As consequence you are unable to see the messages matching the filter in Kibana.

## [Optional] Step 3: Add Custom Fields
Sometimes it is helpful to include information in addition to the log message.
Instead of just extending the log message itself, we want structured information as part of the JSON output.
Structured information like `{ [...], "custom_fields":{"key":"value"}, [...] }` can easily be parsed and processed in Kibana and other tools.

For this exercise step, add log statements to your code which include a key-value pair using custom fields.
For that add an argument using the `CustomField.customField()` method provided by the logging library:

```
logger.info("demonstration of custom fields, not part of message", CustomField.customField("example-key", "example-value"));
logger.info("demonstration of custom fields, part of message: {}", CustomField.customField("example-key", "example-value"));
```

Make sure that the JSON log output then contains `{ [...], "custom_fields":{"example-key":"example-value"}, [...] }`.

## Used Frameworks and Tools
- [Simple Logging Facade for Java (SLF4J)](http://www.slf4j.org/)
- [Logging Library](https://github.com/SAP/cf-java-logging-support) 

## Further Reading
- [Mapped Diagnostic Context](http://logback.qos.ch/manual/mdc.html)


***
<dl>
  <dd>
  <div class="footer">&copy; 2018 SAP SE</div>
  </dd>
</dl>
<hr>
<a href="/LoggingTracing/Exercise_12_Setup_Logger.md">
  <img align="left" alt="Previous Exercise">
</a>
<a href="/LoggingTracing/Exercise_14_GettingStarted_With_ELK_Stack.md">
  <img align="right" alt="Next Exercise">
</a>

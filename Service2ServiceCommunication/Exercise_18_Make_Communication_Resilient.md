Exercise 18: Make the Communication More Resilient (Robust)
===========================================================

## Learning Goal

Once you have a layer of indirection take advantage of it! Hystrix acts as a dependency [circuit breaker](http://martinfowler.com/bliki/CircuitBreaker.html): it throttles the calls to the client when the client service is not healthy. However, if an external service is not responding at all or too late we should provide an alternative result, the so-called 'fallback'. You will learn in this exercise how to leverage and configure Hystrix provided features.

The task is to provide a fallback implementation that is automatically called by Hystrix when the User service returns unexpected errors (e.g. HTTP status code 500) or does not respond in a specified time.

In this exercise we will test the `GetUserCommand` class, which is used internally by the `UserServiceClient` class.
As the `UserServiceClient` in the test is provided just as a mocked implementation (see [Exercise 16](Exercise_16_Call_UserService.md)), we will test directly against `GetUserCommand`.

## Prerequisite
Continue with your solution of the last exercise. If this does not work, you can checkout the branch [`origin/solution-17-Integrate-Hystrix`](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-17-Integrate-Hystrix).

## Step 1: Implement Tests
- In order to make outgoing request call substitutable by the test, we first need to prepare the `GetUserCommand` class: Use **Eclipse refactoring tools** to extract the code that does the request (`ResponseEntity<User> responseEntity = restTemplate.getForEntity(url, User.class);`) from the `run` method, into a **protected method `sendRequest`**. With that we can easily provoke long running and failing requests within the test.

- As part of the `src/test/java` source folder create a new class `GetUserCommandTest` in the package `com.sap.bulletinboard.ads.services` and copy the code from [here](https://github.com/SAP/cloud-bulletinboard-ads/blob/exercise-18-Make-Communication-Resilient/src/test/java/com/sap/bulletinboard/ads/services/GetUserCommandTest.java).

## Step 2: Provide Fallback Implementation
In this step we provide the required fallback implementation. This is necessary because our code relies on the response of the wrapped Hystrix command. Use the tests implemented in the `GetUserCommandTest` class to drive the implementation.

- Integrate the **test case** `responseTimedOutFallback` that is commented out. Try to understand what the test does, run the test and analyze why it fails. 
- Fix the failing test by overriding the `getFallback()` function within your `GetUserCommand` class. 
- Integrate the **test case** `responseErrorFallback` that is commented out. We expect that this test runs if a fallback function is implemented.
- As of now it is not transparent in the log, whether the `getFallback` method is executed or not and for which reason it is called (timeout, error or full thread pool). In the `GetUserCommand` class make use of the following methods: `isResponseTimedOut()`, `isFailedExecution()` and `isResponseRejected()` to log the information respectively. When running your Unit tests again, make sure that the log messages are written in a readable way.

**Note:** Here we concentrate on the technical issue of providing a fallback. In practice a lot of thought has to go into the decision what exactly to return when a fallback is used. One good idea might be to return a default (unauthorized) user. Another idea is to clearly communicate errors to the end user (without provoking a long timeout).

Have also a look into the last commit of the branch [solution 18](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-18-Make-Communication-Resilient) to see our sample solution.

## Step 3: Increase Timeout
Beside of providing a fallback implementation you also might want to overrule the default timeout setting of Hystrix (1000ms) for this command (group). We can do that programmatically and / or we can provide a configuration that is evaluated dynamically at runtime.

### Introduce HystrixCommandKey
In order to define command specific properties, you need to remove the argument `DEFAULT_TIMEOUT_MS` from the super constructor and follow the instructions to specify a `HystrixCommandKey` like "User.getById":
```java
public GetUserCommand(/*...*/) {
   super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("User"))
	.andCommandKey(HystrixCommandKey.Factory.asKey("User.getById"))); //<-- new
   // ...
}
```

### Alternative 1: Programmatic Configuration
You can define a command specific property programmatically. In your `GetUserCommand` constructor you need to pass a Setter in a manner similar to this:
```java
public GetUserCommand(/*...*/) {
   super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("User"))
        .andCommandKey(HystrixCommandKey.Factory.asKey("User.getById"))
        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(1500))); //<-- new
   // ...
}
```
With that Hystrix should not run into a timeout i.e. does not return an unauthorized fallback User. Don't forget to test this code change by creating some advertisements via `Postman` or by executing your tests.

### Alternative 2: Dynamic Configuration
You can define a command specific property dynamically, that gets loaded during runtime. Create a file named `config.properties` in the package `src/main/resources` with the following content:

```
 # HystrixCommandKey = User.getById
 hystrix.command.User.getById.execution.isolation.thread.timeoutInMilliseconds=2000
```

**Note:** Hystrix identifies our particular command via the `HystrixCommandKey`, which is `User.getById` in our case. The property value overrules the one defined in the code ("Alternative 1").
You can also define default properties for all (other) commands by specifying `default` instead of `User.getById`: `hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=2000`

## [Optional] Step 4: Hystrix Exceptions Discussed
There are mainly two exceptions exposed by a `HystrixCommand`:

1. `HystrixRuntimeException` that is thrown when a `HystrixCommand` fails. In case you raise an exception within your `run` method this gets wrapped in a `HystrixRuntimeException` with failure type `FailureType.COMMAND_EXCEPTION` and your exception as cause. Note that the `HystrixRuntimeException`, raised by the `run` method is not exposed by the `execute` method when the fallback succeeded.

2. `HystrixBadRequestException` that is the Hystrix’s equivalent of `IllegalArgumentException`. Unlike all other exceptions thrown by a `HystrixCommand` this **does not trigger the `getFallback` method** and does not count for circuit breaking. Note that `HystrixBadRequestException` does not extend `HystrixRuntimeException`.

You can make use of the `HystrixBadRequestException` for all outgoing requests that return a `4xx` http status code as also implemented [here](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-18-Make-Communication-Resilient/src/main/java/com/sap/bulletinboard/ads/services/GetUserCommand.java) 
([test case `responseHystrixBadRequest`](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-18-Make-Communication-Resilient/src/test/java/com/sap/bulletinboard/ads/services/GetUserCommandTest.java)). 


## [Optional] Step 5: Make Fallback Configurable by its Caller
When the same Hystrix command has different callers or is called in different contexts, the caller might wish to define a fallback that fits into its context. For providing a Java callback function we make use of lambda expressions (Java 8), which can be easily overwritten by the caller (in our case the `AdvertisementController`).

The easiest way to make the Hystrix fallback configurable is to add a function parameter which returns the specified dummy User object.
- You can extend the `GetUserCommand` constructor by an additional parameter `Supplier<User> fallbackFunction`, which represents a `FunctionalInterface`.
- Within your `GetUserCommand.getFallback()` implementation you can then call it via `fallbackFunction.get()`;
- On the consumer side (test)  you can make use of the following lambda expressions:
  - `this::dummyUser` - to reference a function of name `dummyUser`
  - `User::new` or `() -> { return new User(); }` - to return a new `User` object when called.

A Lambda example can be found in the branch `solution-18-2-Make-Fallback-Configurable-using-Lambda` [GetUserCommand](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-18-2-Make-Fallback-Configurable-using-Lambda/src/main/java/com/sap/bulletinboard/ads/services/GetUserCommand.java) 
 ([GetUserCommandTest](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-18-2-Make-Fallback-Configurable-using-Lambda/src/test/java/com/sap/bulletinboard/ads/services/GetUserCommandTest.java)). 

  
## Used Frameworks and Tools
- [Hystrix](https://github.com/Netflix/Hystrix)

## Further Reading
- [Hystrix - How to use fallbacks](https://github.com/Netflix/Hystrix/wiki/How-To-Use#Fallback)
- [Hystrix - Configuration](https://github.com/Netflix/Hystrix/wiki/Configuration)
- [Java8 Lambda Functional Programming](http://www.studytrails.com/java/java8/Java8_Lambdas_FunctionalProgramming.jsp) 
- [Java8 Lambda Tutorial](http://tutorials.jenkov.com/java/lambda-expressions.html)

***
<dl>
  <dd>
  <div class="footer">&copy; 2018 SAP SE</div>
  </dd>
</dl>
<hr>
<a href="Exercise_17_Introduce_Hystrix.md">
  <img align="left" alt="Previous Exercise">
</a>
<a href="Exercise_19_Transfer_CorrelationID.md">
  <img align="right" alt="Next Exercise">
</a>

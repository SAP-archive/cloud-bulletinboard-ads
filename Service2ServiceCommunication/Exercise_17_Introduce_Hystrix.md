Exercise 17: Introduce Hystrix
==============================

## Learning Goal
After this exercise you will know how to use Hystrix as wrapper for the call to the existing User service within your Advertisement microservice. With Hystrix it is possible to mitigate overload and error situations when using external services. Hystrix can throttle the client if the called service is not healthy. 

The task of this exercise is to wrap our User service call with the **Hystrix command pattern**.


## Prerequisite
Continue with your solution of the last exercise. If this does not work, you can checkout the branch [`origin/solution-16-Call-User-Service`](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-16-Call-User-Service).

## Step 1: Add Maven Dependency
Add the following dependencies to your `pom.xml` using the XML view of Eclipse:

- Add `hystrix-core` dependency:
```
<!-- Resilience and asynchronous requests -->
<dependency>
    <groupId>com.netflix.hystrix</groupId>
    <artifactId>hystrix-core</artifactId>
    <version>1.5.10</version>
</dependency>
```

Note: After you've changed the Maven settings, don't forget to update your Eclipse project (`ALT-F5`)! 

## Step 2: Create Hystrix Command
Create a `GetUserCommand` class in the `com.sap.bulletinboards.ads.services` package and provide the following implementation:
```java
public class GetUserCommand extends HystrixCommand<User> {
    // Hystrix uses a default timeout of 1000 ms, increase in case you run into problems in remote locations
    private static final int DEFAULT_TIMEOUT_MS = 1000;

    private String url;
    private RestTemplate restTemplate;

    public GetUserCommand(String url, RestTemplate restTemplate) {
        super(HystrixCommandGroupKey.Factory.asKey("User"), DEFAULT_TIMEOUT_MS);
        this.url = url;
        this.restTemplate = restTemplate;
    }

    @Override
    protected User run() throws Exception {
        //TODO send request and return User
        return null;
    }

    // this will be used in exercise 18
    protected int getTimeoutInMs() {
        return this.properties.executionTimeoutInMilliseconds().get();
    }
}
```

Now let's move the implementation of sending and handling the User GET request from the `UserServiceClient.isPremiumUser` into the `GetUserCommand.run` method.   
The execution of the Hystrix command remains in the `UserServiceClient.isPremiumUser` method.
```
User user = new GetUserCommand(url, restTemplate).execute();
```
**Note:** As the execute method potentially raises an `HystrixRuntimeException`, this would be the right place to handle and log it.

## Step 3: Test the Ads-Microservice Locally	

In this step we want to test the creation of an advertisement via `Postman`, which should call the User service.

As Hystrix provides some default configuration such as timeout after 1000ms you might face a timeout issue (usually only at the first call). In that context Hystrix expects a fallback implementation, which we are going to introduce in the [next exercise](Exercise_18_Make_Communication_Resilient.md). 

## Used Frameworks and Tools
- [Hystrix](https://github.com/Netflix/Hystrix)

***
<dl>
  <dd>
  <div class="footer">&copy; 2018 SAP SE</div>
  </dd>
</dl>
<hr>
<a href="Exercise_16_Call_UserService.md">
  <img align="left" alt="Previous Exercise">
</a>
<a href="Exercise_18_Make_Communication_Resilient.md">
  <img align="right" alt="Next Exercise">
</a>

Exercise 16: Call User Service (synchronous)
============================================

## Learning Goal
After this exercise you know how to call an existing service synchronously within your Advertisement microservice.

The task of this exercise is to call the User service to find out whether the current user is a premium user. Only then this user is allowed to create an advertisement. 
Technically we are going to use [`RestTemplate`](http://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html). The `RestTemplate` is the central Spring class for client-side HTTP access. Conceptually, it is very similar to the `JdbcTemplate`, `JmsTemplate`, and the various other templates found in the Spring Framework. This means, for instance, that the `RestTemplate` is thread-safe once constructed, and that you can use callbacks to customize its operations.

## Prerequisite
Continue with your solution of the last exercise. If this does not work, you can checkout the branch [`origin/solution-13-Use-SLF4J-Features`](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-13-Use-SLF4J-Features).

## Step 1: Test User Service Using a REST Client
Before we start with the implementation we want to get familiar with the User service. 

You can test the following REST service endpoints manually in the browser using the `Postman` Chrome plugin:
- `https://opensapcp5userservice.cfapps.eu10.hana.ondemand.com/api/v1.0/users` - returns all available users with their IDs.
- `https://opensapcp5userservice.cfapps.eu10.hana.ondemand.com/api/v1.0/users/{ID}` - returns the information for a user where {ID} is a placeholder for a user id, e.g. "42".

## Step 2: Add Maven Dependency
Add the dependency to the Apache http client to your `pom.xml` using the XML view of Eclipse:
```
<!-- Apache HTTP Client (closeable, configurable) -->
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.5.2</version>
    <exclusions>
        <exclusion>
            <artifactId>commons-logging</artifactId>
            <groupId>commons-logging</groupId>
        </exclusion>
    </exclusions>
</dependency>
```
- Note: After you've changed the Maven settings, don't forget to update your Eclipse project (`Alt+F5`)!

## Step 3: Create a User Service Client
The User service client hides the call to the RESTful User Webservice, provides JSON parsing and error handling.

Create a new class `UserServiceClient` in package `com.sap.bulletinboard.ads.services` and copy the code from [here](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-16-Call-User-Service/src/main/java/com/sap/bulletinboard/ads/services/UserServiceClient.java). Explanation: This class should offer the information whether a given user (`String id`) is a premium User or not. It makes use of the `RestTemplate` that gets injected via the constructor and needs to be defined as well. The route/URI to the User service is retrieved from an environment variable using the `@Value("${USER_ROUTE}")` annotation. The path is set to the endpoint `api/v1.0/users/{id}`. 

Create a new class `RestTemplateConfig` in package `com.sap.bulletinboard.ads.config` and copy the code from [here](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-16-Call-User-Service/src/main/java/com/sap/bulletinboard/ads/config/RestTemplateConfig.java).

**Explanation**
- This class makes use of the Apache `HttpClientBuilder` to build a `CloseableHttpClient` instance with a proxy, in case the environment variables `http.proxyHost` and `http.proxyPort` are set (for local execution). 
- By default the `RestTemplate` establishes and closes a connection on every HTTP request. As the SSL handshake is time-consuming, we've configured an HTTP connection pool to reuse connections by keeping the sockets open.
- Furthermore we've configured timeouts to specify how long to wait until a connection is established and how long a socket should be kept open (i.e. how long to wait for the (next) data package). By default there are no timeout settings sepcified. 
- Note that the configuration is application specific and the settings should be aligned with the hystrix settings (timout, threadpool size), which are introduced in [Exercise 18](Exercise_18_Make_Communication_Resilient.md).

## Step 4: Integrate UserServiceClient.isPremiumUser()

The `isPremiumUser` check should be executed before a new `Advertisement` is created. So the next step is to introduce the check at the right place in the `AdvertisementController` class. Hint: Please hard-code the User `id` for now (use `"42"` as this user is a premium user) as we do not have a way to specify the information about the current user in the incoming request, yet.

## Step 5: Test the Advertisement Microservice Locally

In this step we want to test the creation of an advertisement via Postman, which should call the User service. 

### Run in Eclipse IDE
Before you (re-)start your Tomcat webserver within Eclipse, you need to adapt the Tomcat configuration.

- Open the `Servers` view.
- Double-click the server instance and select the `Open launch configuration` link.
- Open the `Edit configuration` dialog. 
- switch to the `Environment` tab and add the following environment variables:
  - `USER_ROUTE=https://opensapcp5userservice.cfapps.eu10.hana.ondemand.com`
- switch to the `Arguments` tab and add the proxy settings to the VM arguments:
  - ` -Dhttp.proxyHost=proxy.wdf.sap.corp -Dhttp.proxyPort=8080`<sub><b>[to-do]</b></sub>

**Why are proxy settings required?** <sub><b>[to-do]</b></sub> If you run your service locally within the SAP corporate network, the host `opensapcp5userservice.cfapps.eu10.hana.ondemand.com` cannot be resolved. If you apply the proxy settings to the Java process (via VM arguments) then the SAP proxy is used which is able to resolve the host name. Settings in Eclipse are separate from the settings in the shell (bash), which in our IDE are defined in `~/.environment` and loaded at the start of each shell via `~/.bashrc`.

<sup>Note: In case you are getting a **null-pointer-exception** because `USER_ROUTE==null`, you probably created the `UserServiceClient` with `new` instead of `@Inject`. The latter is necessary since annotations in a class are not interpreted when you create the instance yourself with `new`.</sup>


### Alternatively: Run on Command Line
As described in [Exercise 1](../CreateMicroservice/Exercise_1_GettingStarted.md) you can also deploy the service on an embedded Tomcat using Maven.


## Step 6: Fix Tests
As we do not want our JUnit tests (`AdvertisementControllerTest`) to call third-party services, we need to introduce mocks for the `UserServiceClient` and as well for the `PropertySourcesPlaceholderConfigurer` to specify the `USER_ROUTE` variable. Similar to stub objects, mocks are object instances that just mock the original behavior and can be configured to behave in a certain way. 

Create a new @Configuration annotated class `TestAppContextConfig` in **test package** `com.sap.bulletinboard.ads.config` and copy the code from [here](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-16-Call-User-Service/src/test/java/com/sap/bulletinboard/ads/config/TestAppContextConfig.java).

```java
@Configuration
public class TestAppContextConfig {

    @Bean
    static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        // provide a mock so that the @Value annotation can be resolved even if the environment variable is not set
        PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
        Properties properties = new Properties();
        properties.setProperty("USER_ROUTE", ""); // ensures that USER_ROUTE is initialized with no "real" route
        pspc.setProperties(properties);
        return pspc;
    }
    
    @Bean
    @Primary // preferred bean when there are multiple candidates
    UserServiceClient userServiceClient() {
        // return a UserServiceClient mock that just returns "true" for isPremiumUser, without issuing a network request
        UserServiceClient userServiceClient = Mockito.mock(UserServiceClient.class);
        Mockito.when(userServiceClient.isPremiumUser(Mockito.anyString())).thenReturn(true);
        return userServiceClient;
    }
}
```
**Note:** The tests will only run, when the `placeholderConfigurer` bean gets instantiated before the `userServiceClient`. One option to ensure that is to have only one `placeholderConfigurer` bean registered in the ApplicationContext. You can make use of Spring `Profiles` here: annotate the productive `placeholderConfigurer` bean, declared in the `WebAppContextConfig` class, with `@Profile("cloud")` to make sure, that it is never loaded as part of your Test-ApplicationContext.

**Explanation:**  
- The `PropertySourcesPlaceholderConfigurer` is already registered in the `WebAppContextConfig` class; it resolves `@Value` annotations against the current Spring Environment and needs to provide other (dummy) values in the test context.
- Note: As of now the mock for the `UserServiceClient.isPremiumUser` method always returns true, i.e., the user is always a premium user. All your JUnit tests rely on that. If you analyze the Code Coverage for the JUnit tests, you will see that there is no JUnit test that tests the correct behaviour when the "User is unknown" or the "User is not a premium User" and as of now the user ID is hardcoded to “42”.

## Step 7: Push to Cloud Foundry

When pushing the application to Cloud Foundry, the `USER_ROUTE` needs to be configured as a system environment variable. This can easily be done in the `manifest.yml` file by adding another entry under `env`:
```
env:
    USER_ROUTE: 'https://opensapcp5userservice.cfapps.eu10.hana.ondemand.com'
``` 

## Used Frameworks and Tools
- [Postman REST Client (Chrome Plugin)](https://chrome.google.com/webstore/detail/postman/fhbjgbiflinjbdggehcddcbncdddomop)
- [Mockito - Mocking Framework](http://mockito.org/)
- [JDoc Spring RestTemplate](http://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html)
- [Apache Rest Client](http://hc.apache.org/httpcomponents-client-ga/)

## Further Reading
- [JSON Conversion using JacksonJsonProvider](../Knowledge/JSONConversion.md)
- [Spring RestTemplate](https://spring.io/blog/2009/03/27/rest-in-spring-3-resttemplate)
- [Mockito User Guide](https://docs.google.com/document/d/15mJ2Qrldx-J14ubTEnBj7nYN2FB8ap7xOn8GRAi24_A)
- [Spring Configuration](http://docs.spring.io/autorepo/docs/spring/4.1.1.RELEASE/javadoc-api/org/springframework/context/annotation/Configuration.html)


***
<dl>
  <dd>
  <div class="footer">&copy; 2018 SAP SE</div>
  </dd>
</dl>
<hr>
<div align="left">
    <a href="/LoggingTracing/Exercise_14_GettingStarted_With_ELK_Stack.md">
</div>    
<a href="/LoggingTracing/Exercise_14_GettingStarted_With_ELK_Stack.md">
  <img align="left" alt="Previous Exercise">
</a>
<a href="/Service2ServiceCommunication/Exercise_17_Introduce_Hystrix.md">
  <img align="right" alt="Next Exercise">
</a>

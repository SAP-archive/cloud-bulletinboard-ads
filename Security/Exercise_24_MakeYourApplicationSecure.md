# Exercise 24: Make your Application Secure

## Learning Goal
In the previous exercise you learned how you can protect your application with the application router. But unauthenticated and/or unauthorized requests could be sent directly to your app - bypassing the application router. Hence, the application itself must also ensure that only those requests are served which are sent from an authenticated and authorized user.

After this exercise you will know how to secure your application and introduce (domain specific) authorization checks.

Your task is to secure your application with the SAP Java Container Security library and the Spring Security framework, so that the application blocks all incoming requests if the user is not authenticated or has no authorization for the needed scope "$XSAPPNAME.Display".

Note: There is currently no easy way to make a subset of apps 'unreachable' via http(s) from the outside, e.g. by network segregation. But even if we had that capability, it would still be necessary to have authorization checks in the 'backend' for all sensitive operations.

## Prerequisite

Continue with your solution of the last exercise. If this does not work, you can checkout the branch [`solution-23-Setup-Generic-Authorization`](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-23-Setup-Generic-Authorization).

## Step 1: Integrate SAP Java Cloud Security library

The SAP Java Cloud Security library is available [here](https://github.com/SAP/cloud-security-xsuaa-integration) 
To use it add the following dependency to your `pom.xml` using the XML view of Eclipse:

```xml
<!-- Security -->
  <dependency>
    <groupId>com.sap.cloud.security.xsuaa</groupId>
    <artifactId>spring-xsuaa</artifactId>
    <version>2.6.0</version>
  </dependency>
  <dependency>
    <groupId>com.sap.cloud.security</groupId>
    <artifactId>java-security-test</artifactId>
    <version>2.6.0/version>
    <scope>test</scope>
  </dependency>
```

The second dependency adds functionality that we later use for testing security features.

You also need these additional spring dependencies:

```xml
<!-- Spring Security and other related libraries-->
<dependency> <!-- includes spring-security-oauth2 -->
    <groupId>org.springframework.security</groupId>
	<artifactId>spring-security-oauth2-jose</artifactId>
	<version>5.2.4.RELEASE</version>
</dependency>
<dependency>
	<groupId>org.springframework.security</groupId>
	<artifactId>spring-security-config</artifactId>
	<version>5.2.4.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
	<artifactId>spring-security-oauth2-resource-server</artifactId>
	<version>5.2.4.RELEASE</version>
</dependency>
<!-- END additional dependencies -->
```

- Note: After you've changed the Maven settings, don't forget to update your Eclipse project (`Alt+F5`)!

## Step 2: Configure Spring Security

### Add and modify `WebSecurityConfig` class
Create a `WebSecurityConfig` class in the package `com.sap.bulletinboard.ads.config` and copy the code from [here](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-24-Make-App-Secure/src/main/java/com/sap/bulletinboard/ads/config/WebSecurityConfig.java).

> You have now enabled security centrally on the web level. Besides that you have the option to do the authorization checks on method level using [Method Security](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#method-security-expressions).

### Activate Security by registering `springSecurityFilterChain` Servlet Filter
- In order to **activate** the Spring Security framework you need to add a servlet filter in the `AppInitializer.onStartup()` method.

```java
// register filter with name "springSecurityFilterChain"
servletContext.addFilter(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME,
                        new DelegatingFilterProxy(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME))
              .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
```
The `DelegatingFilterProxy` intercepts the requests and adds a `ServletFilter` chain between the web container and your web application, so that the Spring Security framework can filter out unauthenticated and unauthorized requests.


## Step 3: Setup Security for Component Tests

The service tests from [Exercise 4](../CreateMicroservice/Exercise_4_CreateServiceTests.md) are not affected by the above changes. They are still running even if the configuration in `WebSecurityConfig` class is loaded into the application context. We strongly recommend you to **activate** security for your service level tests to ensure automatically that all of your application endpoints are protected against unauthorized access. In this step you will learn to "fake" the security infrastructure, so that the Unit Tests can also test the security settings.

### Activate Security
- Like in the `AppInitializer.onStartup()` method we also need to make sure, that `springSecurityFilterChain` bean is added as filter to Mock MVC in the `AdvertisementControllerTest` test class:
```java
@Inject //use javax.servlet.Filter
private Filter springSecurityFilterChain;

@Before
public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilters(springSecurityFilterChain).build();
}    
```
- Now run your JUnit tests and see them failing because of unexpected `401` ("unauthenticated") status code.

### Fake Test Security Infrastructure
In productive environments, the security library reads the public key value from the environment variable `VCAP_SERVICES`. For unit tests, you can explicitly set the
public key of your test key pair using the `SecurityTestRule` from [java-security-test](https://github.com/SAP/cloud-security-xsuaa-integration/tree/master/java-security-test).
The `SecurityTestRule` takes the public key file from the resources. Since you included
the java security test library, a public/private key pair is already put into the resources and accessible from the resources path `/publicKey.txt` and `/privateKey.txt`.

## Step 4: Fix and Run Component Tests
### Generate a valid JWT Token
- Add the `SecurityTestRule` and a `jwt` field to the test class:

```java
@ClassRule
public static SecurityTestRule securityTestRule = SecurityTestRule.getInstance(Service.XSUAA)
                 .setKeys("/publicKey.txt", "/privateKey.txt");

private String jwt;
```

- Update the setup of the `AdvertisementControllerTest` test class according to the below code snippet:

```java
@Before
public void setUp() throws Exception {
    ...
  jwt = "Bearer " + securityTestRule.getPreconfiguredJwtGenerator()
          .withLocalScopes(WebSecurityConfig.DISPLAY_SCOPE_LOCAL, WebSecurityConfig.UPDATE_SCOPE_LOCAL)
          .createToken()
          .getTokenValue();
}
```

> Note: The class `JwtGenerator` has the responsibility to generate a JWT Token for those scopes which are passed to the `withLocalScopes()` method. It returns the token in a format that is suitable for the HTTP `Authorization` header. The generator signs the JWT Token with the private key that was set on the `SecurityTestRule`.

### Add `Authorization` header to each HTTP request
The class `AdvertisementControllerTest` must further be updated in those locations where the test performs a HTTP method call. All HTTP method calls must be updated with an HTTP header field of name `Authorization` and value `jwt`. For example:

Before... ```get(AdvertisementController.PATH + "/" + id)```

After...  ```get(AdvertisementController.PATH + "/" + id).header(HttpHeaders.AUTHORIZATION, jwt)```


### Add `TestPropertySource` to the test class

The security library creates a configuration object that fits the environment it is run in by parsing the `VCAP_SERVICES` environment variable. For the test we want to
override that parsing with test settings. This can be done by adding the `@TestPropertySource` annotation right before the test class declaration.
Here the required properties can be overriden so that they match the inforamtion that is contained in the `jwt` token that is generated for the test.
To simplify the setup we use the defaults from the `SecurityTestRule`

```java
@TestPropertySource(properties = {
        "xsuaa.uaadomain=" + SecurityTestRule.DEFAULT_DOMAIN,
        "xsuaa.xsappname=" + SecurityTestRule.DEFAULT_APP_ID,
        "xsuaa.clientid=" + SecurityTestRule.DEFAULT_CLIENT_ID })
//@formatter:off
public class AdvertisementControllerTest {
...
```

### Run JUnit tests
Now you can run the JUnit tests as described [in Exercise 4](../CreateMicroservice/Exercise_4_CreateServiceTests.md). They should succeed now.


## Step 5: Run and Test the Service Locally

In this step you prepare the local run environment and test your application manually using `Postman` to discover that your application is now secure.

### Prepare `VCAP_SERVICES`
Based on the `VCAP_SERVICES` environment variable the `spring-security` module instantiates the `SecurityContext`.

- In Eclipse, open the Tomcat server settings (by double-clicking on the server) and then open the launch configuration. In the Environment tab edit the `VCAP_SERVICES` variable and replace the value with the following:
```javascript
{"postgresql-9.3":[{"name":"postgresql-lite","label":"postgresql-9.3","credentials":{"dbname":"test","hostname":"127.0.0.1","password":"test123!","port":"5432","uri":"postgres://testuser:test123!@localhost:5432/test","username":"testuser"},"tags":["relational","postgresql"],"plan":"free"}],"xsuaa":[{"credentials":{"clientid":"sb-clientId!t0815","clientsecret":"dummy-clientsecret","identityzone":"<<your tenant>>","identityzoneid":"a09a3440-1da8-4082-a89c-3cce186a9b6c","tenantid":"a09a3440-1da8-4082-a89c-3cce186a9b6c","uaadomain":"localhost","tenantmode":"shared","url":"dummy-url","verificationkey":"-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAm1QaZzMjtEfHdimrHP3/2Yr+1z685eiOUlwybRVG9i8wsgOUh+PUGuQL8hgulLZWXU5MbwBLTECAEMQbcRTNVTolkq4i67EP6JesHJIFADbK1Ni0KuMcPuiyOLvDKiDEMnYG1XP3X3WCNfsCVT9YoU+lWIrZr/ZsIvQri8jczr4RkynbTBsPaAOygPUlipqDrpadMO1momNCbea/o6GPn38LxEw609ItfgDGhL6f/yVid5pFzZQWb+9l6mCuJww0hnhO6gt6Rv98OWDty9G0frWAPyEfuIW9B+mR/2vGhyU9IbbWpvFXiy9RVbbsM538TCjd5JF2dJvxy24addC4oQIDAQAB-----END PUBLIC KEY-----","xsappname":"xsapp!t0815"},"label":"xsuaa","name":"uaa-bulletinboard","plan":"application","tags":["xsuaa"]}]}
```
- If you run the application from the command line, update your `localEnvironmentSetup` script accordingly to  [`localEnvironmentSetup.sh`](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-24-Make-App-Secure/localEnvironmentSetup.sh) ([`localEnvironmentSetup.bat`](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-24-Make-App-Secure/localEnvironmentSetup.bat))

> Note: With this configuration we can mock the XSUAA backing service as we make use of so-called "offlineToken verification". Having that we can simulate a valid JWT Token to test our service as described below.

### Generate JWT Token
Before calling the service you need to provide a digitally signed JWT token to simulate that you are an authenticated user. 
- Therefore simply set a breakpoint in the `setUp` method of the `AdvertisementControllerTest` and run the `JUnit` tests again to fetch the value of `jwt` from there. 

> Explanation: The generated JWT Token is an "individual one" as it
>  - contains specific scope(s) e.g. `xsapp!t0815.Display`. Furthermore note that the scope is composed of **xsappname** e.g. `xsapp!t0815` which also needs to be the same as provided as part of the `VCAP_SERVICES`--`xsuaa`--`xsappname`
>  - it is signed with a private key that fits to the public key that is provided as part of the `VCAP_SERVICES`--`xsuaa`--`verificationkey`

### Call local Service 
Now you can test the service manually in the browser using the `Postman` chrome plugin. 

- You should get for any endpoint (except for `\health`) an `401` ("unauthorized") status code. 
- Add a header field `Authorization` with the value of the **generated JWT token**.
- Then you can check whether you are able to request the `/api/v1/ads` endpoints. In case your offlineToken verification fails, make sure that the `VCAP_SERVICES` environment variable is provided on Tomcat as described above, another restart might be required.


## Step 6: Deploy and Test
In this step you are going to deploy your application to Cloud Foundry and discover that you are not any longer authorized to call your service endpoints directly. This is due to to fact that the necessary scopes are not (yet) assigned to your user account. Unlike in the previous steps, your application is now running in a productive security environment which enforces the current existing security policy.

### Bind UAA Service to your application
Before deploying your application to Cloud Foundry you need to bind your application to the UAA service. 
- As part of the `manifest.yml` you need to enhance the list of services bound to the `bulletinboard-ads` application with the name of your XSUAA service:
```
- name: bulletinboard-ads
  services:
  ...
  - uaa-bulletinboard
```
- Now re-deploy your application to Cloud Foundry.

### Call deployed service
- Call your service endpoints e.g. `https://bulletinboard-ads-<<your user id>>.cfapps.<<region>>.hana.ondemand.com` manually using the `Postman` Chrome plugin. You should get for any endpoint (except for `\health`) an `401` ("unauthorized") status code. 
- On Cloud Foundry it is not possible to provide a valid JWT token which is accepted by the XSUAA. Therefore if you like to provoke a `403` ("forbidden", "insufficient_scope") status code **you need to call your application via the `approuter`** e.g.  
`https://<<your tenant>>-approuter-<<your user id>>.cfapps.<<region>>.hana.ondemand.com/ads/api/v1/ads` in order to authenticate yourself and to create a JWT Token with no scopes. **BUT** you probably will get as response the login screen in HTML. That's why you need to
  - enable the `Interceptor` within `Postman`. You might need to install another [`Postman Interceptor` Chrome Plugin](https://chrome.google.com/webstore/detail/postman-interceptor/aicmkgpgakddgnaphhhpliifpcfhicfo), which will help you to send requests using browser cookies through the `Postman` app. 
  - logon via `Chrome` Browser first and then
  - back in `Postman` resend the request e.g.  
    `https://<<your tenant>>-approuter-<<your user id>>.cfapps.<<region>>.hana.ondemand.com/ads/api/v1/ads` and
  - make sure that you now get a `403` status code.

> **Note:**  
> By default the application router enables **CSRF protection** for any state-changing HTTP method. That means that you need to provide a `x-csrf-token: <token>` header for state-changing requests. You can obtain the `<token>` via a `GET` request with a `x-csrf-token: fetch` header to the application router.


## Further Reading
- [Spring Security Reference](http://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#abstractsecuritywebapplicationinitializer)
- [Expression-Based Access Control](https://docs.spring.io/spring-security/site/docs/3.0.x/reference/el-access.html)
- [Cloud Application Security Samples](https://github.com/SAP/cloud-application-security-sample/)


***
<dl>
  <dd>
  <div class="footer">&copy; 2018 SAP SE</div>
  </dd>
</dl>
<hr>
<a href="Exercise_23_SetupGenericAuthorization.md">
  <img align="left" alt="Previous Exercise">
</a>
<a href="Exercise_24_Part2_Administrate_Authorization.md">
  <img align="right" alt="Next Exercise">
</a>

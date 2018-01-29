# Exercise 24: Make your Application Secure

## Learning Goal
In the previous exercise you learned how you can protect your application with the application router. But unauthenticated and/or unauthorized requests could be sent directly to your app - bypassing the application router. Hence, the application itself must also ensure that only those requests are served which are sent from an authenticated and authorized user.

After this exercise you will know how to secure your application and introduce (domain specific) authorization checks.

Your task is to secure your application with the SAP Java Container Security library and the Spring Security framework, so that the application blocks all incoming requests if the user is not authenticated or has no authorization for the needed scope "$XSAPPNAME.Display".

Note: There is currently no easy way to make a subset of apps 'unreachable' via http(s) from the outside, e.g. by network segregation. But even if we had that capability, it would still be necessary to have authorization checks in the 'backend' for all sensitive operations.

## Prerequisite

Continue with your solution of the last exercise. If this does not work, you can checkout the branch [`solution-23-Setup-Generic-Authorization`](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-23-Setup-Generic-Authorization).

## Step 1: Add Maven Dependencies

Add the following dependencies to your `pom.xml` using the XML view of Eclipse:

It suffices to add the direct dependency on the SAP Java Container Security library, because the library itself depends on the Spring Security libraries and the indirect dependency on the Spring Security framework will be resolved automatically.

- Add the `java-container-security` dependency, **in case you haven't done already as part of Exercise 22**:
```
<!-- Security -->
<dependency>
    <groupId>com.sap.xs2.security</groupId>
    <artifactId>java-container-security</artifactId>
    <version>0.26.4</version> 
</dependency>
```
- Note: After you've changed the Maven settings, don't forget to update your Eclipse project (`Alt+F5`)!

- Note: You can get the current version of the SAP Java Container Security library from [SAP Service Marketplae](https://launchpad.support.sap.com/#/softwarecenter/template/products/%20_APP=00200682500000001943&_EVENT=DISPHIER&HEADER=Y&FUNCTIONBAR=N&EVENT=TREE&NE=NAVIGATE&ENR=73555000100200004333&V=MAINT&TA=ACTUAL&PAGE=SEARCH/XS%20JAVA%201) (the filename currently is XS_JAVA_8-70001362.ZIP althrough version/filename may change in the future).


## Step 2: Configure Spring Security

### Add and modify `WebSecurityConfig` class
- Create a `WebSecurityConfig` class in the package `com.sap.bulletinboard.ads.config` and copy the code from [here](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-24-Make-App-Secure/src/main/java/com/sap/bulletinboard/ads/config/WebSecurityConfig.java).
- Change the value of field `XSAPPNAME` from `"bulletinboard-d012345"` to `"bulletinboard-<Your d/c/i-User>"`.  
**Note:** The value of `private static final String XSAPPNAME` must be equal to the value of `xsappname`, that is defined in your `xs-security.json` file. 


#### Explanation
The responsibility of the `WebSecurityConfig` class is to **__configure__** the Spring Security framework. The class represents a java based configuration. You can use this class as a template for your own applications. Change the `antMatchers` method with the respective HTTP method and URL pattern of the service endpoint(s) to be secured. Change the `access` method with the authorization a user requires for that URL pattern. You can add as many `antMatchers(...).access(...)` method chains as you wish.

Technically - under the hood - the default [`AffirmativeBased`](https://docs.spring.io/spring-security/site/docs/4.2.1.RELEASE/apidocs/org/springframework/security/access/vote/AffirmativeBased.html) `AccessDecisionManager` is used. This holds the `WebExpressionVoter`, which in turn makes use of the `OAuth2WebSecurityExpressionHandler` that handles Spring EL expressions like `hasRole` or `isAuthenticated` ([read more](https://docs.spring.io/spring-security/site/docs/3.0.x/reference/el-access.html)). If you require more than one Voter you can specify a "custom" `AccessDecisionManager` such as [`UnanimousBased`](https://docs.spring.io/spring-security/site/docs/4.2.1.RELEASE/apidocs/org/springframework/security/access/vote/UnanimousBased.html).

### Activate Security by registering `springSecurityFilterChain` Servlet Filter
- In order to **activate** the Spring Security framework you need to add a servlet filter in the `AppInitializer.onStartup()` method.

```java
// register filter with name "springSecurityFilterChain"
servletContext.addFilter(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME,
                        new DelegatingFilterProxy(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME))
              .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
```
The `DelegatingFilterProxy` intercepts the requests and adds a `ServletFilter` chain between the web container and your web application, so that the Spring Security framework can filter out unauthenticated and unauthorized requests.

### Note on how to enable security checks on method level
You have enabled security centrally on the web level as defined in `WebSecurityConfig`. Besides that you have the option to do the authorization checks on method level using [Method Security](http://docs.spring.io/autorepo/docs/spring-security/current/reference/htmlsingle/#jc-method).

## Step 3: Setup Security for Component Tests

The service tests from [Exercise 4](../CreateMicroservice/Exercise_4_CreateServiceTests.md) are not affected by the above changes. They are still running even if the configuration in `WebSecurityConfig` class is loaded into the application context. We strongly recommend you to **activate** security for your service level tests to ensure automatically that all of your application endpoints are protected against unauthorized access.  In this step you will learn to "fake" the security infrastructure, so that the Unit Tests can also test the security settings.

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
- Create folder `cc-bulletinboard-ads/src/test/resources` and copy the files [privateKey.txt](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-24-Make-App-Secure/src/test/resources/privateKey.txt), [publicKey.txt](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-24-Make-App-Secure/src/test/resources/publicKey.txt) into the new folder.

- Copy the implementation of the `TestSecurityConfig` class from [here](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-24-Make-App-Secure/src/test/java/com/sap/bulletinboard/ads/config/TestSecurityConfig.java) into the **test package** named `com.sap.bulletinboard.ads.config`.  
In productive environments, `SAPOfflineTokenServicesCloud` reads the public key value from the environment variable `VCAP_SERVICES`. For unit tests, you explicitly set the public key of your test key pair with the `JwtGenerator`. The `JwtGenerator` takes the public key from the `publicKey.txt` file.

- Copy the implementation of the `JwtGenerator` class from [here](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-24-Make-App-Secure/src/test/java/com/sap/bulletinboard/ads/testutils/JwtGenerator.java) into a new test package named `com.sap.bulletinboard.ads.testutils`.


## Step 4: Fix and Run Component Tests
### Generate a valid JWT Token
- Update the setup of the `AdvertisementControllerTest` test class according to the below code snippet:
```java
private String jwt;

@Before
public void setUp() throws Exception {
    ...
    // compute valid token with Display and Update scopes
    // tenant specific XSAPPNAME (appid) looks like <xsappname>!t<tenant specific index> 
    jwt = new JwtGenerator().getTokenForAuthorizationHeader("bulletinboard-d012345!t27.Display", "bulletinboard-d012345!t27.Update"); 
}
```
Note: The class `JwtGenerator` has the responsibility to generate a JWT Token for those scopes which are passed to the `getTokenForAuthorizationHeader()` method as a String array. It returns the token in a format that is suitable for the HTTP `Authorization` header. The generator signs the JWT Token with its private key (taken from file `privateKey.txt`).

### Add `Authorization` header to each HTTP request
The class `AdvertisementControllerTest` must further be updated in those locations where the test performs a HTTP method call. All HTTP method calls must be updated with an HTTP header field of name `Authorization` and value `jwt`. For example:  

Before... ```get(AdvertisementController.PATH + "/" + id)```  

After...  ```get(AdvertisementController.PATH + "/" + id).header(HttpHeaders.AUTHORIZATION, jwt)```  


### Run JUnit tests
Now you can run the JUnit tests as described [in Exercise 4](../CreateMicroservice/Exercise_4_CreateServiceTests.md). They should succeed now.


## Step 5: Run and Test the Service Locally

In this step you prepare the local run environment and test your application manually using `Postman` to discover that your application is now secure.

### Prepare `VCAP_SERVICES`
Based on the `VCAP_SERVICES` environment variable the `spring-security` module instantiates the `SecurityContext`.

- In Eclipse, open the Tomcat server settings (by double-clicking on the server) and then open the launch configuration. In the Environment tab edit the `VCAP_SERVICES` variable and replace the value with the following:
```javascript
{"rabbitmq-lite":[{"credentials":{"hostname":"127.0.0.1","password":"guest","uri":"amqp://guest:guest@127.0.0.1:5672","username":"guest"},"name":"rabbitmq-lite","label":"rabbitmq-lite","tags":["rabbitmq33","rabbitmq","amqp"]}],"postgresql-9.3":[{"name":"postgresql-lite","label":"postgresql-9.3","credentials":{"dbname":"test","hostname":"127.0.0.1","password":"test123!","port":"5432","uri":"postgres://testuser:test123!@localhost:5432/test","username":"testuser"},"tags":["relational","postgresql"],"plan":"free"}],"xsuaa":[{"credentials":{"clientid":"testClient!t27","clientsecret":"dummy-clientsecret","identityzone":"uaa","url":"dummy-url","verificationkey":"-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAn5dYHyD/nn/Pl+/W8jNGWHDaNItXqPuEk/hiozcPF+9l3qEgpRZrMx5ya7UjGdvihidGFQ9+efgaaqCLbk+bBsbU5L4WoJK+/t1mgWCiKI0koaAGDsztZsd3Anz4LEi2+NVNdupRq0ScHzweEKzqaa/LgtBi5WwyA5DaD33gbytG9hdFJvggzIN9+DSverHSAtqGUHhwHSU4/mL36xSReyqiKDiVyhf/y6V6eiE0USubTEGaWVUANIteiC+8Ags5UF22QoqMo3ttKnEyFTHpGCXSn+AEO0WMLK1pPavAjPaOyf4cVX8b/PzHsfBPDMK/kNKNEaU5lAXo8dLUbRYquQIDAQAB-----END PUBLIC KEY-----","xsappname":"bulletinboard-d012345"},"tags":["xsuaa"]}]}
```
- If you run the application from the command line, update your `localEnvironmentSetup` script accordingly to  [`localEnvironmentSetup.sh`](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-24-Make-App-Secure/localEnvironmentSetup.sh) ([`localEnvironmentSetup.bat`](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-24-Make-App-Secure/localEnvironmentSetup.bat))
- In both cases make sure that you've changed the value of field `xsappname` from "bulletinboard-d012345" to "bulletinboard-<Your d/c/i-User>".

Note: With this configuration we can mock the XSUAA backing service as we make use of so-called "offlineToken verification". Having that we can simulate a valid JWT Token to test our service as described below.

### Generate JWT Token
Before calling the service you need to provide a digitally signed JWT token to simulate that you are an authenticated user. 
- Therefore simply set a breakpoint in `JWTGenerator.getTokenForAuthorizationHeader()` in package `com.sap.bulletinboard.ads.testutils` and run the `JUnit` tests again to fetch the value of `jwt` from there. 

#### Explanation
The generated JWT Token is an "individual one" as it
 - contains specific scope(s) e.g. `bulletinboard-d012345.Display` (as defined in your `WebSecurityConfig` class). Furthermore note that the scope is composed of **xsappname** e.g. `bulletinboard-d012345` which also needs to be the same as provided as part of the `VCAP_SERVICES`--`xsuaa`--`xsappname`
 - it is signed with a private key that fits to the public key that is provided as part of the `VCAP_SERVICES`--`xsuaa`--`verificationkey`

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

### Call Deployed service
- Call your service endpoints e.g. `https://bulletinboard-ads-d012345.cfapps.sap.hana.ondemand.com` manually using the `Postman` Chrome plugin. You should get for any endpoint (except for `\health`) an `401` ("unauthorized") status code. 
- On Cloud Foundry it is not possible to provide a valid JWT token which is accepted by the XSUAA. Therefore if you like to provoke a `403` ("forbidden", "insufficient_scope") status code **you need to call your application via the `approuter`** e.g. `https://d012345trial-approuter-d012345.cfapps.sap.hana.ondemand.com/ads/api/v1/ads` in order to authenticate yourself and to create a JWT Token with no scopes. **BUT** you probably will get as response the login screen in HTML. That's why you need to
  - enable the `Interceptor` within `Postman`. You might need to install another [`Postman Interceptor` Chrome Plugin](https://chrome.google.com/webstore/detail/postman-interceptor/aicmkgpgakddgnaphhhpliifpcfhicfo), which will help you to send requests using browser cookies through the `Postman` app. 
  - logon via `Chrome` Browser first and then
  - back in `Postman` resend the request e.g. `https://d012345trial-approuter-d012345.cfapps.sap.hana.ondemand.com/ads/api/v1/ads` and
  - make sure that you now get a `403` status code.

> **Note:**  
> By default the application router enables **CSRF protection** for any state-changing HTTP method. That means that you need to provide a `x-csrf-token: <token>` header for state-changing requests. You can obtain the `<token>` via a `GET` request with a `x-csrf-token: fetch` header to the application router.


## Further Reading
- [Spring Security Reference](http://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#abstractsecuritywebapplicationinitializer)
- [Expression-Based Access Control](https://docs.spring.io/spring-security/site/docs/3.0.x/reference/el-access.html)



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

Exercise 4: Create Automated Component Tests
==========================================

## Learning Goal
After this exercise you will know how to setup an automated way to deploy and test the service again and again. You will learn how to setup a JUnit test suite that will test the `AdvertisementController` on an embedded Tomcat web server.

The task of this exercise is to implement a **JUnit test suite** that tests the correct behavior of the provided HTTP methods as specified below. Technically you will leverage Spring **Mock Mvc** for mocking all the mechanics of Spring MVC and executing HTTP requests against controllers. This will enable you to test your controllers without firing up a web server like Tomcat.


| HTTP Verb |  CRUD      | collection (e.g. `/api/v1/ads/`)   | specific item (e.g. `/api/v1/ads/0`)|   
| ----------- | ---------- | -------------------------------------- | --------------------------------------- |
| POST        | Create     | 201 (Created), single ad, `Location` header with link to `/api/v1/ads/{id}` | 405 (Method not allowed) |
| GET         | Read       | 200 (OK), list of advertisements | 200 (OK), single ad; 404 (Not Found), if no advertisement with this ID exists |

## Prerequisite
Continue with your solution of the last exercise. If this does not work, you can checkout the branch [`solution-3-Create-Ads-Endpoints`](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-3-Create-Ads-Endpoints).

## Step 1: Create JUnit Test Suite
In Java the test classes are typically separated from the source code so that Maven can package the microservice as application without the test code. That means that the test classes are stored in another root directory, while the package structure is identical to the structure in `src/main`:

./src/**main**/java/com/sap/bulletinboard/ads/controllers/AdvertisementController.java
./src/**test**/java/com/sap/bulletinboard/ads/controllers/AdvertisementController**Test**.java

As the above example shows, the test class is named like the class under test with an additional `Test` suffix.
- Note: While JUnit itself does not care about file names, the maven `surefire plugin` will only look for files whose names begin or end with `Test` (and [some other patterns](http://maven.apache.org/surefire/maven-surefire-plugin/examples/inclusion-exclusion.html)). Therefore you have to name the test classes according to those conventions. You run these tests with `mvn clean verify`.


In Eclipse within the (source) folder named **`src/test/java`** create an `AdvertisementControllerTest` class in the package `com.sap.bulletinboard.ads.controllers` and copy the code from [here](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-4-Create-ServiceTests/src/test/java/com/sap/bulletinboard/ads/controllers/AdvertisementControllerTest.java).


**Some JUnit Explanations**
- `@Test` marks a test method
- `@Before` is executed before each test method
- We use the `assertThat` method, a standard set of **Hamcrest**'s and **MockMvc** matchers, that we provide using static imports: 
```java
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
```

## Step 2: Run JUnit Tests in Eclipse

To run or debug the JUnit tests within your Eclipse IDE:
- Right-click on the project's root and select `Run as - JUnit Test` or `Debug as - JUnit Test`.
- This will open the `JUnit` view and display the test results.


## [Optional] Step 3: Run JUnit Tests on the Command Line

The tests can also be executed on the command line as follows:
```
$ mvn clean test
```
- Ensure that the Maven build was successful.
- Ensure that all your tests are executed.
  - Note: At this point you will probably still have 3 failures since the tests were not fully implemented yet.

## Step 4: Measure Code Coverage in Eclipse
We want to measure the code coverage within Eclipse using the [`Eclemma` eclipse plugin](https://marketplace.eclipse.org/content/eclemma-java-code-coverage).

To measure the code covered by the JUnit tests:
- Right-click on the project's root and select `Coverage as - JUnit Test`.
- This will open the `Coverage` view and display the coverage results. You can analyze the code which is not covered by the tests.

> You can install the `Eclemma` plugin from the Eclipse Marketplace and finalize the installation by restarting your Eclipse IDE. 

## Step 5: Implement Tests
Now we need to implement the three remaining test methods - **one at a time**.

**Implementation Notes**
- Run these tests regularly!
- Please ensure that you've understood the code as this serves you as base for the other tests. The test class contains a `create` test case that gives you an example on how to invoke a RESTful WebService in MockMVC using the following pattern:
```java
MockHttpServletResponse response = mockMvc.perform(RequestBuilder requestBuilder)
                                    .andExpect(ResultMatcher matcher)
                                    .andReturn().getResponse();
```
- In order to build a POST request you can make use of the static methods provided by the [`MockMvcRequestBuilders`](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/test/web/servlet/request/MockMvcRequestBuilders.html) request builder like `post("/api/v1/ads").content(toJson(advertisement)).contentType(APPLICATION_JSON);`. Note that you are responsible to convert the Advertisement object into JSON String and vice versa.
- Make use of [`MockMvcResultMatchers`](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/test/web/servlet/result/MockMvcResultMatchers.html) like `status()`, `header()`, `content()` and `jsonPath()` (see [path examples](https://github.com/json-path/JsonPath#path-examples)) to validate the response. Alternatively you can extract further information from the returned response ([`MockHttpServletResponse`](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/mock/web/MockHttpServletResponse.html)).
- Hint: extract duplicate code into private test helper methods to encourage code reuse by other tests.

## [Optional] Step 6: Advanced Test
For POST requests creating a new object instance, it is common to provide a URL which can be used to access this new object as part of the response. Implement a test that ensures that the location header field of the POST request points to a valid URL (starting with `http://`).

## FAQ
#### Q: Can the execution order of the JUnit tests be determined?
No. Tests should be able to run independently from each other.
Use annotations @Before and @After within each test to create/destroy controllers and therefore to manage the setup/tear down of required objects and controllers.

#### Q: How to autocomplete static imports such as hamcrest matchers

`Window` > `Preferences` > `Java` > `Editor` > `Content Assist` > `Favorites`

![](/CreateMicroservice/images/EclipseContentAssistForStaticImports.png)
Find a detailed step-by-step description on [Eclipse help](http://help.eclipse.org/mars/index.jsp?topic=%252Forg.eclipse.jdt.doc.user%252Ftips%252Fjdt_tips.html).

#### Q: When using ResultMatcher.json() method I get a java.lang.ClassNotFoundException
Next to `jsonPath` using the [JayWay JsonPath library](https://github.com/json-path/JsonPath) you can also make use of `json` ResultMatcher. When using that you need to add another dependency to Maven, namely [skyscreamer's JSONassert](https://github.com/skyscreamer/JSONassert) as described [here](http://www.baeldung.com/jsonassert).

***

<dl>
  <dd>
  <div class="footer">&copy; 2018 SAP SE</div>
  </dd>
</dl>
<hr>
<a href="Exercise_3_CreateAdsEndpoints.md">
  <img align="left" alt="Previous Exercise">
</a>
<a href="Exercise_4_Part2_CreateAdditionalAdsEndpoints.md">
  <img align="right" alt="Next Exercise">
</a>


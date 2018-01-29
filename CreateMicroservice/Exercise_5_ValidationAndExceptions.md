[Optional] Exercise 5: Introduce Validations and Exception Handler
=======================================================

## Learning Goal
After this exercise you will know how to validate method arguments and properties automatically using annotations defined as part of the [Java Validation API (a.k.a. JSR-303)](https://docs.oracle.com/javaee/7/api/javax/validation/constraints/package-summary.html), like `@NotNull` or `@Size`. Furthermore, you will learn how exceptions can be mapped to specific http status codes or error messages. 

The task of this exercise is to add validation for one possible error case, and show a corresponding error message to the user.


## Prerequisite
Continue with your solution of the last exercise. If this does not work, you can checkout the branch [origin/solution-4-2-DeleteUpdate](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-4-2-DeleteUpdate).

## Step 1: Add Maven Dependencies

The `Hibernate Validator` is the reference implementation of JSR-303/JSR-349 bean validation (not to be confused with `Hibernate` JPA implementation). In order to make Java Validation work in Spring MVC you need to make sure that an implementation of the Java API, such as Hibernate Validator, is in the project’s classpath and 

Add the following dependencies to your `pom.xml` using the XML view of Eclipse:

Add the `hibernate-validator` dependency:
```
<!-- Bean validation (@NotNull etc.) -->
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>5.2.1.Final</version>
</dependency>
```

Add the `javax.el-api` dependency
```
<!-- MethodValidationPostProcessor relies on Expression Language -->
<dependency>
	<groupId>javax.el</groupId>
	<artifactId>javax.el-api</artifactId>
	<version>3.0.1-b04</version>
</dependency>
```

- After you've changed the Maven settings, don't forget to update your Eclipse project! To do so, right click on your Eclipse project and select `Maven` - `Update Project ...`  (`Alt+F5`)

- Furthermore there need to be a [`MethodValidationPostProcessor`](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/validation/beanvalidation/MethodValidationPostProcessor.html) bean registered in the application context that delegates to a JSR-303 provider for performing method-level validation. You can add the bean for example as part of the `WebAppContextConfig` class:
```java
@Bean
public MethodValidationPostProcessor methodValidationPostProcessor() {
   return new MethodValidationPostProcessor();
}
```

## Step 2: Implement REST Argument Validation Test-driven
- Extend the `AdvertisementControllerTest` class and add a test which does a GET request using the ID "-1". Your expectation is that the server responds with an HTTP status code `400` (Bad Request).
But actually the test fails either because of HTTP status code `404` (as no advertisement with ID -1 exists), or HTTP status code `204` (No Content).

- To implement a validation of the ID path parameter you need to add the `@Min(0)` annotation to your ID argument in the `AdvertisementController` class. This indicates that only non-negative IDs are valid for this method. And the class needs to be annotated with `@Validated` annotation, for their methods to be searched for inline constraint annotations. 

**Notice that the test still fails** as the server responds now with a `javax.validation.ConstraintViolationException` exception (500,  Internal Server Error), which needs now to be mapped to `400` as explained in the next step.


## Step 3: Use ExceptionMapper
Currently an Internal Server Error (HTTP code 5xx) is returned when requesting using a negative ID. But, even more appropriate would be a client error (HTTP code 4xx)! Thus, change your test and assert that HTTP code 400 (Bad Request) is returned.

- To fix the failing test, we want to implement an exception handler which automatically transforms exceptions of type `ConstraintViolationException` to respond with an HTTP code 400 (Bad Request). This response should also include a meaningful error message.

- Create a class `CustomExceptionMapper` in the package `com.sap.bulletinboard.ads.controllers` and copy the code from [here](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-5-ValidationExceptions/src/main/java/com/sap/bulletinboard/ads/controllers/CustomExceptionMapper.java).

- Then, make sure the test succeeds. Furthermore when triggering a request in Postman, check that the error message contains a helpful description and **indicates the erroneous value**. 

- the user-facing `message` SHOULD be localizable and 
- next to the `message` field also a `code` field MUST be provided that is *"a technical code of the error situation to be used for support purposes"*.

## Step 4: Implement Entity Validation Test-driven
Similar to the `@Min` annotation we can also use `@NotNull` or `@NotBlank` ([Hibernate Validator](https://docs.jboss.org/hibernate/validator/6.0/api/org/hibernate/validator/constraints/package-summary.html)).

- Write a test creating a new advertisement with `title` set to `null` or even "". Your expectation is that the server responds with an HTTP status code `400` (Bad Request).
- To fix the test, extend the definition of the `title` field in your `Advertisement` class using `@NotBlank`.
Furthermore you need to add the `@Valid` annotation to the argument of the POST request method, to trigger the verification of the passed argument.

### Detail Notes
| Annotation   | JSR-303 standard  | Description |   
| -----------  | ----------------- | ---------- |
| `@NotNull`   | yes               | The CharSequence, Collection, Map or Array object is not null, but can be empty. |
| `@NotEmpty`  | Hibernate         | The CharSequence, Collection, Map or Array object is not null and size > 0. |
| `@NotBlank`  | Hibernate         | The string is not null and the trimmed length is greater than zero. |


***
<dl>
  <dd>
  <div class="footer">&copy; 2018 SAP SE</div>
  </dd>
</dl>
<hr>
<a href="Exercise_4_Part2_CreateAdditionalAdsEndpoints.md">
  <img align="left" alt="Previous Exercise">
</a>
<a href="/CloudFoundryBasics/Exercise_6_DeployAdsOnCloudFoundry.md">
  <img align="right" alt="Next Exercise">
</a>

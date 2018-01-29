Exercise 2:  Provide Hello World REST API on Tomcat
===================================================

## Learning Goal
After this exercise you know how to develop a simple REST API using Spring Web MVC and how to run it on Tomcat.

The task of this exercise is to implement a Hello World service that simply responds with a customizable welcome message. The service should be accessable under the url `http://localhost:8080/hello/Dude`. Here, `Dude` is a placeholder for any name, a so called URI path variable. The response message content should be represented using the `text/plain` media type.

## Step 1: Create the REST API
- Create a controller class `HelloWorldController` in the package `com.sap.bulletinboard.ads.controllers`.
In this controller, you should use the [Spring Web MVC framework](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html) to implement a simple REST controller.
This controller should provide a single `@GetMapping` annotated method which is able to handle a parameterized HTTP GET request:

```java
@RequestMapping("hello")
@RestController
public class HelloWorldController {

    @GetMapping(path = "/{name}")
    public String responseMsg(@PathVariable("name") String name) {
        return "Welcome: " + name;
    }
}
```
Note: You will most likely get syntax errors in Eclipse. Click on the underlined places, hit `CTRL+1` (='quick fix') and select the proper import statements.

**Some Explanations:**

In this exercise the following **Spring Web** annotations (`org.springframework.web.bind.annotation.*`) are used and processed at runtime:

  - The `@RequestMapping` annotation specifies that the endpoints are hosted at the path `/hello`.
  - The `@GetMapping` annotation specifies the Java method which processes the HTTP GET requests. It is similar to `@RequestMapping(method = RequestMethod.GET)`.
  - The `@PathVariable` annotation binds the value of the `/{name}` path segment to our method argument `name`. (You can specify multiple path segments, e.g. `/{firstname}/{surname}`, that should be mapped to different corresponding `@PathVariable` annotated method arguments.). Also note that pathVariable differs from requestParam as pathVariable is part of URL. The same url using requestParam would look like `www.mydomain.com/hello?name=Dude`.
  - The `@RestController` specifies a RESTful web service controller that simply returns the object / return value that is written directly to the HTTP response as JSON/XML. There is no model and no view involved.

## Step 2: Run and Test Microservice
- Run the microservice as described here: [Exercise 1: Getting Started](Exercise_1_GettingStarted.md)
- Open the URL `http://localhost:8080/hello`. A 404 (Not Found) response is expected as the GET is only provided under the path `/hello/{Name}`!
- A simple GET request can be tested directly in the browser. Use the browser developer tools to analyze the response in more detail. In Chrome you can open the developer tools with `F12` and analyze the messages in the console and network views.

## Step 3: Test Using a REST Client
[Postman](https://chrome.google.com/webstore/detail/postman/fhbjgbiflinjbdggehcddcbncdddomop) is a Chrome Plugin that helps to create and test custom HTTP requests. In our case we would like to analyze a simple GET request to `http://localhost:8080/hello/Dude`.

## Used Frameworks and Tools
- [Spring Web MVC](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html)
- [Tomcat Web Server](http://tomcat.apache.org/)
- [Spring - DI Framework](https://github.com/spring-projects/spring-framework)
- [Postman REST Client (Chrome Plugin)](https://chrome.google.com/webstore/detail/postman/fhbjgbiflinjbdggehcddcbncdddomop)


***
<dl>
  <dd>
  <div class="footer">&copy; 2018 SAP SE</div>
  </dd>
</dl>
<hr>
<a href="Exercise_1_GettingStarted.md">
  <img align="left" alt="Previous Exercise">
</a>
<a href="Exercise_3_CreateAdsEndpoints.md">
  <img align="right" alt="Next Exercise">
</a>



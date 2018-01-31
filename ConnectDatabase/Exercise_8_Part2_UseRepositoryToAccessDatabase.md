Exercise 8 (Part 2): Use Repository to Access Database
======================================================

## Learning Goal
After this exercise you know how to access our database using the convenience CRUD repository interface. 

The task of this exercise is to persist the advertisements via the corresponding repository instead of storing the data temporarily into a `Map` and ensure that the JUnit tests are still running successfully. That's why you also need to configure the in-memory database (H2) in context of the JUnit tests. 

## Prerequisite
Continue with your solution of the last exercise. If this does not work, you can checkout the branch [`origin/solution-8-1-Configure-Persistence`](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-8-1-Configure-Persistence).

## Step 1: Inject Repository Into Controller
In the previous exercise we used the `Spring Data JPA` plugin to create an instance of the `AdvertisementRepository` interface. In this step we inject such an instance into the `AdvertisementController`.

- As constructor injection is the preferred approach, we create in the `AdvertisementController` class, a constructor with an `AdvertisementRepository` typed argument, and annotate the constructor with `@Inject`.

## Step 2: Use CRUD Repository in Controller
Refactor the `AdvertisementController` class so that instead of using the HashMap, the advertisements are persisted using the `AdvertisementRepository` instance (using the [`CrudRepository`](http://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html) methods `findOne`, `findAll`, and `save`).

**Note**: The CRUD repository automatically decides whether to create a new database entry, or update an existing one when using its `save` method. To trigger updates, you need to pass an entity object containing the ID in addition to all the (updated) fields.

## Step 3: Run and Test the Service Locally
Run the service as described here: [Exercise 1: Getting Started](../CreateMicroservice/Exercise_1_GettingStarted.md)

Now you can test the service manually in the browser using the `Postman` chrome plugin. Please also test that the data is persisted. To test this you could create some advertisements, relaunch the application and see whether the advertisements created in the previous session are returned by new GET requests.

**Note**: Currently the tests are failing (which will be fixed in the next steps). If you want to build using Maven, run `mvn clean package -DskipTests`.

## Step 4: Run Component Tests
The service tests from [Exercise 4](../CreateMicroservice/Exercise_4_CreateServiceTests.md) are broken now, because the test provides no information about how to inject the `AdvertisementRepository` instance.

Furthermore the tests should run independently from a concrete database, so we provide an alternative database configuration which uses an in-memory database named `H2`. 

- Add the `h2` dependency to your `pom.xml`:
```
<!-- H2 (in-memory) database implementations -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.184</version>
    <scope>test</scope>
</dependency>
```
Note: After you've changed the Maven settings, don't forget to update your Eclipse project! To do so right click on your Eclipse project and select `Maven` - `Update Project ...` (`ALT-F5`)

- Create a new class `EmbeddedDatabaseConfig` in the **test package** `com.sap.bulletinboard.ads.config` and copy the code from [here](https://github.com/SAP/cloud-bulletinboard-ads/raw/solution-8-2-Use-Repository-To-Access-Database/src/test/java/com/sap/bulletinboard/ads/config/EmbeddedDatabaseConfig.java).
- Now you can run the JUnit tests as described [in Exercise 4](../CreateMicroservice/Exercise_4_CreateServiceTests.md).

**Explanation:** When the tests are started, the application is not initialized using the `AppInitializer`. The application context is initialized with all classes that are added as part of the `@ContextConfiguration` annotation (@see `AdvertisementControllerTest` class). As consequence the "cloud" profile isn't set in context of the test context. 

You might have the question why you need an extra database configuration namely `EmbeddedDatabaseConfig` for your tests instead of providing different configuration settings via system environment variables?
We decided to use a separate configuration class for the tests, as the tests do not run in a cloud-like environment. Even though the configuration classes look very similar, the main difference is, that the `CloudDatabaseConfig` extends from the `AbstractCloudConfig` class that expects a cloud environment. A cloud environment is only detected if the `VCAP_APPLICATION` and `VCAP_SERVICES` variables are set. To make that more explicit, we have annotated the `CloudDatabaseConfig` class with `@Profile("cloud")` i.e. it gets only loaded when "cloud" is set as active profile.

## [Optional] Step 5: Check ID for Updates
If you have implemented the PUT Request according to [Exercise 4-2](../CreateMicroservice/Exercise_4_Part2_CreateAdditionalAdsEndpoints.md), make sure that the ID contained in the URL is the same ID contained in the advertisement entity.
Therefore add a test that sends a PUT request for an entity with a different ID than what is specified in the URL.
In the test assert that the server responds with a `Bad Request` (400) status code.
You may add a `setId` method to the `Advertisement` class so that, in the tests, you can create advertisement entity instances with a specific ID.

Fix the code according to the test by introducing a `throwIfInconsistent` method comparing the two IDs.
Also make sure that the tests in `AdvertisementControllerTest` are still passing, especially `updateNotFound`.

## [Optional] Step 6: Debug Entity ID Generation

For new advertisements the code does not directly set the ID. However, the advertisement object returned by the POST
endpoint has a non-null ID. Use the Eclipse debugger to find out when exactly the ID is set in the object instance.

 - In Eclipse, add a breakpoint to the first line of the `add` method (for that you can double-click on the bar to the left of your code)
 - Deploy the application and start Tomcat in debug mode
 - Use `Postman` chrome plugin to create a new advertisement
 - In the Debug perspective and in the Variables view, observe that `advertisement.id` is null
 - Using "Step over (F6)" advance to the next line (stepping over the `save` method invocation)
 - Observe that `advertisement.id` is now set
 - Continue to run the application (F9), or stop Tomcat

## [Optional] Step 7: Introduce Pagination
Assume you have hundreds or more entries in a table. As part of a GET-all request the application needs to fetch all these entries from the database, buffer them, convert them into JSON in order to provide it as part of the response etc. This costs CPU, memory and network resources while most of the data is usually neither required nor consumable by the client / UI. To improve the performance of the mass GET query it is common practice to introduce pagination, sorting and filtering. Accordingly, you should also differentiate on REST API level which kind of pagination you like to implement:
- **client-driven pagination** normally chosen for consumer flexibility: the consumer decides what portions of data are to be retrieved.  
- **server-driven pagination** for optimizations (caching) and protection (e.g. against denial of service by selecting a too large page size): the server decides on page cutting and provides additional information about the locations of the previous and next page; the client still has a possibility to configure the page size. 

For paging / pagination, you can simply make use of the [`PagingAndSortingRepository`](http://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/PagingAndSortingRepository.html), which is an extension of `CrudRepository`. As part of your `readAll` request handler method you just need to introduce additional [`RequestParam`](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/bind/annotation/RequestParam.html)s `pageId` and `pageSize` with some meaningful defaults.

Have a look into the last commit of the [`solution-8-3-IntroducePaging`](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-8-3-IntroducePaging) branch to see how an implementation could look like.

## Used Frameworks and Tools
- [H2 in-memory Database](http://www.h2database.com/html/tutorial.html)

## Further Reading
- [JDoc CrudRepository](http://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html)
- [JDoc PagingAndSortingRepository](http://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/PagingAndSortingRepository.html)


***
<dl>
  <dd>
  <div class="footer">&copy; 2018 SAP SE</div>
  </dd>
</dl>
<hr>
<a href="/ConnectDatabase/Exercise_8_Part1_ConfigurePersistence.md">
  <img align="left" alt="Previous Exercise">
</a>
<a href="/ConnectDatabase/Exercise_9_ImplementJPAEntity.md">
  <img align="right" alt="Next Exercise">
</a>

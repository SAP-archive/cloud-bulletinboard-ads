Exercise 9: Implement JPA Entity Test-Driven
=============================================

## Learning Goal
After this exercise you know how to implement a JPA entity leveraging the JPA annotations that are defined in the [`javax.persistence` package](https://docs.oracle.com/javaee/7/api/javax/persistence/package-summary.html). 

The task of this exercise is to get familiar with the most common JPA annotations using JUnit tests to motivate their usage.

## Prerequisite
Continue with your solution of the last exercise. If this does not work, you can checkout the branch [`origin/solution-8-2-Use-Repository-To-Access-Database`](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-8-2-Use-Repository-To-Access-Database).

## Step 1: Create JUnit Test Class 
Create JUnit test case `AdvertisementRepositoryTest` in test-package `com.sap.bulletinboard.ads.models`. In Eclipse select `File - New - Other`. In the next dialog, enter `JUnit Test Case`. The default settings should be OK.

- Like in the component tests (`AdvertisementControllerTest`) we want to use the H2 in-memory database instead of the real database. Therefore we use Spring Dependency Injection (DI) to inject a H2-configuration (`EmbeddedDatabaseConfig.class`) for the `AdvertisementRepository`. So add the following annotations to the JUnit test class:
```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = EmbeddedDatabaseConfig.class)
```

- As mentioned in [Exercise 4](../CreateMicroservice/Exercise_4_CreateServiceTests.md) we want to make use of Hamcrest's `assertThat` method and the standard set of matchers, both of which we provide using static imports:

```java
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;
```

- Like in the productive mode, the `AdvertisementRepository` instance, which is the class under test (CUT), should be created/ injected by Spring DI:
```java
@Inject
private AdvertisementRepository repo;
```

## Step 2: Implement Your Entity Test-Driven
That means, you will always start with the test case. Run the test case and watch the test fail. Then you would implement the production code necessary to pass the tests.

**Explanation:** Basically you will introduce some technical fields documenting for example when entries are created / updated. Those are helpful to analyze or even avoid database inconsistencies.

### Test 1: Id should be generated for new advertisement
Test Case:
```java
@Test
public void shouldSetIdOnFirstSave() {
    Advertisement entity = new Advertisement();
    entity.setTitle("title");
    entity = repo.save(entity);
    assertThat(entity.getId(), is(notNullValue()));
}
```
This test should be green right from the beginning, as the JPA features covered by the test are already used in the `Advertisement` entity. Find out what happens if you would comment the respective JPA annotations `@Id` and `@GeneratedValue`.


### Test 2 : Should set CreatedAt timestamp once
Test Case:

```java
@Test
public void shouldSetCreatedTimestampOnFirstSaveOnly() throws InterruptedException{
    Advertisement entity = new Advertisement();
    entity.setTitle("title");
    
    entity = repo.save(entity);
    Timestamp timestampAfterCreation = entity.getCreatedAt();
    assertThat(timestampAfterCreation, is(notNullValue()));
    
    entity.setTitle("Updated Title");
    Thread.sleep(5); //Better: mock time!

    entity = repo.save(entity);
    Timestamp timestampAfterUpdate = entity.getCreatedAt();
    assertThat(timestampAfterUpdate, is(timestampAfterCreation));
}
```
To fulfill the test case you need to implement a callback method in the `Advertisement` class (annotated with `@PrePersist`) that is called by JPA before a new entity is persisted the first time.

You can use the following code snippet to create a timestamp representing the current time:
```java
protected Timestamp now() {                       // use java.sql.Timestamp
    return new Timestamp((new Date()).getTime()); // use java.util.Date
} 
```

> Whenever you are referring to an exact moment in time, persist the time according to a unified standard that is not affected by daylight savings, e.g. **UTC**. Right now, the timestamp is not stored in UTC time zone, but it is persisted in local JVM time zone, i.e. the product might not work correctly across different time zones and during the daylight saving time switch.

### Test 3 : Should set UpdatedAt timestamp on every update
Test Case:

```java
@Test
public void shouldSetUpdatedTimestampOnEveryUpdate() throws InterruptedException{
    Advertisement entity = new Advertisement();
    entity.setTitle("title");
    entity = repo.save(entity);
    
    entity.setTitle("Updated Title");
    entity = repo.save(entity);
    Timestamp timestampAfterFirstUpdate = entity.getUpdatedAt();
    assertThat(timestampAfterFirstUpdate, is(notNullValue()));
    
    Thread.sleep(5); //Better: mock time!
    
    entity.setTitle("Updated Title 2");
    entity = repo.save(entity);
    Timestamp timestampAfterSecondUpdate = entity.getUpdatedAt();
    assertThat(timestampAfterSecondUpdate, is(not(timestampAfterFirstUpdate)));
}
```
To fulfill the test case you need to implement a callback method (annotated with `@PreUpdate`) that is called by JPA when an entity is identified as modified.

### Test 4 : Add version entity field  
Test Case:
```java
@Test(expected = JpaOptimisticLockingFailureException.class)
public void shouldUseVersionForConflicts() {
    Advertisement entity = new Advertisement();
    entity.setTitle("some title");
    entity = repo.save(entity); // persists entity and sets initial version

    entity.setTitle("entity instance 1");
    Advertisement updatedEntity = repo.save(entity); // returns instance with updated version

    repo.save(entity); // tries to persist entity with outdated version
}
```

To fulfill the test case you need to add a field `version` of type `long` that is annotated with `@Version`. JPA uses this to count up version numbers and in the context of [**Optimistic Locking**](https://en.wikipedia.org/wiki/Optimistic_concurrency_control) to detect parallel updates on the same entity (database record).

**Explanation**: In this test, the version is set when saving the entity for the first time. After updating the object instance and persisting the changes to the database, JPA updates the version in the database to note this change. While the returned `updatedEntity` instance contains the updated version, the `entity` instance still contains the old version. The test provokes an optimistic locking exception by trying to persist `entity` again, JPA detects a mismatch of the `entity` version and the version noted in the database.

**Warning**: By adding the `version` field you've changed the database schema implicitely. Note that the existing advertisement table entries will have no values (i.e. null) for the new `version` field. 

**Note**: When using a CRUD repository, its `save` method will only update an entity if the passed argument contains both the ID and the version field. In other words, without specifying the version field in a PUT request the repository would try to create a new entity with the same ID (which will fail).

## Step 3: Run All the Tests
After you've finished the implementation of your entity, you should also ensure that your changes have no undesired side-effects to your application. You can check that by running all the JUnit Tests within your project. 

Furthermore it might be helpful to test your service manually in the browser using the `Postman` chrome plugin in order to check, for example, whether the `createdAt` field is properly set.


## [Optional] Step 4: What Happens When...
- you delete an Entity class (you can simulate that by commenting the respective entity class)?
- you change the field name of an Entity?
- ...


## Used Frameworks and Tools
- [Hamcrest Matchers](http://hamcrest.org/JavaHamcrest/)
- [JPA 2.0 Annotation - Specification](http://download.oracle.com/otndocs/jcp/persistence-2.0-fr-eval-oth-JSpec/)

***
<dl>
  <dd>
  <div class="footer">&copy; 2018 SAP SE</div>
  </dd>
</dl>
<hr>
<a href="/ConnectDatabase/Exercise_8_Part2_UseRepositoryToAccessDatabase.md">
  <img align="left" alt="Previous Exercise">
</a>
<a href="/ConnectDatabase/Exercise_10_DeployAdsWithDBServiceOnCF.md">
  <img align="right" alt="Next Exercise">
</a>


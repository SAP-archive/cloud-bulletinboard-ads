[Optional] Exercise 11: Implement a Custom Query
================================================

## Learning Goal
After this exercise you know how to implement a custom query for a given JPA Entity. 

The task of this exercise is to understand how to enhance the advertisement repository by custom functionality and how to implement a query using the Java Persistence Query Language (JPQL) and the JPA Criteria Builder.

## Prerequisite
Continue with your solution of the last exercise. If this does not work, you can checkout the branch [`origin/solution-10-Deploy-Ads-With-DB-Service-On-CF`](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-10-Deploy-Ads-With-DB-Service-On-CF).

## Step 1: Create Custom Query Test-driven

Test Case:
```java
@Test
public void shouldFindByTitle() {
    Advertisement entity = new Advertisement();
    String title = "Find me";

    entity.setTitle(title);
    repo.save(entity);
    
    Advertisement foundEntity = repo.findByTitle(title).get(0);
    assertThat(foundEntity.getTitle(), is(title));
}
```

Analogous to this [description](http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.single-repository-behaviour) we want to provide a very simple (hard coded) implementation to fulfill the test:
- You need to define your custom method in another interface like `AdvertisementRepositoryCustom`.
- Let your `AdvertisementRepository` interface extend the custom one in addition to the `CrudRepository`. This combines the CRUD and custom functionalities.
- Create a class with name `AdvertisementRepositoryImpl` that implements the `AdvertisementRepositoryCustom` interface, the `findByTitle` method by, for now, just returning `null`.

## Step 2: Implement Query Using JPQL

We now extend the implementation by using JPQL to return the corresponding advertisements.

- Inject an `EntityManager` into your instance by annotating the field with `@PersistenceContext`
```java
@PersistenceContext
private EntityManager entityManager;
```
- Implement using a JPQL query
```java
String qlString = "SELECT ads FROM Advertisement ads WHERE ads.title = :title";
TypedQuery<Advertisement> query = entityManager.createQuery(qlString, Advertisement.class);
query.setParameter("title", title);
return query.getResultList();
```

Run your test to ensure it is passing.

**Note**: In order to protect your application against SQL injection you should always make use of `prepared statements` and / or variable binding (aka `parameterized queries`). With JPA or Hibernate you should use `Named Parameter` that are prefixed with a colon (`:`). Named parameters in a query are bound to an argument by the `javax.persistence.Query.setParameter(String name, Object value)` method, any dangerous character should be automatically escaped by the JDBC driver.

## Step 3: Implement Query Using Criteria Builder

Instead of directly writing a query, you can use the Criteria Builder to construct a query using typesafe API.

```java
CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

CriteriaQuery<Advertisement> criteriaQuery = criteriaBuilder.createQuery(Advertisement.class);
Root<Advertisement> advertisement = criteriaQuery.from(Advertisement.class);
ParameterExpression<String> titleParameter = criteriaBuilder.parameter(String.class);
criteriaQuery.select(advertisement).where(criteriaBuilder.equal(advertisement.get("title"), titleParameter));

TypedQuery<Advertisement> query = entityManager.createQuery(criteriaQuery);
query.setParameter(titleParameter, title);

return query.getResultList();
```

Run your test again to ensure the test is still running.

**Note:** It is advisable to replace the string literal `"title"` by a field reference using a JPA Static Metamodel (see links below).

## Step 4: Remove Implementation
The name `findByTitle`, together with the field name `title` in the `Advertisement` entity definition, is enough for Spring Data to figure out what this should mean. In other words, you do not even need to implement this method to make it work. You can try this out by just removing your `AdvertisementRepositoryCustomImpl` class. You may also move the `findByTitle` definition into the repository interface, and remove the `AdvertisementRepositoryCustom` interface. You can find more details at [Spring Data Query Creation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation).

## Further Reading
- [JPQL Tutorial](http://www.tutorialspoint.com/jpa/jpa_jpql.htm)
- [JPQL: The Basics Video (Youtube)](https://www.youtube.com/watch?v=KdJ4W7nqhVg)
- [JPA Criteria Builder](http://www.objectdb.com/java/jpa/query/criteria) and [javaDoc](https://docs.oracle.com/javaee/7/api/javax/persistence/criteria/CriteriaBuilder.html)
- [JPA Static Metamodel Introduction](http://www.thoughts-on-java.org/static-metamodel/)
- [JPA Static Metamodel Tutorial](https://docs.oracle.com/javaee/6/tutorial/doc/gjiup.html)
- [JPA Static Metamodel Generation](https://docs.jboss.org/hibernate/orm/5.0/topical/html/metamodelgen/MetamodelGenerator.html)
- [Spring Data Query Creation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation)
- [JDBC Template (no JPA, native queries only)](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/jdbc.html)
- [Advanced Spring Data JPA - Specifications and Querydsl](https://spring.io/blog/2011/04/26/advanced-spring-data-jpa-specifications-and-querydsl/) to make queries more readable and reusable e.g. `customerRepository.findAll(isLongTermCustomer());`

***
<dl>
  <dd>
  <div class="footer">&copy; 2018 SAP SE</div>
  </dd>
</dl>
<hr>
<a href="/ConnectDatabase/Exercise_10_DeployAdsWithDBServiceOnCF.md">
  <img align="left" alt="Previous Exercise">
</a>
<a href="/LoggingTracing/Exercise_12_Setup_Logger.md">
  <img align="right" alt="Next Exercise">
</a>


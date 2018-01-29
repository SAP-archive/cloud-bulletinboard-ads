[Optional] Exercise 4 (Part 2): Implement Update/Delete Test-driven
=================================================================

## Learning Goal
After this exercise you will have a more detailed understanding of REST basics (header, body, entity, status code). You will see how easy it is to build RESTful Web services using Spring Web MVC and how to implement that in a test-driven way using MockMVC.

The task of this exercise is to implement the REST-services with responses as defined below. Like in the last exercise, the response type should always be `application/json`.

| HTTP Verb   |  CRUD      | collection/unspecific (e.g. `/api/v1/ads/`) | specific item (e.g. `/api/v1/ads/0`)|   
| ----------- | ---------- | --------------------------------------------- | ------------------------------------- |
| PUT         | Update     | 405 (Method not allowed)                      | 200 (OK), updated ad; 404 (Not Found), if no advertisement with this ID exists |
| DELETE      | Delete     | 204 (No Content)                              | 204 (No Content); 404 (Not Found), if no advertisement with this ID exists |

## Prerequisite
Continue with your solution of the last exercise. If this does not work, you can checkout the branch [origin/solution-4-Create-ServiceTests](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-4-Create-ServiceTests).

## Step 1: Single Update
Start with your test: develop another component test case in your `AdvertisementControllerTest` class that sends a PUT request and implements the expectations that are specified in the table above.

When sending a PUT request, in general the entity with the ID given in the URL should be overwritten with the values provided in the request. Explanation: Based on RFC 7231, `PUT` should be used only for complete replacement of a representation, in an idempotent operation. `PATCH` should be used for partial updates, that aren't required to be idempotent, but it's a good case practice to make them idempotent by requiring a precondition or validating the current state before applying the diff. 
Implement `AdvertisementController` accordingly.

## Step 2: Single Delete
Develop another component test case in your `AdvertisementControllerTest` class that sends a DELETE request and implements the expectations that are specified in the table above.
Implement `AdvertisementController` accordingly.

## Step 3: Mass Delete
Similar to step 2, test deletion of all advertisements and extend your implementation accordingly.

## Step 4: Test using REST Client
Test the REST service at `http://localhost:8080/api/v1/ads` manually in the browser using the `Postman` Chrome plugin. 


***
<dl>
  <dd>
  <div class="footer">&copy; 2018 SAP SE</div>
  </dd>
</dl>
<hr>
<a href="Exercise_4_CreateServiceTests.md">
  <img align="left" alt="Previous Exercise">
</a>
<a href="Exercise_5_ValidationAndExceptions.md">
  <img align="right" alt="Next Exercise">
</a>

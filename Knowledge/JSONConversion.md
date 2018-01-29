# JSON
## Introduction
In our project we use JSON to submit and receive messages. A POJO (plain old java object) can be transformed to
a JSON string automatically, and we can also automatically create object instances based on JSON strings.
As a simple example, the JSON object `{"title": "Some Title", "id": "42"}` can be transformed into a Java object
instance for the following class:

```
public class Test {
  public String title;
  public long id;
}
```

In this object instance the fields are set accordingly.

## Technical Aspects
We use the [Jackson project](https://github.com/FasterXML/jackson) to do this conversion. By registering the `JacksonJsonProvider`
bean CXF automatically uses Jackson when a JSON String or POJO needs to be converted. For this to work, the following aspects
need to be regarded:
* The `Content-Type` of incoming requests containing JSON data must be set to `application/json`
* POJOs are only converted to JSON if the REST controller produces JSON 
* Jackson by default uses the default constructor (no arguments) to create a new object instance.
If you implement another constructor, make sure to also add the default constructor explicitly.
* In the default configuration, the fields must be public, or you need to provide public setter and getter methods. These methods must follow the
`setName` and `getName` pattern. For `boolean` values also `isName` is accepted.
* Jackson can be configured using annotations, e.g. `@JsonIgnoreProperties(ignoreUnknown=true)` to ignore additional fields in the JSON string or you can
* configure your own `JacksonJsonProvider`:
```java
JacksonJsonProvider jsonProvider = new JacksonJsonProvider()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
```
Further settings are documented [here](https://fasterxml.github.io/jackson-databind/javadoc/2.0.0/com/fasterxml/jackson/databind/DeserializationFeature.html)

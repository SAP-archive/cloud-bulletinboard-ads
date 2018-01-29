# The Way Towards Spring Boot

**Spring Boot** makes it easy to create stand-alone applications based on the Spring framework. 
Today Spring Boot is much more mature, it offers a valuable amount and a conclusively set of Spring Boot starter packages, that makes absolutely sense in context of Microservice application development. 

Apart from that **Spring Cloud** builds on top of Spring Boot, which empowers developers to rapidly build complex applications by leveraging common patterns in distributed systems. 

## Rationale Behind Spring Boot
- Has huge open source community: a lot of tutorials / code examples, simplifies troubleshooting, keeping yourself up-to-date.
- One recommended cloud programming model: more and more SAP development teams decide for Spring Boot.
- It's the basis for Spring Cloud components for implementing distributed design patterns in the cloud. 

### The idea of Spring Boot
<img src="https://github.wdf.sap.corp/cc-java-dev/cc-coursematerial/blob/master/SpringBoot/images/spring-ingredients.jpg" height="200" /> => <img src="https://github.wdf.sap.corp/cc-java-dev/cc-coursematerial/blob/master/SpringBoot/images/boot-cake.jpg" height="200" />

## Differences between Current Course and Spring Boot Version
Overall you don't need to expect too much migration efforts towards Spring Boot, when starting from the current version, which is active since 18th November.
You already get in touch with those Spring libraries, that are promoted in context of Spring Boot. 


### Overview of Differences
The following table highlights the most important differences.

| Topic  | Spring Web MVC version | Spring Boot version | Read more   |
|--------|------------------------|---------------------|-------------|
| Sample | [GitHub: Spring Web MVC](https://github.wdf.sap.corp/cc-java/cc-bulletinboard-ads-spring-webmvc)  | [GitHub: Spring Boot](https://github.wdf.sap.corp/cc-java/cc-bulletinboard-ads-spring-boot) | |
| Initial Project Setup | Copy a sample Project, remove unrequired stuff | Make use of [Spring Initializer](http://start.spring.io/) to bootstrap your aplication, also part of Eclipse STS | [Spring Boot STS Tutorial](https://spring.io/blog/2015/03/18/spring-boot-support-in-spring-tool-suite-3-6-4) |
| Adding classpath dependencies | add library as dependency to pom, you need to manage version/order/mutual interference  | add spring boot starter packages as managed dependencies, that were tested in combination | [Dependency Management](https://github.wdf.sap.corp/cc-java-dev/cc-coursematerial/blob/master/SpringBoot/Readme.md#dependency-management) |
| Maven Plugin to run (package) an application “in-place” | `mvn tomcat7:run` deploys your application on Tomcat | `mvn spring-boot:run` runs your Spring Boot application. `mvn spring-boot:repackage` repackages your jar/war to be executable. | [Spring Boot Maven Plugin](http://docs.spring.io/spring-boot/docs/1.4.2.RELEASE/maven-plugin/)|
| Packaging (war, jar) | No specifics here  | Note that the JAR file created by Spring Boot does not "explode" the JAR files of the dependencies, Spring Boot's JarLauncher/WarLauncher starts main method | <ul><li>[Spring.io: The executable jar format](http://docs.spring.io/spring-boot/docs/current/reference/html/executable-jar.html)</li><li>[Packaging (JAR, WAR)](https://github.wdf.sap.corp/cc-java/cc-bulletinboard-ads-spring-boot/blob/master/docs/SpringBootBasics.md#packaging-jar-war)</li><li>[Spring.io: Maven build tools plugin](http://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html)</li></ul> |
| The “main” method | `onStartup` method of a `WebApplicationInitializer` implementation | `main(String[] args)` method as part of a `@SpringBootApplication` annotated class | [Example](http://docs.spring.io/autorepo/docs/spring-boot/current/reference/html/using-boot-using-springbootapplication-annotation.html) |
| `Bean` registration and more | Explicitely register Beans or declare `@ComponentScan` | `@SpringBootApplication` is equivalent to using `@Configuration`, `@EnableAutoConfiguration` and `@ComponentScan` | " |
| Spring Application Context Setup | Define `ApplicationContext` explicitely and assign it to the `DispatcherServlet` | `SpringApplication.run(<primary Spring Component>, args)` bootstraps the application, attempts to create the right type of `ApplicationContext` on your behalf,triggers the start of the auto-configured Tomcat web server | " |
| Auto Configuration | ./. | With `@EnableAutoConfiguration` Spring beans gets automatically configured depending on the `@ConfigurationProperties` annotated beans and what is found on the classpath. | <ul><li>[Spring.io: Auto-configuration](http://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-auto-configuration.html)</li><li>[@EnableAutoConfiguration explained](https://github.wdf.sap.corp/cc-java/cc-bulletinboard-ads-spring-boot/blob/master/docs/SpringBootBasics.md#enableautoconfiguration)</li><li>How to [troubleshoot auto-configuration](http://docs.spring.io/spring-boot/docs/current/reference/html/howto-spring-boot-application.html#howto-troubleshoot-auto-configuration)</li></ul>|
| Security Filter | Servlet filter `springSecurityFilterChain` needs to be registered in ServletContext | If Spring Security is on the classpath then automatically all endpoints are secured with ‘basic’ authentication | [Spring.io: Security](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-security)
| Externalized Configuration | configure your application via system environment variables | additionally you can make use of Profile-specific application properties (`application-{profile}.properties` and YAML variant) | [Spring.io: Externalized Config](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) |
| Actuator Plugin | Even if you can make use of that Spring Boot plugin, not all management points are accessible [read here](https://github.wdf.sap.corp/cc-java-dev/cc-coursematerial/blob/master/CreateMicroservice/Exercise_1_GettingStarted.md) | full functionality exposed as `JMX Beans` | <ul><li>[Spring.io: Actuator](http://docs.spring.io/spring-boot/docs/current/reference/html/production-ready.html)</li><li>[Actuator Endpoints](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html)</li></ul> |
| Profile aware logback.xml | Workaround for tests: have a `/src/test/resources/logback-test.xml` | One [`logback-spring.xml`](https://github.wdf.sap.corp/CF-PROVISIONING/tenant-onboarding/blob/master/src/main/resources/logback-spring.xml) that contains the log configuration for several profiles | [Spring.io: Custom log configuration](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html#boot-features-custom-log-configuration)
| Configure `MockMVC`-Controller Tests | explicitely setup `MockMVC` and web `ApplicationContext` |  Based on the `@WebMvcTest` or `@AutoConfigureMockMvc` annotation Spring automatically creates a web `ApplicationContext` containing the controller and registers a `MockMvc` bean. Typically it is used in combination with `@MockBean` or `@Import` to create any collaborators required by your `@Controller` beans | <ul><li>[Spring.io: Testing](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html)</li><li>[Java-Doc: WebMvcTest](http://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/test/autoconfigure/web/servlet/WebMvcTest.html)</li></ul>|

### Dependency Management
As you can see in the Spring Boot [`pom.xml`](https://github.wdf.sap.corp/cc-java/cc-bulletinboard-ads-spring-boot/blob/master/pom.xml) file, Spring (Boot) components are added to the project by defining `spring-boot-starter` dependencies. For example, for getting started with JPA for database access just include `spring-boot-starter-data-jpa`. It comes with a consistent, supported set of managed transitive dependencies. The Spring Boot version needs to be specified once `spring-boot-starter-parent` (<parent> section) and not for each dependency. The "managed" versions are inherited from the `spring-boot-dependencies`, which is the parent of `spring-boot-starter-parent`.
 
For some Spring Boot components a dedicated `<dependencyManagement>` block needs to be added.

### `@EnableAutoConfiguration`
Furthermore, `@SpringBootApplication` is annotated with `@EnableAutoConfiguration`.
Because of this, several other beans are automatically registered and configured.
As a simple example, if the necessary (Maven) dependencies are provided, Spring automatically creates a `RabbitTemplate` bean which connects to a `RabbitMQ` server running on `localhost`.

The auto-configuration as done by Spring Boot is often described as having an "opinionated  view on Spring", meaning that reasonable defaults are chosen despite having many other possibilities in Spring.
In the example of `RabbitTemplate`, the default is to configure and create the bean so that it connects to `localhost`, but you can also disable this behaviour, or re-configure the bean so that it connects to (for example) a backing service in Cloud Foundry.
Indeed, we manually disable this kind of auto-configuration in our tests, and use other extension to connect to remote `RabbitMQ` services (as defined in `VCAP_SERVICES`).

The main advantage of Spring Boot and its auto-configuration is that several components easily integrate with each other, for example endpoints which are automatically offered using Spring MVC, or communication classes which use Eureka+Hystrix+Ribbon+Sleuth with only little manual configuration or code changes.
Together with the fact that the versions of several components are set in a central place (to avoid dependency issues), this can help developing faster.


Beside of Spring `profiles` there is an option to define arbitrary conditions to activate a Bean. The auto-configuration internally uses annotations like `@ConditionalOnMissingBean` and `@ConditionalOnProperty`. Find more `@Condition` annotations and supporting classes [here](http://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/condition/package-summary.html).

```
@Configuration
@ConditionalOnClass({RabbitTemplate.class, Channel.class})
@EnableConfigurationProperties(RabbitProperties.class)
public class RabbitAutoConfiguration {
	
	@Configuration
	@ConditionalOnMissingBean(RestTemplate.class)
	protected static class RabbitTemplateCreator {
		//only executed when RabbitTemplate is on class path 
		//and there is no Bean defined elsewhere ...
	}
	
}
```
Auto-configuration classes may be disabled using Spring properties, e.g by adding `spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration` to `src/main/resources/application.properties`.

### Auto configuration report
The auto configuration report, which can be enabled by entering `--debug` as command line argumemt or configuring `debug=true` in the `application.properties` file, shows config conditions for all components:
- Positive Matches: condition matches -> do config
- Negative Matches: no match -> do nothing
- Exclusions (explicitly defined by you)
- Unconditional classes –> always configured

You get the same report with the **`/autoconfig` endpoint**, provided by the `Actuator Plugin`, which also visualizes all auto-configuration candidates and the reason why they ‘were’ or ‘were not’ applied.

Find here [all Auto-Configuration classes](http://docs.spring.io/spring-boot/docs/current/reference/html/auto-configuration-classes.html) provided by SpringBoot, which will also help you to implement your own AutoConfiguration.

![](/SpringBoot/images/SpringBoot_AutoConfigurationReport.png)


## Further References
- [Spring.io: Spring Boot Features](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features.html)
- [Spring.io: DevTools](http://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-devtools.html)
- [Sample Solution: Spring Web MVC](https://github.wdf.sap.corp/cc-java/cc-bulletinboard-ads-spring-webmvc) 
- [Sample Solution: Spring Boot](https://github.wdf.sap.corp/cc-java/cc-bulletinboard-ads-spring-boot) 
- [Spring Framework Reference](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/index.html)
- [Spring.io: Spring (Boot) Tutorials](https://spring.io/guides)
- [SAP internal Spring (Boot) Tutorial](https://github.wdf.sap.corp/d022051/SpringTutorial/wiki)


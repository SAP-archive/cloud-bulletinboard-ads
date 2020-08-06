# openSAP course: [Cloud-Native Development with SAP Cloud Platform](https://open.sap.com/courses/cp5)

## Description

Join this free online course, [Cloud-Native Development with SAP Cloud Platform](https://open.sap.com/courses/cp5), to learn and experience how microservice-based cloud native development with SAP Cloud Platform works. This is an advanced course aimed primarily at developers and application and technology consultants, teaching hands-on the important concepts of developing innovative cloud applications.

# <img src="./Z_ReuseImages/images/281495_SpiralboundNotebook_R_blue.png" height="80" alt="Course Information"/> Course Information

<img src="/Z_ReuseImages/images/openSAP_CP5_Course_Outline.png" height="250" alt="Course Outline"/>

SAP Cloud Platform is the enterprise platform-as-a-service, with comprehensive application development services and capabilities. It enables customers to achieve business agility, create a truly integrated and optimized enterprise, and accelerate digital transformation across the business – all without the requirement of maintaining or investing in on-premise infrastructure.

In this new advanced course, you’ll learn how to develop microservice-based cloud-native applications with [SAP Cloud Platform](https://cloudplatform.sap.com/) through hands-on exercises. You’ll be working primarily with the Cloud Foundry environment within SAP Cloud Platform and many of its open-source services to develop step-by-step a Java-based application that is made for the cloud.

We’ll start with a theoretical introduction to SAP Cloud Platform and microservice architectures. We will also cover Spring basics and introduce the sample application with which we will be working throughout the course for the hands-on exercises. In the following weeks, we’ll go deeper into creating microservices, connecting databases, handling logging and tracing, as well as service-to-service communication, ensuring security, and additional topics like multitenancy, Spring Boot, continuous integration and delivery, and advanced microservice design. In between we will also cover topics like REST services, stateless apps, Cloud Foundry as a platform, unit and component testing, and much more. In the hands-on exercises we will start based on an empty shell application and build a full real-world cloud application. The optional system preparation with instructions on setting up the development environment and some information to get started will open two weeks before the actual course starts.

This advanced course is primarily aimed at developers and application and technology consultants who have already finished the openSAP course [SAP Cloud Platform Essentials (Update Q3/2017)](https://open.sap.com/courses/cp1-2) and want to learn more. For the first time, we’ll cover in-depth microservice architectures and the open-source services of the Cloud Foundry environment within SAP Cloud Platform, and how to leverage them to build cloud-native applications.

As this course focuses in detail on the app level, the backend and how to offer REST services, topics like building SAP Fiori UIs with SAPUI5 are not covered. To learn about those topics please have a look at the great openSAP courses [Developing Web Apps with SAPUI5](https://open.sap.com/courses/ui51) and [SAP Fiori for iOS - Build Your First Native Mobile App](https://open.sap.com/courses/ios2). Also, database development for SAP HANA is not covered in this course, please refer to the openSAP course [Software Development on SAP HANA (Update Q4/2017)](https://open.sap.com/courses/hana6) to learn more about that.

The registration, learning content, and final exam are free of charge. You’ll be able to get some practical experience of the platform by using a free trial account. We’ll explain how you can access this in the System Preparation.

**Target Audience**
* Developer
* Application consultant
* Technology consultant

**Course Requirements**
* Programming skills in Java
* Knowledge of how to use the Eclipse IDE
* Experience with Git/GitHub, as well as Apache Maven
* Familiarity with the topics covered in the openSAP course: [SAP Cloud Platform Essentials (Update Q3/2017)](https://open.sap.com/courses/cp1-2)

**Development Systems**
If your primary interest in taking this course is getting a technical overview of the development-related capabilities of SAP Cloud Platform, you do not need access to a development system. However, we realize that many of you will be interested in experimenting with the platform and developing your own code, based on the instructions in the course.

You can [register for your own free trial account for SAP Cloud Platform](https://accounts.sap.com/ui/public/showRegisterForm?spName=https%3A%2F%2Fnwtrial.ondemand.com%2Fservices&targetUrl=https%3A%2F%2Faccount.hanatrial.ondemand.com%2Fcockpit&sourceUrl=https%3A%2F%2Fcloudplatform.sap.com%2Findex.html). We’ll also explain how to do this at the start of the course. There are a few restrictions for the free trial account, but you’ll be able to follow the course without any problems, as the instructor will use the same system as you.

For this course a preconfigured virtual machine image is provided and its use is recommended if you want to follow along with the hands-on exercises. The setup is provided and explained in Week 0: System Preparation.

## Requirements
- [The course prerequisites are menioned here](/CoursePrerequisites/README.md) which we cover in week 0 of the course

# <img src="./Z_ReuseImages/images/281498_SwissArmyKnife_R_blue.png" height="80" alt="Exercise"/> Exercises

## Week 0: System Preparation

**The Image**
- [Detailed installation / configuration steps](/CoursePrerequisites/README.md)

**Getting Started - Part I**
- [Exercise 1: Getting Started](/CreateMicroservice/Exercise_1_GettingStarted.md)

## Week 1: Fundamentals

**No Exercises**

## Week 2: Microservices

**Getting Started - Part II**
- [Exercise 2: [Optional] Provide Hello World Service on Tomcat](/CreateMicroservice/Exercise_2_HelloWorldResource.md)

**Creating a Microservice**
- [Exercise 3: Create Advertisement Endpoints](/CreateMicroservice/Exercise_3_CreateAdsEndpoints.md)
- [Exercise 4: Create Automated Component Tests](/CreateMicroservice/Exercise_4_CreateServiceTests.md)
- [Exercise 4.2: [Optional] Implement Update/Delete Test-driven](/CreateMicroservice/Exercise_4_Part2_CreateAdditionalAdsEndpoints.md)
- [Exercise 5: [Optional] Introduce Validations and Exception Handler](/CreateMicroservice/Exercise_5_ValidationAndExceptions.md)
- [Exercise 6: Deploy Advertisement on Cloud Foundry](/CloudFoundryBasics/Exercise_6_DeployAdsOnCloudFoundry.md)

## Week 3: Persistence

**Connecting a Database**
- [Exercise 7: Connect to local PostgreSQL Database](/ConnectDatabase/Exercise_7_ConnectLocalDatabase.md)
- [Exercise 8.1: Configure Persistence](/ConnectDatabase/Exercise_8_Part1_ConfigurePersistence.md)
- [Exercise 8.2: Use Repository to Access Database](/ConnectDatabase/Exercise_8_Part2_UseRepositoryToAccessDatabase.md)
- [Exercise 9: Implement JPA Entity](/ConnectDatabase/Exercise_9_ImplementJPAEntity.md)
- [Exercise 10: Deploy Ads on Cloud Foundry](/ConnectDatabase/Exercise_10_DeployAdsWithDBServiceOnCF.md)
- [Exercise 11: [Optional] Implement a Custom Query](/ConnectDatabase/Exercise_11_Develop_Custom_Queries.md)

## Week 4: Service-to-Service Communication

**Logging and Tracing**
- [Exercise 12: Setup Logger](/LoggingTracing/Exercise_12_Setup_Logger.md)
- [Exercise 13: Use SLF4J Features](/LoggingTracing/Exercise_13_Use_SLF4J_Features.md)
- [Exercise 14: Analyze Log/Traces in Kibana](/LoggingTracing/Exercise_14_GettingStarted_With_ELK_Stack.md)

**Service-to-Service Communication**
- [Exercise 16: Call User Service (synchronuous)](/Service2ServiceCommunication/Exercise_16_Call_UserService.md)
- [Exercise 17: Call User Service via Hystrix](/Service2ServiceCommunication/Exercise_17_Introduce_Hystrix.md)
- [Exercise 18: Make Communication more Resilient](/Service2ServiceCommunication/Exercise_18_Make_Communication_Resilient.md)
- [Exercise 19: [Optional] Hand-over Correlation-ID](/Service2ServiceCommunication/Exercise_19_Transfer_CorrelationID.md)
- [Exercise 20: [Optional] Use Message Queues](/Service2ServiceCommunication/Exercise_20_Use_Message_Queues.md)
- [Exercise 21: [Optional] Receive MQ Messages](/Service2ServiceCommunication/Exercise_21_Receive_MQ_Messages.md)

## Week 5: Security

**Security**
- [Exercise 22: Deploy Application Router for Authentication](/Security/Exercise_22_DeployApplicationRouter.md)
- [Exercise 23: Setup generic Authorization](/Security/Exercise_23_SetupGenericAuthorization.md)
- [Exercise 24: Make Application Secure](/Security/Exercise_24_MakeYourApplicationSecure.md)
- [Exercise 24.2: Administrate Authorizations](/Security/Exercise_24_Part2_Administrate_Authorization.md)

## Week 6: Additional Topics

**No Exercises**

# <img src="./Z_ReuseImages/images/281518_OpenBook_R_blue.png" height="80" alt="Further Resources"/> Further Resources

* [Troubleshooting VM image installation](/VMImage/VMImage_GettingStarted.md#troubleshooting)
* [Troubleshooting Maven, Eclipse](/Knowledge/TroubleShooting.md)
* [Eclipse IDE Tips](/Knowledge/EclipseIdeTips.md)
* [Cloud Foundry CheatSheet](https://blog.anynines.com/cloud-foundry-command-line-cheat-sheetutm_sourcecf-summitutm_mediumprintutm_campaigncf-summit-cheat-sheet/)
* [Hamcrest (matchers) CheatSheet](http://www.marcphilipp.de/downloads/posts/2013-01-02-hamcrest-quick-reference/Hamcrest-1.3.pdf)
* [Cloud Foundry Basic Overview](http://docs.cloudfoundry.org/concepts/overview.html)
* [Cloud Foundry Component Overview](http://docs.cloudfoundry.org/concepts/architecture/)
* [Migrating to Cloud-Native Application Architectures (free PDF ebook)](http://pivotal.io/platform/migrating-to-cloud-native-application-architectures-ebook)
* [Pivotal Cloud Foundry Glossary](http://docs.pivotal.io/pivotalcf/concepts/glossary.html)
* [FAQ - Frequently Asked Questions regarding the Eclipse IDE](/Knowledge/EclipseIdeTips.md)
* [JSON Conversion](/Knowledge/JSONConversion.md)

## Known issues

Please look into GitHub issues for any issues reported.

## Support

Please use GitHub issues for any bugs to be reported.

## License

Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
This file is licensed under the Apache Software License, v. 2 except as noted otherwise in the [LICENSE](./LICENSE) file.

Exercise 6: Deploy Ads on the Cloud Foundry Environment
=======================================
## Learning Goal
Get familiar with the basic commands of the Cloud Foundry CLI, learn how to deploy your advertisement service into the cloud, and understand how microservices are managed.

## Prerequisite
Continue with your solution of the last exercise. If this does not work, you can checkout the branch [origin/solution-5-ValidationExceptions](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-5-ValidationExceptions).

Prepare a Trial Account and Space on the SAP Business Technology Platform Cloud Foundry environment
- Create your own **Trial Account and Space** on the **Cloud Foundry environment** using the [**self-service on SAP Business Technology Platform Cockpit**](https://account.hanatrial.ondemand.com). 

## Step 1: Login
The following commands will setup your environment to use the provided Cloud Foundry instance.

 - `cf api <<Your API endpoint>>`
 - `cf login -u <<your user id>>`
 - In case you are assigned to multiple orgs, select the `trial` organisation.

To find the API end point, please refer the [documentation]( https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/350356d1dc314d3199dca15bd2ab9b0e.html)

## Step 2: Create `manifest.yml`
In the root directory of your project, create a new file named `manifest.yml` and fill it with the following data:

```
---
applications:
- name: bulletinboard-ads
  memory: 1G
  timeout: 360
  path: target/bulletinboard-ads.war
  buildpack: https://github.com/cloudfoundry/java-buildpack.git
  env:
      # Use the non-blocking /dev/urandom instead of the default to generate random numbers.
      # This may help with slow startup times, especially when using Spring Boot.
      JAVA_OPTS: -Djava.security.egd=file:///dev/./urandom
```
Note: In case you make use of the Community Java Buildpack it is recommended to specify the **version of the buildpack** e.g. `buildpack: https://github.com/cloudfoundry/java-buildpack.git#v4.16.1`, you can get the current version using `cf buildpacks` and on Github there must be a so-called `release` for every released buildpack version.

## Step 3: Push Your Service
- Before you push your service into the cloud, make sure to build the WAR file (`mvn clean verify`). 

- The name `bulletinboard-ads` specified in the manifest file is used as hostname and is already used in this CF instance. With this in mind, push your microservice using another hostname:
  ```
  cf push -n bulletinboard-ads-<<your user id>>
  ```
  Make sure the execution is successful.
- Use a browser and the `Postman` REST client to test whether your microservice runs in the cloud.
For this use the URL `https://bulletinboard-ads-<<your user id>>.cfapps.<<region>>.hana.ondemand.com/api/v1/ads/`. The `<<region>>` needs to be replaced with eu10 or us10 depending on the trial environment where you have registered. For more details, please refer the [documentation](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/350356d1dc314d3199dca15bd2ab9b0e.html)

Note: In order to build the WAR file without running the tests, you can use `mvn clean package -DskipTests`.

## Step 4: Scale Your Service
Currently we only run one instance of the microservice.
Run `cf scale bulletinboard-ads -i 2` to scale your microservice to two instances, so that the load is spread and a single crash is less harmful.

Manually create and retrieve advertisements and observe the responses. You might recognize that you get different responses depending on the service instance you are talking to. Each service instance runs in a fully separated process. In our current example, the hash maps are not in sync.

> Note:
> When you deploy your application into your Cloud Foundry `trial` subaccount then you're limited to **2GB memory**. In  order to monitor the quota you need to open the [SAP BTP Cockpit](https://account.hanatrial.ondemand.com/cockpit#/home/overview)
, navigate into your `trial` subaccount e.g. by using the `Go to Cloud Foundry trial` button.

## [Optional] Step 5: Explore the SAP Business Technology Platform Cockpit 

Open the [SAP Business Technology Platform Cockpit](https://account.hanatrial.ondemand.com/cockpit#/home/overview)
- Navigate first into your **Trial Global Account** and find out how much memory (in GByte) is assigned to it and which kind of services you are allowed use.
- Then navigate into your `trial` **Subbaccount** and find out how much of the memory you have already consumed by the applications deployed  in all of your Cloud Foundry spaces.
- Now navigate into your `dev` Cloud Foundry **Space** and make sure that only one single application instance is running. 
- Finally assign a colleague in the role of an `Auditor` to your space and let them check, whether they see the space as well in its Cockpit.

![](/CloudFoundryBasics/images/SAPCockpit.jpg) 

## [Optional] Step 6: Add Heartbeat URL to the Application Manifest
There are various attributes that can be used to configure the deployment of your application, which are documented [here](https://docs.cloudfoundry.org/devguide/deploy-apps/manifest.html).

For example you can configure the health check in such a way that it considers the application as healthy, when the `health` endpoint returns HTTP `200` status within 1 second. For this you need to specify the following attributes in your `manifest.yml`:

```
---
applications:
- name: bulletinboard-ads
  health-check-type: http
  health-check-http-endpoint: /health
```
Note: By default the health check tries to establish a TCP connection to an application port within 1 second.  

Now deploy your application again and make sure that it starts and does not crash. In case you face a Server error when deploying your application similar to `Server error, status code: 400, error code: 100001, message: The app is invalid: health_check_http_endpoint HTTP health check endpoint is not a valid URI path:`: Check the version of your Cloud Foundry CLI version using `cf --version` and make sure that it is **>=6.27.0**. You can upgrade the version as documented [here](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html).

Note also that the `health` endpoint remains accessible even after introduction of authentication and authorization checks as part of **[Exercise 24](../Security/Exercise_24_MakeYourApplicationSecure.md)**.

## Further Reading
- [Cloud Foundry Command Line Interface (cf CLI)](https://docs.cloudfoundry.org/cf-cli/index.html)
- [Cloud Foundry CheatSheet](https://blog.anynines.com/cloud-foundry-command-line-cheat-sheetutm_sourcecf-summitutm_mediumprintutm_campaigncf-summit-cheat-sheet/)
- [Troubleshooting Application Deployment and Health](https://docs.cloudfoundry.org/devguide/deploy-apps/troubleshoot-app-health.html)

***
<dl>
  <dd>
  <div class="footer">&copy; 2018 SAP SE</div>
  </dd>
</dl>
<hr>
<a href="/CreateMicroservice/Exercise_5_ValidationAndExceptions.md">
  <img align="left" alt="Previous Exercise">
</a>
<a href="/ConnectDatabase/Exercise_7_ConnectLocalDatabase.md">
  <img align="right" alt="Next Exercise">
</a>

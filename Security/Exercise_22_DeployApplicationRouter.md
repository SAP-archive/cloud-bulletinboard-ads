# Exercise 22: Deploy Application Router and Set Up Authentication

## Learning Goal
You need your own application router that connects your service to the centrally provided "user account and authentication (UAA) service". Technically this means that you need to deploy an approuter as part of your application that manages the user authentication for you.

The [approuter](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/01c5f9ba7d6847aaaf069d153b981b51.html) has these main functions:
* Handles authentication for all apps of the application 
* Serves static resources
* Performs route mapping (URL mapping)
* In case of multi tenancy it derives the tenant information from the url and provides it to the XSUAA to redirect the authentication request to the tenant specific identity provider.


<img src="/Security/images/app-router-diagram.png" width="400">


## Prerequisite

Continue with your solution of the last exercise. If this does not work, you can checkout the branch [origin/solution-19-Transfer-CorrelationID](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-19-Transfer-CorrelationID).

Ensure that [Node.JS including its NPM Packager Manager](https://nodejs.org/en/) is installed: 
```
node --version
npm --version
```

## Step 1: Setup the Application Router as Part of Your Application
- Inside your `cc-bulletinboard-ads` directory create a directory with the name `src/main/approuter` (right-click on the project, then select "New" - "Folder"). 
- In there create a file named **`.npmrc`** with the following content specifying the registry:
```
@sap:registry=https://npm.sap.com
```
- Furthermore create a file named **`package.json`** with the following content (similar to `pom.xml`):
```
{
    "name": "approuter",
    "dependencies": {
        "@sap/approuter": "3.0.1"
    },
    "scripts": {
        "start": "node node_modules/@sap/approuter/approuter.js"
    },
    "engines": {
        "node": "6"
    }
}
```

## [Optional] Step 2: Build Approuter Once (Locally)
Like we are doing it for our bulletinboard-ads Java application, we also recommend to build the approuter Node application once (locally or later triggered as part of the Continuous Delivery build) before deploying it to Cloud Foundry. Therefore we use the NPM Packager Manager to download the packages (`node_modules`) as specified in the `package.json`.

In order to avoid Eclipse crashes / getting slow while parsing packages downloaded via NPM in the next step, ensure that there is a resource filter defined for the `node_modules` directory as visualized in the screenshot (project properties):
<img src="/Security/images/Eclipse_Mars_node_modules.png" width="400">

Now execute in the terminal (within directory `src/main/approuter`):  
```
npm install
```
With this the node modules are downloaded by the NPM package manager from the `https://npm.sap.com` SAP external NPM repository (aka registry) and are copied into directory `src/main/approuter/node_modules/@sap/approuter`. 


## Step 3: Update `manifest.yml`
As the approuter is a Node.JS application, it needs to be added into the `manifest.yml` as another sub element of the `applications` element, just like `bulletinboard-ads`:
```
- name: approuter
  host: approuter-<<your user id>>
  path: src/main/approuter
  buildpack: https://github.com/cloudfoundry/nodejs-buildpack.git#v1.6.10
  memory: 128M
  env:
    XSAPPNAME: bulletinboard-<<your user id>>
    TENANT_HOST_PATTERN: "^(.*)-approuter-<<your user id>>.cfapps.<<region>>.hana.ondemand.com"
    destinations: >
      [
        {"name":"ads-destination", 
         "url":"https://bulletinboard-ads-<<your user id>>.cfapps.<<region>>.hana.ondemand.com",
         "forwardAuthToken": true}
      ]
  services:
    - applogs-bulletinboard
    - uaa-bulletinboard
```
**Furthermore you need to specify the `host` of your `bulletinboard-ads` application as well**. For example: `host: bulletinboard-ads-<<your user id>>`. Reason: As the `manifest.yml` contains now multiple applications you are not longer able to specify the host using the command line flag `-n`. The references to `<<region>>` needs to be replaced with eu10 or us10 depending on the trial environment where you have registered. For more details, please refer the [documentation](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/350356d1dc314d3199dca15bd2ab9b0e.html)

> **Note**: Even though the `approuter` is not "stateless" (as it maps the `SessionID` to the `JWT token`) the amount of `approuter` instances depends on the load on the business application. This is possible as Session stickiness is implemented by Cloud Foundry with the `VCAPID` header. Using the same header value in the following requests causes the Cloud Foundry router to route those requests to the same (`approuter`) application instance. BUT: applications MUST NOT rely on being called by the same `approuter` instance during a session. The `approuter` instance could change if the old instance dies and a new instance is created during the recovery procedure.

## Step 4 Configure the Application Router
Now create in the `src/main/approuter` directory a file named `xs-app.json` with the following content:
```
{
  "welcomeFile": "index.html",
  "routes": [{
    "source": "^/ads",
    "target": "/",
    "destination": "ads-destination"
  }]
}
```
If you like to provide a "welcome file" then you need also to add an `index.html` file, that must be created within the `src/main/approuter/resources` directory. Be aware that the "ads-destination" destination is already specified as system environment variable in the `manifest.yml`.

> **Note**: The `ads-destination` used in this file is a logical destination that is mapped to the `ads-destination` defined in the `manifest.yml`. This is because the 'real URL' is defined at deploy time (it may even be a 'random route') and the `xs-app.json` is concerned only with the endpoints relative to the app URL. 

## Step 5: Deploy Approuter and Application to Cloud Foundry

In this step we create an XSUAA service instance that is able to serve requests from multiple customers, so called tenants. 

- Create an XSUAA service instance with name `uaa-bulletinboard` and replace `xsappname` placeholder accordingly.  
**Note**: The following statement works on **Linux/Mac only** (get other examples with `cf cs -h`).
```
$   cf create-service xsuaa application uaa-bulletinboard -c '{"xsappname":"bulletinboard-<Your user id>"}'
```

- Deploy the applications with:
```
$   cf push     # host names are already specified in the manifest
```
- Then have a look at the XS UAA service connection information that is part of the `VCAP_SERVICES` environment variable and try to find the `identityzone`.

```
$   cf env approuter
```
> **Important Note:** The value of `identityzone` matches the value of **`subdomain`** name of the subaccount and represents the name of the **tenant** for the next steps.

- Enter `cf routes` to see which routes are already mapped to your applications. Every tenant consuming your application needs his own route, prefixed with **tenant** (e.g. `p20001234trial`) and needs to be created with the following command:
```
$   cf map-route approuter cfapps.<<region>>.hana.ondemand.com -n <<your tenant>>-approuter-<<your user id>>
```

## Step 6: Access Your Application Via the Approuter
Observe how the authentication works:
- Get the url of the approuter via `cf apps`. 
- Then enter the approuter URL e.g. `https://<<your tenant>>-approuter-<<your user id>>.cfapps.<<region>>.hana.ondemand.com` in the browser. This should redirect you to the XS-UAA Logon Screen. Note that you've configured your approuter on how to derive the tenant from the URL according to the `TENANT_HOST_PATTERN` that you've provided as part of the `manifest.yml`.
- You will be redirected to SAP User ID Service (https://accounts.sap.com), login with your SAP email address and domain password.
- After successful login to you will get redirected to the welcome page if you've defined one. 

Observe the route / path mappings:
- Test what happens if you enter `ads` or `ads/health` as path and again check out the welcome page.
- Test what happens if you enter `ads/api/v1/ads/1`

So, what have you achieved right now? You have made your application accessible via the application router, that authenticates the user before they get redirected to the application.

**BUT your application is still not secure!**
- Any user can access any service endpoint with an invalid or even without security token, without any permissions. That needs to be prevented and we will do this in the next exercise.



## Further Remarks
- Application router also implements Cross-Site Request Forgery (CSRF) protection. A modification request (PUT, POST, DELETE, etc.) is rejected unless the request contains a valid x-csrf-token header. Clients can fetch this token after successful authentication/authorization. It is enough to fetch this token only once per session. The x-csrf-token can be obtained with HTTP header `x-csrf-token: fetch`.

- Application router can be configured to initiate a central logout after a specified time of inactivity (no requests have been sent to the application router). Assign the time of inactivity in minutes to the environment variable `SESSION_TIMEOUT` in your `manifest.yml` file. Ensure that the timeout configuration for application router and XSUAA are identical for all routes with authentication type `xsuaa`

- Each instance of application router holds it´s own mappings of JWT-Tokens to jSessionIDs. The mappings are neither shared across multiple instances of application router nor are the mappings persisted in a common persistency. Application router uses Cloud Foundry session stickiness (VCAP_ID) to ensure that requests belonging to the same session are routed via the same application router instance - this means that the user will need to re-authenticate if an application router instance of a particular session goes down and is recovered by a new application router instance.

## Further Resources
- [sap.help.com: Configure Application Router](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/01c5f9ba7d6847aaaf069d153b981b51.html)

***
<dl>
  <dd>
  <div class="footer">&copy; 2018 SAP SE</div>
  </dd>
</dl>
<hr>
<a href="/Service2ServiceCommunication/Exercise_21_Receive_MQ_Messages.md">
  <img align="left" alt="Previous Exercise">
</a>
<a href="Exercise_23_SetupGenericAuthorization.md">
  <img align="right" alt="Next Exercise">
</a>



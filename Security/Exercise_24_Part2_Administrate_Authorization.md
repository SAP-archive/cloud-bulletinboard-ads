# Exercise 24 Part 2: Administrate Authorizations

## Learning Goal
As of now you've configured your xsuaa service with the application security model ([xs-security.json](https://github.com/SAP/cloud-bulletinboard-ads/blob/solution-24-Make-App-Secure/security/xs-security.json)). With that, the xsuaa has the knowledge about the role-templates and role-collections. But you as a User have still no permission to access the advertisement endpoints, as the required scopes or roles are not yet assigned to your user.

In this exercise you will use the SAP BTP Cockpit to maintain authorizations for your application and assign them to you or to other members of your Subaccount (cloud foundry organization).

## Prerequisite
Continue with your solution of the last exercise. If this does not work, you can checkout the branch [solution-24-Make-App-Secure-Spring5](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-24-Make-App-Secure-Spring5).


## Step 1: Administrate Authorizations for your Business Application
To administrate authorizations for your business application, you need to assign the Role Collection to your user. To do so,  perform the following steps:

- In the cockpit, e.g. [https://account.hana.ondemand.com/cockpit/#/home/allaccounts]() navigate to your `Subaccount`. Choose `Security` --> `Trust Configuration`.
- Click on the link **SAP ID Service** - the default trust configuration. 
- Now, in the `Role Collection Assignment` UI, enter your user id used to logon to the current account and click on button **Add Assignments** to assign new Role Collections to the user as visualized in the screenshot:

<img src="/Security/images/CockpitRoleCollectionAssignToUser.png" width="700">  

Further up-to-date information you can get on [sap.help.com: Assign Role Collections](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/9e1bf57130ef466e8017eab298b40e5e.html).


## Step 2: Call deployed service
You need to logon again to your application so that the authorities are assigned to the user. You can provoke a logon screen when clearing your cache. 

Call your service endpoints via the approuter e.g. `https://<<your tenant>>-approuter-<<your user id>>.cfapps.<<region>>.hana.ondemand.com/ads/api/v1/ads` manually using the `Postman` Chrome plugin as explained [here](/Security/Exercise_24_MakeYourApplicationSecure.md#call-deployed-service).

Now you should have full access to all of your application endpoints.


> **Troubleshoot**
> You can analyze the authorities that are assigned to the current user via `https://<<your user id>>trial.authentication.<<region>>.hana.ondemand.com/config?action=who`


## Further Reading
- [sap.help.com: Authorization and Trust Management in the Cloud Foundry Environment](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/6373bb7a96114d619bfdfdc6f505d1b9.html).

***
<dl>
  <dd>
  <div class="footer">&copy; 2017 SAP SE</div>
  </dd>
</dl>
<hr>
<a href="Exercise_24_MakeYourApplicationSecure.md">
  <img align="left" alt="Previous Exercise">
</a>

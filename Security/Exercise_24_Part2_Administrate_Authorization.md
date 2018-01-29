# Exercise 24 Part 2: Administrate Authorizations

## Learning Goal
As of now you've configured your xsuaa service with the application security model ([xs-security.json](https://github.wdf.sap.corp/cc-java/cc-bulletinboard-ads-spring-webmvc/blob/solution-24-Make-App-Secure/security/xs-security.json)).<sub><b>[to-do]</b></sub> With that, the xsuaa has the knowledge about the role-templates. But you as a User have still no permission to access the advertisement endpoints, as the required scopes or roles are not yet assigned to your user.

In this exercise you will use the SAP CP Cockpit to maintain authorizations for your application and assign them to you or to other members of your Subaccount (cloud foundry organization). 

## Step: Administrate Authorizations for your Business Application
To administrate authorizations for your business application, perform the following steps:

- Navigate to your Subaccount
- Select menu item **Roles** from the **Security** menu on the left side of the screen and perform the procedure visualized in the screenshots:  
<img src="/Security/images/CockpitRoleCollectionCreate.jpg" width="700">  
<img src="/Security/images/CockpitRoleCollectionAddRole.jpg" width="700">  

- Navigate back to your Subaccount  
- Select menu item **Trust Configuration** from the **Security** menu on the left side of the screen and select the standard IdP SAP ID Service. Now perform the procedure visualized in the screenshot:    
<img src="/Security/images/CockpitRoleCollectionAssignToUser.jpg" width="700">  

- Afterwards you need to logon again to your application so that the authorities are assigned to the user. You can provoke a logon screen when clearing your cache. Now you should have full access to all of your application endpoints.

> **Troubleshoot**
> You can analyze the authorities that are assigned to the current user via `https://d012345trial.authentication.sap.hana.ondemand.com/config?action=who`


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

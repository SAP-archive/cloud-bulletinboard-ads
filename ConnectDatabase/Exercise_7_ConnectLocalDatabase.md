Exercise 7: Connect to Local Database Within Eclipse IDE
========================================================

## Learning Goal
In this exercise you will learn how to setup a connection to the database running inside your virtual machine (Note: All backing services needed in this course are also running locally in the VM to allow local tests and debugging).
Furthermore, you will get to know the `VCAP_SERVICES` environment variable and understand how it can be used for local databases.

## Prerequisite
Continue with your solution of the last exercise. If this does not work, you can checkout the branch [`origin/solution-6-Deploy-Ads-On-CF`](https://github.com/SAP/cloud-bulletinboard-ads/tree/solution-6-Deploy-Ads-On-CF).

## Step 1: Connect to Local Database in Eclipse
We want to manage the connection to the PostgreSQL database within Eclipse using the [`DBeaver` eclipse plugin](https://marketplace.eclipse.org/content/dbeaver).

Open the `DBeaver` perspective (under `Window` -> `Perspective` -> `Open Perspective`).

We have to create a new connection in the `Database Navigator` view as follows:

* Right click and select `Create New Connection`
* Select "PostgreSQL" from the list of connection types and hit "Next".
* Enter the following settings (also see screenshot) and proceed. All the other settings may remain at their default values. The JDBC URL will be filled automatically.
  * host: localhost
  * port: 5432
  * database (name): **test**
  * user: testuser
  * password: test123!
* Pressing the button "Test Connection" or switching to the "Driver properties" tab will open a download screen which will download the appropriate driver for you.
* Press "Next" or "Finish" and the creation of the connection is completed.

![Connect to PostgreSql Database](images/Connect_Database.png)

You can connect to the database by simply double-clicking the connection in the `Database Navigator` view.

If you get the error message "Can't create driver instance" when trying to connect, the PostgreSQL driver was not (successfully) downloaded. Retry by editing the connection and make sure to confirm the download. 

Make sure, that the `test` database appears in **bold**, which means that this is the current active one.

You can view the contents of a database table by double-clicking the name of the table under `public` -> `Tables` and then choose `Data` from the main view.

## Step 2: Understand VCAP_SERVICES
Open the file [`localEnvironmentSetup.bat`](https://github.com/SAP/cloud-bulletinboard-ads/blob/master/localEnvironmentSetup.bat) (or `localEnvironmentSetup.sh`) in an editor.
Find the definition of the `VCAP_SERVICES` local variable and understand how it is used to define the database connection information.


## Further Reading / Tools
- [Cloud Foundry Environment Variables](http://docs.run.pivotal.io/devguide/deploy-apps/environment-variable.html#VCAP-SERVICES)
- [DBeaver for Eclipse](https://marketplace.eclipse.org/content/dbeaver) for a convenient access of your local database.


***
<dl>
  <dd>
  <div class="footer">&copy; 2018 SAP SE</div>
  </dd>
</dl>
<hr>
<a href="/CloudFoundryBasics/Exercise_6_DeployAdsOnCloudFoundry.md">
  <img align="left" alt="Previous Exercise">
</a>
<a href="Exercise_8_Part1_ConfigurePersistence.md">
  <img align="right" alt="Next Exercise">
</a>

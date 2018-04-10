# TroubleShooting

## Checkout branch in Eclipse
If you want to checkout the branch within **Eclipse**, you need to open the `Git Repositories` View:
-	To avoid merging conflicts [stash](https://git-scm.com/docs/git-stash) your uncommitted changes: `Right click on your repository` - `Stashes` - `Stash Changes...`
-	Then checkout the remote branch: `Double Click on the branch node` - `Checkout...` - `Checkout as New Local Branch`.
- **Note** that `git reset --hard` might not be sufficient for avoiding conflicts with upcoming branches, as it undo's only changes from files already tracked by git. Let's assume you're currently working on the `master` branch and you have created a bunch of classes in context of the exercises, those classes created by yourself are not considered by git and not reverted by git.

## Run Maven Build without tests
Let's assume you would like to create a `war`-file but the tests are currently failing. You can skip the tests for now by executing:
```
mvn clean verify -DskipTests
```

## Issues on Cloud Foundry
If this happens in the course you should definitively get in contact with the SAP Cloud Platform Cloud Foundry team. 
Find here some options on where to find current issues and how to create a support ticket:
https://github.infra.hana.ondemand.com/cloudfoundry/cf-docs/wiki/CF-EU10-CANARY 

For the `Kibana`, `HANA` and `Security` demo the SAP Cloud Platform Cloud Foundry environment is mandatory! For the rest you can also get a trial account at any other cloud foundry provider.

Examples:
- anynines.com
- pivotal.io

## Eclipse - Tomcat Integration
Especially when switching branches very often, we faced issues in restarting the app on Tomcat within Eclipse. It seems that the `/target/m2e-wtp` folder gets messed up, which is specified as part of the `Deployment Assembly` properties.
The deletion of the `target` folder should help in this case.

## Issues with `pom.xml` or "Error Updating Maven Configurations"
If you receive "Error Updating Maven Configurations" while doing maven update (ALT+F5), or dependencies can't be resolved...
The root cause could be that dependencies cannot be downloaded (possibly due to network issues or proxy setting problems before). In that case one of these options should solve it.

### Option 1: Try with forced maven update
- When doing `ALT+F5`, mark the checkbox "force update" OR
- run `mvn clean verify -U` in the command line (-U forces a retry on failed downloads), OR

### Option 2: Purge your local maven repository
You cannot make Maven re-download dependencies, but what you can do instead is to cleanup dependencies that were incorrectly downloaded using `mvn dependency:purge-local-repository`.

Or alternatively delete the whole `/home/.m2/repository` directory. **Never delete the `.m2` folder**, just delete the `repository` or one of its subfolders!

And then run `mvn clean verify` in the command line and evaluate the log for possible issues.

## Issues with network
- We've observed that sometimes, on the VirtualBox VM, colleagues could not reach SAP internal pages like e.g. https://github.wdf.sap.corp, although their laptop was connected with the corporate network. This issue can be resolved by simply restarting the VirtualBox VM.
- This [documentation](/Knowledge/BasicNetworkKnowhow.md) describes some basic networking knowhow that may come in handy when you need to troubleshoot these situations.

## Other Eclipse/VM Issues
It may happen that the files inside the VM become corrupted.
If Eclipse does not start or misses critical views and settings, you can restore the workspace as follows:

### Option 1
In the terminal run `eclipse -clearPersistedState` (helps in most cases) or `eclipse -clean -clearPersistedState`.

### Option 2
 - Close Eclipse
 - Open a terminal and move the old workspace: `mv ~/workspace/ ~/workspace-broken/`
 - Download [this file](https://github.wdf.sap.corp/agile-se/vagrant-development-box/blob/master/workspace.tar.gz?raw=true) to `~/Downloads` (**Note**: the VM might already contain this file at `~/workspace.tar.gz`)
 - Unpack the contents to `~/workspace`: `cd ~ && tar xvzf ~/Downloads/workspace.tar.gz`
 - Re-import the project :
   - `File` - `Import...` - `General` - `Existing Projects into Workspace`
   - `Select root directory` - `Home` - `git/cc-bulletinboard-ads/`
   - Update Maven Project: `ALT+F5`, check **"Force Update of Snapshots/Releases"**




# Getting Started with the Virtual Box Image

### Install Oracle VM Virtual Box version 5.1.x
 
- Install the relevant [Oracle VM Virtual Box 5.1.x platform packages](https://www.virtualbox.org/wiki/Downloads) <b>for your OS, on your own computer</b> to be able to run the virtual machine (information for SAP-internal participants: Oracle VM Virtual Box 5.1.x is grey listed).
- **Hint**: You can change the UI language in `File - Preferences - Interface Language`

### Load and Run the Virtual Machine (VM)
- Make sure you have at least 15 GB of free disk space
- Download the [Virtual Box VM Image](https://sap-my.sharepoint.com/:f:/p/sven_kohlhaas/EjwDmYwrdcRAio35jRYnTi8BGmd1sIkO1K5_uecz69GGUQ?e=PqMZR1) (about <b>4.77</b> GB).
- Start Oracle VM Virtual Box and import the downloaded Virtual Box VM Image: Menu: `File - Import Appliance`
- After a successful import you can delete the downloaded image (<VM image name>.ova) file since Virtual Box converts it and stores it under `~/VirtualBox VMs`
- (Optional) If your computer has **more** than 8 GB of RAM, you might want to increase the VMs memory from 3 GB to e.g. 4 GB. For that, right-click on the VM in Virtual Box and change the memory settings in the "System" tab on the left (4 GB is 4096 MB).
- Start the imported VM and **[customize the VM](VMImage_GettingStarted.md#inside-the-vm)**. 

## Advanced / Optional

### Update 'Guest Additions'

At the first time you use the VM (or any time later), you may get a popup that states that the 'Ubuntu guest additions' have a lower release than the version of your Virtual Box. Guest additions are additional installs on the guest OS (Ubuntu) that support Virtual Box features. Virtual Box and Guest Additions release should be in sync.

In order to install guest additions:
* The VM must be 'powered off', i.e. you should shut down.
* With our VM selected, click on `Settings - Storage - Add(+) - Add Optical Drive - Leave Empty `. Now you have a virtual CD ROM drive
* Start the VM again.
* In the top menu (Virtual Box) select `Devices - Insert Guest Additions CD Image`. When asked for the password, enter your image password (default 'vagrant') and hit `Run` on the resulting popup.
* This will bring up a terminal window that shows the installer output. Wait until prompted to close the window.

### Exporting / Importing Files with the windows host

When you work with the VM, all your files will be inside the file system of the VM. If you want to export e.g. your source files or jenkins job configurations at the end of the course to keep them for later outside of  the VM, you can mount a shared folder that is accessible within the VM and outside on the windows host. Since you could have multiple VMs we could not have a predefined shared folder but you have to define one yourself as follows:
* Shutdown the VM if it is running and open the `Virtual Box Manager`. Select the IDE VM and click on `Settings`, and then click on `Shared Folders`. Now click on the `+` button to add a shared folder.
* First select a folder path that is under your VM path, e.g. `C:\Users\<your user>\VirtualBox VMs\vagrant-development-box_<rest of the path>\shared` (you have to create the `shared` folder in Windows). Keep the default value `shared` for `folder name` and select `Automount` and `Make permanent`.
* Restart your VM. The shared folder will now appear in the VM under `/media/sf_shared` (the `sf_` prefix is added automatically). But you cannot access the folder yet from within the VM (you will get 'permission denied').
* To enable access to the shared folder in Ubuntu you must do `sudo adduser <user-for-the-image> vboxsf` in a shell window. Then restart the VM for the setting to take effect.
* Now, you can exchange files between the windows host and the Ubuntu guest VM via this folder. To copy all files from a VM directory (with subdirectories) to the shared directory type e.g.:
```
cp -r cc-bulletinboard-ads /media/sf_shared/cc-bulletinboard-ads
```

## Troubleshooting
- For troubleshooting see the **[troubleshooting section](VMImage_GettingStarted.md#troubleshooting)**.

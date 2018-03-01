# Getting Started with the VM Image

We offer a VM image that contains all needed tools for the Cloud Development courses. The image runs in VirtualBox.

## Setting up the Virtual Box Image
Follow these steps to setup your Virtual Box [Click Here](VMImage_GettingStarted_VirtualBox.md)

<div id="inside-the-vm"></div>

## Customize the VM
- After the setup of VirtualBox and importing the provided VM Image, start the Virtual Machine and customize the VM according the following guidance/steps.
- Note: In case you run into trouble, you might need to change some BIOS settings as described in the **[troubleshooting section](#troubleshooting)** below

#### Download and install API Development and Testing Tool

- Download and install an API Development and Testing Tool such as [Postman](https://www.getpostman.com/) inside of the VM

#### General
- The user for the image is `vagrant` with password `vagrant`.
- Always do a proper `shut down` of the Virtual Machine after work (click on the top right icon). Do not force `Power off`, as otherwise Eclipse might hang on startup and/or data can get inconsistent. If you face this, check the "Troubleshooting" section below.

<img src="images/VM_Shut_Down.png " width="300" />

#### Configure Localization
- You can configure the proxy settings, time zone, and keyboard layout using a dedicated script. For that, double-click the `Localization` link on the Desktop. If there is no `Localization` link, try to execute `source localization.sh` in the terminal.
(Before configuring the proxy settings, make sure you enter your proxy details in the **`.proxy_environment_template_on`** file which you will find in your home directory)

<img src="images/VM_Execute_Localization.png " width="300" />

  - **Important**: restart the VM after this process as some processes will only re-read the changed settings on system startup
  - When your Virtual Machine has started, you will see the `Unity` desktop manager of ubuntu. Change the **timezone** by clicking on the time in the upper right corner (standard is UTC); select your timezone by clicking on the map.
  - Change some settings:
    - You probaly also need to change the keyboard layout by (right) clicking the "en" icon in the upper right. Select the language from the drop down list. If this is not available, select "Text Entry Settings" and add the language using the "+" button in the lower left.
    - The menu bar for windows is shown at the top of the screen which is confusing for most people. To make the menu bars appear at the top of the window go to `System Settings ... - Appearance - Behavior` and change `show the menus for a window`.
- Do **not** install the suggested security updates, as the upgrade process may break the virtual machine.

<img src="images/VM_Change_Keyboard_Layout.png " width="300" />

# Troubleshooting

## Chromium asks for 'keyring password'
When you start Chromium and it asks for the **keyring password**, just enter your user password `vagrant` and hit enter. The popup should not appear anymore.

## Eclipse Hangs During Startup
This sometimes happens when you do not shut down the VM properly, while Eclipse is running (see above). Previously this was caused by a buggy extension, but this was fixed already. If the problem resurfaces, try starting Eclipse from the command line with command line argument `-clearPersistedState`. It might also help to use `-clean` as an argument.

## System Level issues

### ThinkPad BIOS settings
* The [article "Enabling Virtualization Technology in Lenovo Systems"](http://amiduos.com/support/knowledge-base/article/enabling-virtualization-in-lenovo-systems) perfectly describes how to adapt the BIOS setup settings.
* Windows general: If BIOS settings are fine and it is still not working, please ensure that Hyper-V is not activated/installed – both cannot be used in parallel. Please check in Control Panel / Programs and Features / De-/Activate Windows features / uncheck ‘Hyper-V’. (see also [this article](https://forums.lenovo.com/t5/Windows-7-Discussion/Intel-VT-Virtualization-Technology-Enabled-but-not-recognized-on/td-p/1599332) ).

### BIOS settings & Hyper-V
Hyper-V needs to be disabled to start the VM in VirtualBox
1. Programs and Features > Turn Windows Features on or off > Disable Hyper-V
2. Restart Windows
3. Try to start VM in Virtual Box

If it still does not work maybe VTx is not enabled in your BIOS.
1.	Restart PC and enter BIOS (only IT might have access - create ITDirect ticket)
2.	For HP notebooks go to „Advanced“ > „Device Configurations“ > Activate „Virtualization Technology (VTx)“ and „Virtualization Technology for Directed I/O (VTd)“
3.  Additional you may need to change the settings under “Security” -> “Set Security Level” as well

### Small Screen Resolution
Sometimes we have observed that the screen resolution is not good (e.g. max. 640x480).
This is often caused by the VM guest addons not being installed or not up to date.

### Virtual Box on Mac 

**Mac users:** During the installation of Virtual Box on your MAC, if Installer gets stuck at "Verifying....", then just restart your MAC. After restart, the installation should work fine.

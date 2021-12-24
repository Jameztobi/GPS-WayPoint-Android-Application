# GPS-WayPoint-Android-Application

SETTING UP ANDROID STUDIO

First and foremost you will need to download and install 
Android Studio which is available for Windows/Linux/OSX and can be found at
https://developer.android.com/studio
Download the relevant installation for your OS and follow the provided instructions. 
Once you this installed open it up and after going through the setup screens you should arrive at a menu that will give you the option to create a new project. 

For each of the examples it is recommended that you create a new project with the following options.
1. Choose “Empty Activity”
2. Give your application a name
3. Set the language to Kotlin
4. Set the minimum SDK to API 23 (this is the current default)
5. click finish

This will setup the environment to be the same as the environment I’m currently using. 
Next you will need to decide if you are using an emulator or an android device for testing. 
If you are using an emulator make sure you pick an image that has an API level 23 or higher and also has support for the google play store. 
You will an image supports the store by seeing the play store icon in the description of the image itself.
You’ll need the play store support for something like “Mock Locations” which is an app used to simulate an android device on the move
Should you wish to use an actual Android device you will need to enable developer mode. 
These instructions will vary from device to device. But the main bit you are looking for is the “Build Number”
Go into your phone settings and find “About Phone” in here you should find “Build Number” keep hitting it repeatedly (about 10 times)
and it should show a message stating “you are now a developer”. 
You should have an extra menu in your settings called “Developer Options” go in here and make sure “USB debugging” is enabled you will need this in order to send your apps to the device. 
When you connect your device via USB while Android Studio is open you should see a dialog pop up on the screen of your device asking you to accept a key from your connected machine. 
Click the checkbox here and then yes. After this in about a minute your device should be visible in android studio next to the play button.

APPLICATION DESCRIPTION
an Android application using only the core views provided by the SDK. 
The application will be an expenses tracker that a user will log their expenses on a day to day basis so they can see where they are spending their money.
A reasonable application of this description should permit a user to enter an income and a number of expenses 
and figure out how much money they have left or owe at the end of a month. One assumption made is that the user will earn a salary once a month.

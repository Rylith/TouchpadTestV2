# Watch Mouse Controller

The application allows the user to control the cursor with an Android device. It is a crossplatform application (untested on Mac OSX).

## Description

The program is in three parts :

- Watch application (Android SDK 20+)
- Mobile application (Android SDK 19+)
- Computer application (Java 8)

Mobile application is a bridge between watch and computer. In a near future, when the watch will can open directly sockets with the computer, the mobile will not be necessary anymore.

In addition to the move and click events, the program offers new and orginal way to reach a distant target. In fact, due to the size of a watch screen, users have to make a lot of movements. This application propose to solve this issue with a new mode when you reach the edge of the watch device.

## Installation

Mobile and watch parts are build as Android Studio Project. You can download Android Studio [here] (https://developer.android.com/studio/index.html).
Import the projects and install them on the desired device.

To install the computer application you need :

- Visual Studio (on Windows) [Download](https://www.visualstudio.com/)
- qMake or make (on OSX and Linux)
- Java Runtime Environment (JRE) 8 or more and Java JDK 8 or more (http://www.oracle.com/technetwork/java/javase/downloads/index.html)

First of all, you need to compile the libpointing application:
  
  If you want the lastest version of libpointing : With a terminal go in the libpointing directory and "git pull"
  
### On Windows 

- Open the "pointing" project with Visual Studio in libpointing directory
- Choose "Release x86" and Generate->"Generate pointing"
- Choose "Release x64" (if your system runs on a 64 bits architecture) and Generate->"Generate pointing"
- You can close the project
- Go to the directory : "libpointing\bindings\Java\Jar"
- Launch "compile.bat" On some Windows system javac application is not in the Path. To add it, find your JDK installation directory (C:\Program Files\Java\jdk1.8.0_92\bin for example) and follow these [instructions](https://www.java.com/en/download/help/path.xml)
- Go to msvc directory
- Edit the libpointingJavaBindingDll.vcxproj (with [NotePad++](https://notepad-plus-plus.org/) for example) : Search "jdk1.8.0_91" (for example) and replace with your own version of JDK (jdk1.8.0_92 for example). If your JDK is for 32 bits Architecture and you are on a 64 bits architecture search "C:\Program Files\Java\jdk1.8.0_91" and replace with "C:\Program Files (x86)\Java\jdk1.8.0_92"
- Save the modifications and open the libpointingJavaBindingDll project with Visual Studio
- Choose "Release x86" and Generate->"Generate libpointingJavaBindingDll"
- Choose "Release x64" (if your system runs on a 64 bits architecture) and Generate->"Generate libpointingJavaBindingDll"
- If everything is fine you have just generated the dll libraries in libpointing\bindings\Java\Jar\build\x86 and/or x64\Release
- You can now generate the jar file with the script "makejar.bat" (Necessary only if you want to edit the code)
- Open a terminal and test your java version with "java -version"
- If your runtime environment is in 64 bits you can launch the computer application with the scripts in MouseMotionServer directory : "run_server_without_test_interface" or "run_server_with_test_interface" 
- Else edit the previous scripts and change x64 by x86

### On Linux/OSX : 

- See the installation guide of libpointing [here](https://github.com/INRIA/libpointing/wiki/Installation)

## How to use?

- Be sure that the ip address of computer is accessible
- Be sure that the bluetooth is activated on mobile and it is connected on the same network of computer
- Launch "run_server_without_test_interface" or "run_server_with_test_interface"
- With test interface go to Tools->Settings and get ip address
- Launch application on mobile
- Put the previously getting ip adress and a port (by default between 4444 and 4453)
- Click to connect, a message tells you if you are connected to the server. Moreover the server print a message to indicate that the connexion is accepted
- You can put your phone in your pocket
- Launch watch application
- Choose your position around the computer
- You can now control the cursor with your watch !

## Description of features

The following describes the different way to control the cursor when you reach the edge of the watch. It could be change dynamically with the option interface.
 - "Accroissement Quadratique" :
 - "Vitesse  = f(angle)" :
 - "Blocage avant" :
 - "Mapping direct angle -> déplacement" :
 - A drag and drop application can be launch. Allow the user to take pictures from an explorer and drop them into the application in order to test the different features.
 
## Edit the application

The server application is build as [Eclipse](https://eclipse.org/downloads/) project. You need the jdk if you want to modify the code or launch the application with Eclipse.

You have to add VM arguments as in the following example [here] (https://github.com/INRIA/libpointing/wiki/Java) (same argument that in scripts) and add to the classpath the previous compiled jar of libpointing (libpointing.jar that you can find in "MouseMotionServer\libpointing\bindings\Java\Jar")

Please consider to NOT commit your own modified classpath and/or your own modified launcher scripts in your fork for a potential merge: 

- git update-index --assume-unchanged MouseMotionServer\\.classpath
- git update-index --assume-unchanged MouseMotionServer\run_server_without_test_interface
- git update-index --assume-unchanged MouseMotionServer\run_server_with_test_interface

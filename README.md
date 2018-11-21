# bisqremote
This app can send Mobile notifications. During the development and testing of the mobile Android and iOS Apps, it can substitute the Bisq Notification Node.

## Documentation of Bisq Mobile
The documentation can be found here: (https://github.com/joachimneumann/bisqremote/wiki).

## installation
1. clone the repository (if you have not already done so)
1. cd into the bisqremote directory and make sure you see the pom.xml file.
1. run these commands:
   mvn install
   mvn exec:java -D exec.mainClass=bisq.notification.NotificationApp

Probably, you want to run this program from eclipse or intellij IDEA, but I can't suport you with this, because it depends on the operating system you are using and I am not an expert in the configuration of this type of projects in IDEs.

## Missing serviceAccountKey.json
1. go to https://firebase.google.com
1. click on "GO TO CONSOLE"
1. add project
1. chose any name you like
1. check the two checkboxes at the bottom
1. create project
1. On the left select Grow, Cloud Messaging
1. select Android
1. Use the package name "com.joachimneumann.bisq"
1. click Register App
1. download google-services.json. You will need this file in the Andoid app.
1. Skip the step "Run your app to verify installation" (it did not work for me)
1. In the firebase page click on the settings icon next to "Project overview" in the top left and select project settings.
1. select service accounts
1. select java
1. copy the parameter in setDatabaseUrl() and paste as value of ANDROID_DATABASE_URL in the file BisqNotificationServer, instead of https://bisqnotifications.firebaseio.com
1. generate a new private push notification key and download the file
1. copy the file google-services.json into the folder bisqremote_Android/Bisq of your local copy of the bisqremote_Android repository
1. run the Android app in Android Studio. Executing the app in the simulator is fine, but you have to log into your google account in the simulator with the same google account that you have used in firebase. If the app has been installed before, you must uninstall it.

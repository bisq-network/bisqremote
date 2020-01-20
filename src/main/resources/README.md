Android
=======
The file serviceAccountKey.json should be placed in this folder.
Create and download from https://console.firebase.google.com/project/XXXXXXX/settings/serviceaccounts/adminsdk, for example
https://console.firebase.google.com/project/bisqnotifications/settings/serviceaccounts/adminsdk

iOS
===
The push notification certificate needs to be renewed every year. The file push_certificate.production.p12 should be placed in this folder.
Create and download from https://developer.apple.com/account/ios/certificate/?teamId=XXXXXX
Then add the *.cer fie to your keychain.
You will probably observe that keychain does not allow you to export the certificate as *.p12 file.
In keychain, go to "My certificates", expand the Apple Push Service certificate and select both lines.
Now, you can export as *.p12 file

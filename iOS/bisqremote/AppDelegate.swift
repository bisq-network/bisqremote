//
//  AppDelegate.swift
//  
//
//  Created by Joachim Neumann on 03/06/2018.
//  Copyright © 2018 joachim Neumann. All rights reserved.
//

import UIKit
import UserNotifications

let userDefaultKeySetupDone = "setup"
let userDefaultSymmetricKey = "SymmetricKey"

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?
    var apsToken: String = "unknown"
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        window?.tintColor = UIColor(red: 37.0/255.0, green: 177.0/255.0, blue: 53.0/255.0, alpha: 1.0)
        registerForPushNotifications()

        // Check if launched from a notification
        if let notification = launchOptions?[.remoteNotification] as? [String: AnyObject] {
            let aps = notification["aps"] as! [String: AnyObject]
            NotificationArray.shared.addFromJSON(new: aps["bisqNotification"])
        }

        if UserDefaults.standard.bool(forKey: userDefaultKeySetupDone) {
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let secondViewController = storyboard.instantiateViewController(withIdentifier: "listScreen") as! NotificationTableViewController
            let rootViewController = self.window?.rootViewController as! UINavigationController
            rootViewController.pushViewController(secondViewController, animated: true)
        }

        return true
    }

    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable : Any]) {
        if application.applicationState == .active {
            print ("active")
        } else if application.applicationState == .background {
            print ("background")
        } else if application.applicationState == .inactive {
            print ("inactive")
        } else{
            print ("message received in undefined strange state")
        }
        
        if let message = userInfo as? [String: AnyObject] {
            let aps = message["aps"] as! [String: AnyObject]
            NotificationArray.shared.addFromJSON(new: aps["bisqNotification"])
        }
    }

    
    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }

    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }

    func registerForPushNotifications() {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) {
            (granted, error) in
            print("iOS Notification: permission granted: \(granted)")
            
            guard granted else {
                if (error != nil) {
                    print("iOS Notification: permission not granted: \(error.debugDescription)")
                }
                return
            }
            self.getNotificationSettings()
        }
    }
    
    func getNotificationSettings() {
        UNUserNotificationCenter.current().getNotificationSettings { (settings) in
            print("Notification settings: \(settings)")
            guard settings.authorizationStatus == .authorized else { return }
            DispatchQueue.main.async {
                let x = UIApplication.shared
                x.registerForRemoteNotifications()
            }
        }
    }
    
    
    
    func application(_ application: UIApplication,
                     didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        let tokenParts = deviceToken.map { data -> String in
            return String(format: "%02.2hhx", data)
        }
        
        apsToken = tokenParts.joined()
        print("### Device token: \n\(apsToken)")
        print("\n### Example notification:\n")
        print(NotificationArray.exampleAPS())
    }
    
    func application(_ application: UIApplication,
                     didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("Failed to register: \(error)")
    }
}


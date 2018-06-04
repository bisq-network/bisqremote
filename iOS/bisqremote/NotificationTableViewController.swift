//
//  NotificationTableViewController.swift
//  bisqremote
//
//  Created by Joachim Neumann on 03/06/2018.
//  Copyright © 2018 joachim Neumann. All rights reserved.
//

import UIKit

class NotificationTableViewController: UITableViewController {

   var bisqNotifications = [BisqNotification]()
    override func viewDidLoad() {
        super.viewDidLoad()
        bisqNotifications = UserDefaults.standard.object(forKey:"bisqNotifications") as? [BisqNotification] ?? [BisqNotification]()
        loadSampleBisqNotifications()
    }

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return bisqNotifications.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cellIdentifier = "NotificationTableViewCell"
        guard let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as? NotificationTableViewCell  else {
            fatalError("The dequeued cell is not an instance of NotificationTableViewCell.")
        }
        
        let notification = bisqNotifications[indexPath.row]
        cell.notificationMessage.text = notification.text
        
        return cell
    }
 

    /*
    // Override to support conditional editing of the table view.
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

    /*
    // Override to support editing the table view.
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            // Delete the row from the data source
            tableView.deleteRows(at: [indexPath], with: .fade)
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    */

    /*
    // Override to support rearranging the table view.
    override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        super.prepare(for: segue, sender: sender)

        switch(segue.identifier ?? "") {
        case "showDetail":
            guard let detailViewController = segue.destination as? NotificationDetailViewController else {
                fatalError("Unexpected destination: \(segue.destination)")
            }
            guard let selectedNotificationTableViewCell = sender as? NotificationTableViewCell else {
                fatalError("Unexpected sender: \(sender ?? "missing sender")")
            }
            
            guard let indexPath = tableView.indexPath(for: selectedNotificationTableViewCell) else {
                fatalError("the selected cell is not being displayed in the table")
            }
            
            let selectedNotification = bisqNotifications[indexPath.row]
            detailViewController.bisqNotification = selectedNotification
            
        default:
            fatalError("Unexpected destination: \(segue.destination)")
        }
    }
    
    private func loadSampleBisqNotifications() {
        let n1 = BisqNotification(_text: "n1")
        let n2 = BisqNotification(_text: "n2")
        let n3 = BisqNotification(_text: "n3")
        bisqNotifications += [n1, n2, n3]
    }

}

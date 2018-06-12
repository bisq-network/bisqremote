//
//  NotificationTableViewController.swift
//  bisqremote
//
//  Created by Joachim Neumann on 03/06/2018.
//  Copyright © 2018 joachim Neumann. All rights reserved.
//

import UIKit

class NotificationTableViewController: UITableViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        if (BisqNotifications.shared.countAll == 0) {
            BisqNotifications.shared.add(new: BisqNotifications.exampleNotification())
        }
    }

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return BisqNotifications.shared.countAll
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cellIdentifier = "NotificationTableViewCell"
        guard let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as? NotificationTableViewCell  else {
            fatalError("The dequeued cell is not an instance of NotificationTableViewCell.")
        }
        
        let notification = BisqNotifications.shared.at(n: indexPath.row)
        cell.notificationMessage.text = notification.message
        
        return cell
    }
 

    /*
    // Override to support conditional editing of the table view.
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

    // Override to support editing the table view.
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            // Delete the row from the data source
            BisqNotifications.shared.remove(n: indexPath.row)
            tableView.deleteRows(at: [indexPath], with: .fade)
//        } else if editingStyle == .insert {
//            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }

    // Override to support rearranging the table view.
    override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {

    }

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
            
            let selectedNotification = BisqNotifications.shared.at(n: indexPath.row)
            detailViewController.aNotification = selectedNotification
            
        default:
            fatalError("Unexpected destination: \(segue.destination)")
        }
    }
    
}


extension Dictionary {
    var prettyPrintedJSON: String {
        do {
            let data: Data = try JSONSerialization.data(withJSONObject: self, options: .prettyPrinted)
            let s = String(data: data, encoding: .utf8)!
            return s
        } catch _ {
            return "could not prettyPrint"
        }
    }
}

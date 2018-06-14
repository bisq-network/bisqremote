//
//  NotificationDetailViewController.swift
//  bisqremote
//
//  Created by Joachim Neumann on 04/06/2018.
//  Copyright © 2018 joachim Neumann. All rights reserved.
//

import UIKit

class NotificationDetailViewController: UIViewController {
    let dateformatterShort = DateFormatter()

    var notification: Notification?
    @IBOutlet weak var textLabel: UILabel!
    @IBOutlet weak var eventTimeLabel: UILabel!
    @IBOutlet weak var receiveTimelabel: UILabel!
    @IBOutlet weak var actionMessage: UITextView!
    @IBOutlet weak var transactionID: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        actionMessage.layer.cornerRadius = 10
        dateformatterShort.dateFormat = "yyyy-MM-dd HH:mm"
        if let n = notification {
            textLabel.text = n.notificationType
            eventTimeLabel.text   = "event:    "+dateformatterShort.string(from: n.timestampEvent)
            receiveTimelabel.text = "received: "+dateformatterShort.string(from: n.timestampReceived)
            transactionID.text = "transaction ID: "+n.transactionID
            if n.actionRequired.count > 0 {
                actionMessage.isHidden = false
                actionMessage.text = n.actionRequired
                actionMessage.layoutIfNeeded()
                actionMessage.backgroundColor = UIColor(red: 255.0/255.0, green: 126.0/255.0, blue: 121.0/255.0, alpha: 0.5)
            } else {
                actionMessage.isHidden = true
            }
        }
    }

}

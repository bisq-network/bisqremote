//
//  NotificationDetailViewController.swift
//  bisqremote
//
//  Created by Joachim Neumann on 04/06/2018.
//  Copyright © 2018 joachim Neumann. All rights reserved.
//

import UIKit

class NotificationDetailViewController: UIViewController {

    var notification: Notification?
    @IBOutlet weak var textLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM-dd-yyyy hh:mm:ss"
        textLabel.text = "v=\(notification!.version)"
    }

}

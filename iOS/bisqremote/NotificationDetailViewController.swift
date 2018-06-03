//
//  NotificationDetailViewController.swift
//  bisqremote
//
//  Created by Joachim Neumann on 04/06/2018.
//  Copyright © 2018 joachim Neumann. All rights reserved.
//

import UIKit

class NotificationDetailViewController: UIViewController {

    var bisqNotification: BisqNotification?
    @IBOutlet weak var textLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        textLabel.text = bisqNotification?.text
    }

}

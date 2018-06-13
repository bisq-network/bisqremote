//
//  MainViewController.swift
//  
//
//  Created by Joachim Neumann on 03/06/2018.
//  Copyright © 2018 joachim Neumann. All rights reserved.
//

import UIKit

class MainViewController: UIViewController {
    
    @IBOutlet weak var setupButton: UIButton!
    
    @IBOutlet weak var showNotificationsButton: UIButton!
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        let setupDone = UserDefaults.standard.bool(forKey: userDefaultKeySetupDone)
        if setupDone {
            showNotificationsButton.isEnabled = true
        } else {
//            showNotificationsButton.isEnabled = false
        }
    }
    
    @IBAction func helpPressed(_ sender: Any) {
        let x = UIAlertController(title: "SETUP", message: "Start the Bisq desktop app on your computer and open 'Bisq Mobile' in the Menu. Then press 'Read QR code' on your phone to finalize the Setup", preferredStyle: .actionSheet)
        x.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
        self.present(x, animated: true) {}
    }
}


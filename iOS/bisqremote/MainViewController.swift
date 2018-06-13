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
            showNotificationsButton.isEnabled = false
        }
    }
}


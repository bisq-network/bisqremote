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
    @IBOutlet weak var successTextView: UITextView!
    override func viewDidLoad() {
        super.viewDidLoad()
        let setupDone = UserDefaults.standard.bool(forKey: userDefaultKeySetupDone)
        if setupDone {
            setupButton.setTitle("NEW SETUP", for: .normal)
            showNotificationsButton.isHidden = false
            successTextView.isHidden = false
        } else {
            setupButton.setTitle("SETUP", for: .normal)
            showNotificationsButton.isHidden = true
            successTextView.isHidden = true
        }
    }
}


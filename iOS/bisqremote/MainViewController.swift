//
//  MainViewController.swift
//  
//
//  Created by Joachim Neumann on 03/06/2018.
//  Copyright © 2018 joachim Neumann. All rights reserved.
//

import UIKit

class MainViewController: UIViewController {
    
    var notification: String?
    @IBOutlet weak var messageTextField: UITextView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if notification != nil {
            messageTextField.text = notification
        }
    }

}


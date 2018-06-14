//
//  SetupEncryptionViewController.swift
//  bisqremote
//
//  Created by Joachim Neumann on 14/06/2018.
//  Copyright © 2018 joachim Neumann. All rights reserved.
//

import UIKit

class SetupEncryptionViewController: UIViewController {

    @IBOutlet weak var encryptionKeyStatusImage: UIImageView!
    @IBOutlet weak var encryptionKeyStatusLabel: UILabel!
    @IBOutlet weak var nextButton: UIButton!
    @IBOutlet weak var fakeButton: UIButton!
    @IBOutlet weak var constraintAboveImage: NSLayoutConstraint!
    @IBOutlet weak var constraintBelowImage: NSLayoutConstraint!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let h = UIScreen.main.bounds.height
        if h < 600 {
            constraintAboveImage.constant /= 2
            constraintBelowImage.constant /= 2
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        if FAKE_ENCRYPTION_KEY_BUTTON {
            fakeButton.isHidden = false
        } else {
            fakeButton.isHidden = true
        }
        
        if (UserDefaults.standard.string(forKey: userDefaultSymmetricKey) != nil) {
            nextButton.isHidden = false
            encryptionKeyStatusImage.isHidden = false
            encryptionKeyStatusLabel.isHidden = false
        } else {
            nextButton.isHidden = true
            encryptionKeyStatusImage.isHidden = true
            encryptionKeyStatusLabel.isHidden = true
        }
    }
    
    func webPagePressed(alert: UIAlertAction!) {
        if let url = NSURL(string: "https://en.wikipedia.org/wiki/Symmetric-key_algorithm"){
            UIApplication.shared.open(url as URL, options: [:], completionHandler: nil)
        }
    }
    
    @IBAction func fakePressed(_ sender: Any) {
        UserDefaults.standard.set("fake aps token 82763459827364", forKey: userDefaultApsToken)
        UserDefaults.standard.set("fake encryption key 9876324598723", forKey: userDefaultSymmetricKey)
        nextButton.isHidden = false
        encryptionKeyStatusImage.isHidden = false
        encryptionKeyStatusLabel.isHidden = false
    }
    
    @IBAction func helpPressed(_ sender: Any) {
        let x = UIAlertController(title: "Encryption", message: "The notifications are encryped using symmetric encryption. The key is generated in the Bisq desktop app and you need to read it using the  QR code reader.", preferredStyle: .actionSheet)
        x.addAction(UIAlertAction(title: "about symmetric encryption", style: .default, handler: webPagePressed))
        x.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
        self.present(x, animated: true) {}
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        var title = "Setup Encryption"
        if segue.identifier == "scanQRsegue" {
            title = "Cancel"
        }
        navigationItem.backBarButtonItem = UIBarButtonItem(title: title, style: .plain, target: nil, action: nil)
    }
    
}

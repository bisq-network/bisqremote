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
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func viewWillAppear(_ animated: Bool) {
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

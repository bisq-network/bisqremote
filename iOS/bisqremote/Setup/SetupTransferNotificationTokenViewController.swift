//
//  SetupTransferNotificationTokenViewController.swift
//  bisqremote
//
//  Created by Joachim Neumann on 14/06/2018.
//  Copyright © 2018 joachim Neumann. All rights reserved.
//

import UIKit
import MessageUI

class SetupTransferNotificationTokenViewController: UIViewController, MFMailComposeViewControllerDelegate {
    var apsToken: String = "unknown"
    @IBOutlet weak var emailButton: UIButton!
    @IBOutlet weak var rawText: UITextView!
    @IBOutlet weak var qrImage: UIImageView!
    @IBOutlet weak var instructionLabel: UILabel!
    @IBOutlet weak var selectMethodControl: UISegmentedControl!
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let font = UIFont.systemFont(ofSize: 17)
        selectMethodControl.setTitleTextAttributes(
            [NSAttributedStringKey.font: font],
            for: .normal)
        if let t = UserDefaults.standard.string(forKey: userDefaultApsToken) {
            apsToken = t
            qrImage.image = generateQRCode(from: apsToken)
            rawText.text = apsToken
            setMethod(index: 0)
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func generateQRCode(from string: String) -> UIImage? {
        let data = string.data(using: String.Encoding.ascii)
        
        if let filter = CIFilter(name: "CIQRCodeGenerator") {
            filter.setValue(data, forKey: "inputMessage")
            let transform = CGAffineTransform(scaleX: 3, y: 3)
            
            if let output = filter.outputImage?.transformed(by: transform) {
                return UIImage(ciImage: output)
            }
        }
        
        return nil
    }
    
    func setMethod(index: Int) {
        switch index {
        case 0:
            instructionLabel.text = "Use the camera of your computer"
            qrImage.isHidden = false
            emailButton.isHidden = true
            rawText.isHidden = true
        case 1:
            instructionLabel.text = "Email to yourself, then copy&paste"
            qrImage.isHidden = true
            emailButton.isHidden = false
            rawText.isHidden = true
        case 2:
            instructionLabel.text = "Type this into the Bisq desktop app:"
            qrImage.isHidden = true
            emailButton.isHidden = true
            rawText.isHidden = false
        default:
            print("wrong segmentIndex")
        }
    }
    @IBAction func methodChanged(_ method: UISegmentedControl) {
        setMethod(index: method.selectedSegmentIndex)
    }
    
    func mailComposeController(_ controller: MFMailComposeViewController, didFinishWith result: MFMailComposeResult, error: Error?) {
        controller.dismiss(animated: true)
    }
    
    
    @IBAction func emailButtonPressed(_ sender: Any) {
        if MFMailComposeViewController.canSendMail() {
            let mail = MFMailComposeViewController()
            mail.mailComposeDelegate = self
            mail.setSubject("Apple notification service token")
            mail.setMessageBody("\(apsToken)", isHTML: false)
            
            present(mail, animated: true)
        } else {
            // show failure alert
        }
    }
    
    @IBAction func doneButtonPressed(_ sender: Any) {
        UserDefaults.standard.set(true, forKey: userDefaultKeySetupDone)
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let vc = storyboard.instantiateViewController(withIdentifier: "listScreen") as! NotificationTableViewController
        navigationController?.setViewControllers([vc], animated: true)
    }
    
    func webPagePressed(alert: UIAlertAction!) {
        if let url = NSURL(string: "https://developer.apple.com/documentation/usernotifications/registering_your_app_with_apns"){
            UIApplication.shared.open(url as URL, options: [:], completionHandler: nil)
        }
    }
    
    @IBAction func helpPressed(_ sender: Any) {
        let x = UIAlertController(title: "iOS Notification Service", message: "This app can receive iOS notifications from Apple, whihc are triggered by the Bisq desktop app. In order to identify your phone to the Bisq desktop app, the Bisq desktop app needs to know the notification token, wich the mobile app has already received from Apple.", preferredStyle: .actionSheet)
        x.addAction(UIAlertAction(title: "About the Apple notificaiton token", style: .default, handler: webPagePressed))
        x.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
        self.present(x, animated: true) {}
    }
    
}

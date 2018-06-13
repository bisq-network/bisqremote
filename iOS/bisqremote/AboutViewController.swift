//
//  AboutViewController.swift
//  bisqremote
//
//  Created by Joachim Neumann on 12/06/2018.
//  Copyright © 2018 joachim Neumann. All rights reserved.
//

import UIKit
import MessageUI

class AboutViewController: UIViewController, MFMailComposeViewControllerDelegate {
    
    var apsToken: String = "unknown"
    
    @IBOutlet weak var qrcodeImageView: UIImageView!
    @IBOutlet weak var apsTokenLabel: UILabel!
    @IBOutlet weak var symmetricKeyTitle: UILabel!
    @IBOutlet weak var symmetricKeyLabel: UILabel!
    @IBOutlet weak var hashTitle: UILabel!
    @IBOutlet weak var hashLabel: UILabel!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        apsToken = appDelegate.apsToken
        apsTokenLabel.text = apsToken.trunc(length: 6, trailing:"...")
        if let key = UserDefaults.standard.string(forKey: userDefaultSymmetricKey) {
            symmetricKeyLabel.text = key.trunc(length: 6, trailing:"...")
        }
        if let hash =  UserDefaults.standard.string(forKey: userDefaultKeyHash) {
            hashLabel.text = hash.trunc(length: 6, trailing:"...")
        }
        qrcodeImageView.isHidden = true
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func sendEmail(_ sender: Any) {
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
    
    func mailComposeController(_ controller: MFMailComposeViewController, didFinishWith result: MFMailComposeResult, error: Error?) {
        controller.dismiss(animated: true)
    }
    
    
    func generateQRCode(from string: String) -> UIImage? {
        let data = string.data(using: String.Encoding.ascii)
        
        if let filter = CIFilter(name: "CIQRCodeGenerator") {
            filter.setValue(data, forKey: "inputMessage")
            let transform = CGAffineTransform(scaleX: 3, y: 5)
            
            if let output = filter.outputImage?.transformed(by: transform) {
                return UIImage(ciImage: output)
            }
        }
        
        return nil
    }
    
    
    @IBAction func createQRcode(_ sender: Any) {
        if qrcodeImageView.isHidden {
            qrcodeImageView.image = generateQRCode(from: apsToken)
            qrcodeImageView.isHidden = false
            symmetricKeyTitle.isHidden = true
            symmetricKeyLabel.isHidden = true
            hashTitle.isHidden = true
            hashLabel.isHidden = true
        } else {
            qrcodeImageView.isHidden = true
            symmetricKeyTitle.isHidden = false
            symmetricKeyLabel.isHidden = false
            hashTitle.isHidden = false
            hashLabel.isHidden = false
        }
    }
    
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}

extension String {
    /*
     Truncates the string to the specified length number of characters and appends an optional trailing string if longer.
     - Parameter length: Desired maximum lengths of a string
     - Parameter trailing: A 'String' that will be appended after the truncation.
     
     - Returns: 'String' object.
     */
    func trunc(length: Int, trailing: String = "…") -> String {
        return (self.count > length) ? self.prefix(length) + trailing : self
    }
}

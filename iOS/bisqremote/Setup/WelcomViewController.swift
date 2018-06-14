//
//  WelcomViewController.swift
//  bisqremote
//
//  Created by Joachim Neumann on 14/06/2018.
//  Copyright © 2018 joachim Neumann. All rights reserved.
//

import UIKit

class WelcomViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func webPagePressed(alert: UIAlertAction!) {
        if let url = NSURL(string: "https://bisq.network"){
            UIApplication.shared.open(url as URL, options: [:], completionHandler: nil)
        }
    }

    @IBAction func helpPressed(_ sender: Any) {
        let x = UIAlertController(title: "Bisq", message: "Bisq is an open-source desktop application that allows you to buy and sell bitcoins in exchange for national currencies, or alternative crypto currencies.", preferredStyle: .actionSheet)
        x.addAction(UIAlertAction(title: "https://bisq.network", style: .default, handler: webPagePressed))
        x.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
        self.present(x, animated: true) {}
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

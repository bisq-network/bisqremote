//
/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

import UIKit

class NotificationDetailViewController: UIViewController {
    let dateformatterShort = DateFormatter()

    var notification: Notification?
    @IBOutlet weak var textLabel: UILabel!
    @IBOutlet weak var eventTimeLabel: UILabel!
    @IBOutlet weak var receiveTimelabel: UILabel!
    @IBOutlet weak var actionMessage: UITextView!
    @IBOutlet weak var transactionID: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        actionMessage.layer.cornerRadius = 10
        dateformatterShort.dateFormat = "yyyy-MM-dd HH:mm"
        if let n = notification {
            textLabel.text = n.notificationType
            eventTimeLabel.text   = "event:    "+dateformatterShort.string(from: n.timestampEvent)
            receiveTimelabel.text = "received: "+dateformatterShort.string(from: n.timestampReceived)
            transactionID.text = "transaction ID: "+n.transactionID
            if n.actionRequired.count > 0 {
                actionMessage.isHidden = false
                actionMessage.text = n.actionRequired
                actionMessage.layoutIfNeeded()
                actionMessage.backgroundColor = UIColor(red: 255.0/255.0, green: 126.0/255.0, blue: 121.0/255.0, alpha: 0.5)
            } else {
                actionMessage.isHidden = true
            }
        }
    }

}

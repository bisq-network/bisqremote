//
//  BisqNotifications.swift
//  bisqremote
//
//  Created by Joachim Neumann on 04/06/2018.
//  Copyright © 2018 joachim Neumann. All rights reserved.
//

import Foundation
import UIKit // for setting the badge number

let userDefaultKey = "bisqNotification"

let TYPE_TRADE_ACCEPTED = "TRADE_ACCEPTED"

class RawNotification: Codable {
    let version: Int
    let notificationType: String
    let comment: String
    let timestampEvent: Date

    init(version_: Int, notificationType_: String, comment_: String, timestampEvent_: Date) {
        version = version_
        notificationType = notificationType_
        comment = comment_
        timestampEvent = timestampEvent_
    }
    
    private enum CodingKeys: String, CodingKey {
        case version
        case notificationType
        case comment
        case timestampEvent
    }
    
    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        version = try container.decode(Int.self, forKey: .version)
        let notificationTypeCandidate = try container.decode(String.self, forKey: .notificationType)
        switch notificationTypeCandidate {
        case TYPE_TRADE_ACCEPTED:
            notificationType = TYPE_TRADE_ACCEPTED
        default:
            fatalError("wrong notificationType \(notificationTypeCandidate)")
        }
        comment = try container.decode(String.self, forKey: .comment)
        timestampEvent = try container.decode(Date.self, forKey: .timestampEvent)
    }
    
    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(version, forKey: .version)
        try container.encode(notificationType, forKey: .notificationType)
        try container.encode(comment, forKey: .comment)
        try container.encode(timestampEvent, forKey: .timestampEvent)
    }
    
}

class Notification: RawNotification {
    var read: Bool
    let timestampReceived: Date

    private enum CodingKeys: String, CodingKey {
        case read
        case timestampReceived
    }
    
    override init(version_: Int, notificationType_: String, comment_: String, timestampEvent_: Date) {
        read = false
        timestampReceived = Date()
        super.init(version_: version_, notificationType_: notificationType_, comment_: comment_, timestampEvent_: timestampEvent_)
    }
    
    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        let superdecoder = try container.superDecoder()
        read = try container.decode(Bool.self, forKey: .read)
        timestampReceived = try container.decode(Date.self, forKey: .timestampReceived)
        try super.init(from: superdecoder)
    }
    
    override func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(read, forKey: .read)
        try container.encode(timestampReceived, forKey: .timestampReceived)

        let superdecoder = container.superEncoder()
        try super.encode(to: superdecoder)
    }
}

class BisqNotifications {

    static let shared = BisqNotifications()
    
    let dateformatterShort = DateFormatter()
    private let dateformatterLong = DateFormatter()
    private var array: [Notification] = [Notification]()
    private let decoder = JSONDecoder()
    private let encoder = JSONEncoder()
    private init() {
        // set date format to the javascript standard
        dateformatterLong.dateFormat = "yyyy-MM-dd HH:mm:ss"
        dateformatterShort.dateFormat = "yyyy-MM-dd HH:mm"
        decoder.dateDecodingStrategy = .formatted(dateformatterLong)
        encoder.dateEncodingStrategy = .formatted(dateformatterLong)
        encoder.outputFormatting = .prettyPrinted

        load()
    }

    private struct APS : Codable {
        let alert: String
        let sound: String
        let bisqNotification: RawNotification
    }

    static func exampleAPS() -> String {
        // Normally, the badge number is managed on the server.
        // In our use case, the server (=bisq notification node) should have as
        // little knowledge as possible. Therefore, the badge is incremented
        // in the app.
        // One drawback is that the badge number is not immediately updated
        // when a notification arrives on the phone
        let aps = APS(
            alert: "Bisq Notification",
            sound: "default",
            bisqNotification: exampleNotification())
        let completeMessage = ["aps": aps]
        do {
            let jsonData = try BisqNotifications.shared.encoder.encode(completeMessage)
            return String(data: jsonData, encoding: .utf8)!
        } catch {
            return("could not create example APS")
        }
    }

    static func exampleNotification() -> RawNotification {
        return RawNotification(version_: 1, notificationType_: TYPE_TRADE_ACCEPTED, comment_: "no comment", timestampEvent_: Date())
    }
    
    func parseArray(json: String) {
        do {
            let data: Data? = json.data(using: .utf8)
            array = try decoder.decode([Notification].self, from: data!)
        } catch {
            array = [Notification]()
        }
    }

    func parse(json: String) -> Notification? {
        var ret: Notification?
        do {
            // add timestamp of reception
            let withReceptionTimestamp = json.replacingOccurrences(of: "}", with: ", \"timestampReceived\": \""+dateformatterLong.string(from: Date())+"\"}")
            let data: Data? = withReceptionTimestamp.data(using: .utf8)
            ret = try decoder.decode(Notification.self, from: data!)
        } catch {
            ret = nil
        }
        return ret
    }

    func save() {
        do {
            let jsonData = try encoder.encode(array)
            let toDefaults = String(data: jsonData, encoding: .utf8)!
            UserDefaults.standard.set(toDefaults, forKey: userDefaultKey)
            UIApplication.shared.applicationIconBadgeNumber = countUnread
        } catch {
            print("/n###/n### save failed/n###/n")
        }
    }
    
    private func load() {
        let fromDefaults = UserDefaults.standard.string(forKey: userDefaultKey) ?? "[]"
        parseArray(json: fromDefaults)
        UIApplication.shared.applicationIconBadgeNumber = countUnread
    }
    
    var countAll: Int {
        return array.count
    }
    
    var countUnread: Int {
        var unread = 0
        for n in array {
            if (!n.read) { unread += 1 }
        }
        return unread
    }

    func at(n: Int) -> Notification {
        let x = array[n]
        return x
    }
    
    
    func addFromJSON(new: AnyObject?) {
        do {
            let jsonData = try JSONSerialization.data(withJSONObject: new!)
            let raw = try decoder.decode(RawNotification.self, from: jsonData)
            addRaw(raw: raw)
        } catch {
            print("could not add notification")
        }
    }

    func addRaw(raw: RawNotification) {
        addNotification(n: Notification(version_: raw.version, notificationType_: raw.notificationType, comment_: raw.comment, timestampEvent_: raw.timestampEvent))
    }

    func addNotification(n: Notification) {
        array.append(n)
        save()
    }
    func remove(n: Int) {
        array.remove(at: n)
        save()
    }
}


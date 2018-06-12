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

let TYPE_TRADE_ACCEPTED = 0

class RawNotification: Codable {
    let version: Int
    let notificationType: Int // TODO convert this to an enum
    let timestampEvent: Date

    init(v: Int, t: Date) {
        version = v
        notificationType = TYPE_TRADE_ACCEPTED
        timestampEvent = t
    }
    
    private enum CodingKeys: String, CodingKey {
        case version
        case timestampEvent
        case notificationType
    }
    
    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        version = try container.decode(Int.self, forKey: .version)
        timestampEvent = try container.decode(Date.self, forKey: .timestampEvent)
        notificationType = try container.decode(Int.self, forKey: .notificationType)
    }
    
    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(version, forKey: .version)
        try container.encode(timestampEvent, forKey: .timestampEvent)
        try container.encode(notificationType, forKey: .notificationType)
    }
    
}

class Notification: RawNotification {
    var read: Bool
    let timestampReceived: Date

    private enum CodingKeys: String, CodingKey {
        case read
        case timestampReceived
    }
    
    override init(v: Int, t: Date) {
        read = false
        timestampReceived = Date()
        super.init(v: v, t: t)
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
    
    private var array: [Notification] = [Notification]()
    private let decoder = JSONDecoder()
    private let encoder = JSONEncoder()
    private let dateformatter = DateFormatter()
    private init() {
        // set date format to the javascript standard
        dateformatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        decoder.dateDecodingStrategy = .formatted(dateformatter)
        encoder.dateEncodingStrategy = .formatted(dateformatter)
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
        return RawNotification(v: 1, t: Date())
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
            let withReceptionTimestamp = json.replacingOccurrences(of: "}", with: ", \"timestampReceived\": \""+dateformatter.string(from: Date())+"\"}")
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
    
    
    func addRaw(new: AnyObject?) {
        do {
            let jsonData = try JSONSerialization.data(withJSONObject: new!)
            let raw = try decoder.decode(RawNotification.self, from: jsonData)
            array.append(Notification(v: raw.version, t: raw.timestampEvent))
            save()
        } catch {
            print("could not add notification")
        }
    }
    
    func remove(n: Int) {
        array.remove(at: n)
        save()
    }
}


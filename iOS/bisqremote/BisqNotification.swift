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

struct ANotification: Codable, Equatable {
    var version: Int
    var message: String
    var timestampEvent: Date
    var read: Bool
//    var timestampReceived: Date
    
    static func == (lhs: ANotification, rhs: ANotification) -> Bool {
        let df = DateFormatter()
        df.dateFormat = "yyyy-MM-dd HH:mm"
        return
            lhs.version == rhs.version &&
            lhs.message == rhs.message &&
            df.string(from: lhs.timestampEvent) == df.string(from: rhs.timestampEvent)
    }
}

class TimestampedNotification: Codable {
    var timestampReceived: Date
    var aNotification: ANotification
    init(n: ANotification) {
        aNotification = n
        timestampReceived = Date()
    }
}

class BisqNotifications {

    static let shared = BisqNotifications()
    
    private var array: [TimestampedNotification] = [TimestampedNotification]()
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

    private func test() {
        let df = DateFormatter()
        df.dateFormat = "yyyy-MM-dd HH:mm:ss:SSSZ"
            
        let jd = JSONDecoder()
        let je = JSONEncoder()
        jd.dateDecodingStrategy = .formatted(df)
        je.dateEncodingStrategy = .formatted(df)
        do {
            let n1 = [BisqNotifications.exampleNotification()]
            let nd1 = try je.encode(n1)
            let ns1 = String(data: nd1, encoding: .utf8)!
            
            let nd2: Data? = ns1.data(using: .utf8)
            let n2 = try jd.decode([ANotification].self, from: nd2!)
            
            let nd22 = try je.encode(n2)
            let ns2 = String(data: nd22, encoding: .utf8)!
            assert(n1 == n2)
            assert(n1[0] == n2[0])
            assert(ns1 == ns2)

            let t1 = n1[0].timestampEvent
            let t2 = n2[0].timestampEvent
            let dateS1 = df.string(from: t1)
            let dateS2 = df.string(from: t2)
            assert(dateS1 == dateS2)
            let tx1 = df.date(from: dateS1)
            let tx2 = df.date(from: dateS2)
            print (t1>t2)
            print (t1<t2)
            assert(tx1 == tx2)
            assert(t1 == t2)
        } catch let jsonErr {
            print("/n###/n### save failed/n###/n", jsonErr)
        }
    }
    private struct APS : Codable {
        let alert: String
        let sound: String
        let bisqNotification: ANotification
    }

    static func exampleAPS() -> String {
        // normally, the badge number os managed on the server.
        // In this app, the bisq notification node should have as little knowledge as possible
        // therefore, the badge is incremented in the app
        // One drawback is that the badge number is not immediately incremented
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

    static func exampleNotification() -> ANotification {
        return ANotification(
            version: 1,
            message: "example notification",
            timestampEvent: Date(),
            read: false)
    }
    
    func parseArray(json: String) {
        do {
            let data: Data? = json.data(using: .utf8)
            array = try decoder.decode([TimestampedNotification].self, from: data!)
        } catch {
            array = [TimestampedNotification]()
        }
    }

    func parse(json: String) -> ANotification? {
        var ret: ANotification?
        do {
            // add timestamp of reception
            let withReceptionTimestamp = json.replacingOccurrences(of: "}", with: ", \"timestampReceived\": \""+dateformatter.string(from: Date())+"\"}")
            let data: Data? = withReceptionTimestamp.data(using: .utf8)
            ret = try decoder.decode(ANotification.self, from: data!)
        } catch {
            ret = nil
        }
        return ret
    }

    private func save() {
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
            if (!n.aNotification.read) { unread += 1 }
        }
        return unread
    }

    func at(n: Int) -> TimestampedNotification {
        return array[n]
    }
    
    
    func add(new: ANotification) {
        let temp = TimestampedNotification(n: new)
        array.append(temp)
        save()
    }
    
    func add(new: AnyObject?) {
        do {
            let jsonData = try JSONSerialization.data(withJSONObject: new!)
            add(new: try decoder.decode(ANotification.self, from: jsonData))
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


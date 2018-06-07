//
//  BisqNotifications.swift
//  bisqremote
//
//  Created by Joachim Neumann on 04/06/2018.
//  Copyright © 2018 joachim Neumann. All rights reserved.
//


//example message:
//{"aps":{"bisqNotificationVersion": 1, "alert":"Testing.. (60)","badge":1,"sound":"default", "bisqMessage": "TRADE_ACCEPTED", "timestampEvent": "2018-06-06 21:52:50"}}
import Foundation

let userDefaultKey = "bisqNotification"

struct ANotification: Codable {
    var version: Int
    var message: String
    var timestampEvent: Date
//    var timestampReceived: Date
}

class BisqNotifications {

    static let shared = BisqNotifications()
    private var array: [ANotification] = [ANotification]()
    private let decoder = JSONDecoder()
    let encoder = JSONEncoder()
    private let dateformatter = DateFormatter()
    private init() {
        load()
    
        // set date format to the javascript standard
        dateformatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        decoder.dateDecodingStrategy = .formatted(dateformatter)
        encoder.dateEncodingStrategy = .formatted(dateformatter)
    }

    struct APS : Codable {
        let alert: String
        let badge: Int
        let sound: String
        let bisqNotification: ANotification
    }

    static func exampleAPS() -> APS {
        let aps = APS(
            alert: "Bisq Notification",
            badge: 1,
            sound: "default",
            bisqNotification: exampleNotification())
        return aps
    }

    static func exampleNotification() -> ANotification {
        return ANotification(
            version: 1,
            message: "example notification",
            timestampEvent: Date())
    }
    
    func parseArray(json: String) {
        do {
            let data: Data? = json.data(using: .utf8)
            array = try decoder.decode([ANotification].self, from: data!)
        } catch {
            array = [ANotification]()
        }
    }

    func parse(json: String) -> ANotification? {
        var ret: ANotification?
        do {
            let withReceivedDate = json.replacingOccurrences(of: "}", with: ", \"timestampReceived\": \""+dateformatter.string(from: Date())+"\"}")
            let data: Data? = withReceivedDate.data(using: .utf8)
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
        } catch {
            print("/n###/n### save failed/n###/n")
        }
    }
    
    private func load() {
        let fromDefaults = UserDefaults.standard.string(forKey: userDefaultKey) ?? "[]"
        parseArray(json: fromDefaults)
    }
    
    var count: Int {
        return array.count
    }
    
    func at(n: Int) -> ANotification {
        return array[n]
    }
    
    
    func add(new: ANotification) {
        array.append(new)
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


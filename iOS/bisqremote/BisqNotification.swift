//
//  BisqNotifications.swift
//  bisqremote
//
//  Created by Joachim Neumann on 04/06/2018.
//  Copyright © 2018 joachim Neumann. All rights reserved.
//

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
    static private let decoder = JSONDecoder()
    static private let encoder = JSONEncoder()
    private let dateformatter = DateFormatter()
    private init() {
        load()
        
        // set date format to the javascript standard
        dateformatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        BisqNotifications.decoder.dateDecodingStrategy = .formatted(dateformatter)
        BisqNotifications.encoder.dateEncodingStrategy = .formatted(dateformatter)
        BisqNotifications.encoder.outputFormatting = .prettyPrinted

    }

    private struct APS : Codable {
        let alert: String
        let badge: Int
        let sound: String
        let bisqNotification: ANotification
    }

    static func exampleAPS() -> String {
        let aps = APS(
            alert: "Bisq Notification",
            badge: 1,
            sound: "default",
            bisqNotification: exampleNotification())
        let completeMessage = ["aps": aps]
        do {
            let jsonData = try encoder.encode(completeMessage)
            return String(data: jsonData, encoding: .utf8)!
        } catch {
            return("could not create example APS")
        }
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
            array = try BisqNotifications.decoder.decode([ANotification].self, from: data!)
        } catch {
            array = [ANotification]()
        }
    }

    func parse(json: String) -> ANotification? {
        var ret: ANotification?
        do {
            let withReceivedDate = json.replacingOccurrences(of: "}", with: ", \"timestampReceived\": \""+dateformatter.string(from: Date())+"\"}")
            let data: Data? = withReceivedDate.data(using: .utf8)
            ret = try BisqNotifications.decoder.decode(ANotification.self, from: data!)
        } catch {
            ret = nil
        }
        return ret
    }

    private func save() {
        do {
            let jsonData = try BisqNotifications.encoder.encode(array)
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
            add(new: try BisqNotifications.decoder.decode(ANotification.self, from: jsonData))
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


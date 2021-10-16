import Foundation

@objc public class SunmiNfc: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}

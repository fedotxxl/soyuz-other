package io.belov.soyuz.ua

import io.belov.soyuz.ua.Browser
import io.belov.soyuz.ua.BrowserType
import io.belov.soyuz.ua.DeviceCategory
import io.belov.soyuz.ua.OS
import io.belov.soyuz.ua.OSFamily
import io.belov.soyuz.ua.ParsedUserAgent
import io.belov.soyuz.ua.UserAgentParser
import spock.lang.Specification

/**
 * Created by fbelov on 25.04.15.
 */
class UserAgentParserSpec extends Specification {

    UserAgentParser userAgentParser = new UserAgentParser()

    def "should parse ua"() {
        expect:
        ParsedUserAgent parsedUserAgent = userAgentParser.parseUserAgent(userAgent)

        assert parsedUserAgent.userAgent == userAgent
        assert parsedUserAgent.device == device
        assert parsedUserAgent.browser?.id
        assert parsedUserAgent.os?.id
        assert parsedUserAgent.browser.type == browser.type
        assert parsedUserAgent.browser.version == browser.version
        assert parsedUserAgent.os.family == os.family
        assert parsedUserAgent.os.version == os.version

        where:
        userAgent                                                                                                                                              | browser                                                  | os                                        | device
        "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_2 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8H7 Safari/6533.18.5" | b("Mobile Safari 5.0.2", BrowserType.MOBILE_SAFARI, "5") | o("iOS 4", OSFamily.IOS, "4")             | DeviceCategory.SMARTPHONE
        "Mozilla/5.0 (iPad; CPU OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25"                       | b("Mobile Safari 6.0", BrowserType.MOBILE_SAFARI, "6")   | o("iOS 6", OSFamily.IOS, "6")             | DeviceCategory.TABLET
        "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)"                                                                                                   | b("IE 7.0", BrowserType.IE, "7")                         | o("Windows Vista", OSFamily.WINDOWS, "6") | DeviceCategory.PERSONAL_COMPUTER
    }

    private static Browser b(String fullName, BrowserType type, String ver) {
        return Browser.myValueOf(type, ver)
    }

    private static OS o(String fullName, OSFamily family, String ver) {
        return OS.myValueOf(family, ver)
    }
}



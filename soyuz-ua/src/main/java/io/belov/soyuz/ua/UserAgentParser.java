package io.belov.soyuz.ua;

import net.sf.uadetector.OperatingSystem;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

/**
 * Created by fbelov on 25.04.15.
 */
public class UserAgentParser {

    private final UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();

    public ParsedUserAgent parseUserAgent(String userAgentString) {
        ReadableUserAgent userAgent = getReadableUserAgent(userAgentString);
        DeviceCategory category = getCategory(userAgent);
        Browser browser = getBrowserFromUserAgent(userAgent);
        OS os = getOsFromUserAgent(userAgent);

        return new ParsedUserAgent(category, userAgentString, browser, os);
    }

    private Browser getBrowserFromUserAgent(ReadableUserAgent userAgent) {
        BrowserType type = getBrowserType(userAgent);
        String ver = userAgent.getVersionNumber().getMajor();

        return Browser.myValueOf(type, ver);
    }

    private OS getOsFromUserAgent(ReadableUserAgent userAgent) {
        OperatingSystem os = userAgent.getOperatingSystem();
        OSFamily family = getOsFamily(os);
        String ver = os.getVersionNumber().getMajor();

        return OS.myValueOf(family, ver);
    }

    private ReadableUserAgent getReadableUserAgent(String userAgentString) {
        return parser.parse(userAgentString);
    }

    private OSFamily getOsFamily(OperatingSystem os) {
        return OSFamily.myValueOf(os.getFamily().toString());
    }

    private BrowserType getBrowserType(ReadableUserAgent userAgent) {
        return BrowserType.myValueOf(userAgent.getFamily().toString());
    }

    private DeviceCategory getCategory(ReadableUserAgent userAgent) {
        return DeviceCategory.myValueOf(userAgent.getDeviceCategory().getCategory().toString());
    }

}

/*
 * ParsedUserAgent
 * Copyright (c) 2012 Cybervision. All rights reserved.
 */
package io.belov.soyuz.ua;

public class ParsedUserAgent {

    private DeviceCategory device;
    private String userAgent;
    private Browser browser;
    private OS os;

    public ParsedUserAgent(DeviceCategory category, String userAgent, Browser browser, OS os) {
        this.device = category;
        this.userAgent = userAgent;
        this.browser = browser;
        this.os = os;
    }

    public DeviceCategory getDevice() {
        return device;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public Browser getBrowser() {
        return browser;
    }

    public OS getOs() {
        return os;
    }
}

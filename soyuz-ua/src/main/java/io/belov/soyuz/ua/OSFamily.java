/*
 * ExceptionStatus
 * Copyright (c) 2012 Cybervision. All rights reserved.
 */
package io.belov.soyuz.ua;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.belov.soyuz.mongo.JongoEnum;

public enum OSFamily implements JongoEnum {

    ANDROID("Android", "a"), BLACKBERRY_OS("BlackBerry OS", "b"), FIREFOX_OS("Firefox OS", "ff"),
    IOS("iOS", "ios"), LINUX("Linux", "l"), MAC_OS("Mac OS", "mac"),
    OS_X("OS X", "osx"), WINDOWS("Windows", "win"), OTHER("Other", "o");

    private String name;
    private String stored;

    OSFamily(String name, String stored) {
        this.name = name;
        this.stored = stored;
    }

    public String getName() {
        return name;
    }

    @JsonValue
    public String toStored() {
        return stored;
    }

    @JsonCreator
    public static OSFamily myValueOf(String value) {
        for (OSFamily os : OSFamily.values()) {
            if (os.stored.equals(value) || os.toString().equals(value) || os.name.equals(value)) return os;
        }

        return OTHER;
    }
}

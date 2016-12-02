/*
 * ExceptionStatus
 * Copyright (c) 2012 Cybervision. All rights reserved.
 */
package io.belov.soyuz.ua;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.belov.soyuz.mongo.JongoEnum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public enum BrowserType implements JongoEnum {

    ANDROID_WEBKIT("Android Webkit", "aw"), CHROME("Chrome", "c"),  CHROME_MOBILE("Chrome Mobile", "cm"),
    CHROMIUM("Chromium", "ch"), DOLPHIN("Dolphin", "d"), FIREFOX("Firefox", "ff"),
    IE("IE", "ie"), MOBILE_FIREFOX("Mobile Firefox", "ffm"), MOBILE_SAFARI("Mobile Safari", "sam"),
    OPERA("Opera", "op"), OPERA_MOBILE("Opera Mobile", "opm"), OPERA_MINI("Opera Mini", "opmi"),
    SAFARI("Safari", "sa"), OTHER("Other", "o");

    private String name;
    private String stored;

    BrowserType(String name, String stored) {
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
    public static BrowserType myValueOf(String value) {
        for (BrowserType type : BrowserType.values()) {
            if (type.name.equals(value) || type.toString().equals(value) || type.stored.equals(value)) return type;
        }

        return OTHER;
    }

    public static List<BrowserType> myValueOf(Collection<String> ids) {
        List<BrowserType> answer = new ArrayList<>();

        for (String id : ids) {
            answer.add(myValueOf(id));
        }

        return answer;
    }
}

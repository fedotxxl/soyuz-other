/*
 * DeviceCategory
 * Copyright (c) 2012 Cybervision. All rights reserved.
 */
package io.belov.soyuz.ua;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.belov.soyuz.mongo.JongoEnum;

public enum DeviceCategory implements JongoEnum {

    TABLET("Tablet", "t"), SMARTPHONE("Smartphone", "s"), PERSONAL_COMPUTER("Personal computer", "pc"), OTHER("Other", "o");

    private String name;
    private String stored;

    DeviceCategory(String name, String stored) {
        this.name = name;
        this.stored = stored;
    }

    @JsonCreator
    public static DeviceCategory myValueOf(String value) {
        for (DeviceCategory category : DeviceCategory.values()) {
            if (category.stored.equals(value) || category.toString().equals(value) || category.name.equals(value)) return category;
        }

        return OTHER;
    }

    @JsonValue
    public String toStored() {
        return this.stored;
    }
}

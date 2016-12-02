/*
 * Browser
 * Copyright (c) 2012 Cybervision. All rights reserved.
 */
package io.belov.soyuz.ua;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class Browser {

    @JsonIgnore
    private String id;

    @JsonProperty("t")
    private BrowserType type;

    @JsonProperty("v")
    private String version;

    private Browser() {
    }

    private Browser(BrowserType type, String version) {
        this.type = type;
        this.version = version;
    }

    public static Browser myValueOf(BrowserType type, String version) {
        return new Browser(type, version);
    }

    public String getId() {
        if (id == null) {
            Preconditions.checkNotNull(type);
            Preconditions.checkNotNull(version);

            id = type.toString() + "_" + version;
        }

        return id;
    }

    public BrowserType getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }
}

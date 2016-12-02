/*
 * OS
 * Copyright (c) 2012 Cybervision. All rights reserved.
 */
package io.belov.soyuz.ua;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class OS {

    @JsonIgnore
    private String id;

    @JsonProperty("f")
    private OSFamily family;

    @JsonProperty("v")
    private String version;

    private OS(OSFamily family, String version) {
        this.family = family;
        this.version = version;
    }

    public static OS myValueOf(OSFamily family, String version) {
        return new OS(family, version);
    }

    public String getId() {
        if (id == null) {
            Preconditions.checkNotNull(family);
            Preconditions.checkNotNull(version);

            id = family.toString() + "_" + version;
        }

        return id;
    }

    public OSFamily getFamily() {
        return family;
    }

    public String getVersion() {
        return version;
    }
}

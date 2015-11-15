package io.belov.soyuz.queue;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by fbelov on 03.06.15.
 */
public enum ActionStatus {

    SUCCESS(1), FAILURE(-1);

    private int id;

    ActionStatus(int id) {
        this.id = id;
    }

    @JsonValue
    public int getId() {
        return id;
    }

    @JsonCreator
    static ActionStatus myValueOf(int value) {
        for (ActionStatus status : values()) {
            if (status.id == value) return status;
        }

        return null;
    }
}

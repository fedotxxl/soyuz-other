package io.belov.soyuz.text;

import lombok.Value;

import java.io.File;
import java.util.Set;

/**
 * Created by fbelov on 04.11.15.
 */
public interface TextProcessor {

    boolean isSupported(File file);

    Result process(String content);

    @Value
    class Result {
        private Status status;
        private String html;
        private String title;
        private Set<String> tags;

        public boolean isSuccess() {
            return status == Status.SUCCESS;
        }
    }

    enum Status {
        SUCCESS, FAILURE
    }
}

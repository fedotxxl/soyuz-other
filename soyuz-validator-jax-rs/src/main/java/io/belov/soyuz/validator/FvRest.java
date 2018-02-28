package io.belov.soyuz.validator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fbelov on 14.11.16.
 */
//todo remove it!
public class FvRest {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class ErrorsResponse {
        private Errors errors;

        public ErrorsResponse(List<String> common, Map<String, String> properties) {
            this.errors = new Errors(common, properties);
        }

        @JsonIgnore
        public List<String> getCommon() {
            if (errors != null && errors.hasCommon()) {
                return errors.getCommon();
            } else {
                return new ArrayList<>();
            }
        }

        @JsonIgnore
        public Map<String, String> getProperties() {
            if (errors != null && errors.hasProperties()) {
                return errors.getProperties();
            } else {
                return new HashMap<>();
            }
        }

        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        @EqualsAndHashCode
        @ToString
        public static class Errors {
            private List<String> common;
            private Map<String, String> properties;

            public boolean hasCommon() {
                return common != null;
            }

            public boolean hasProperties() {
                return properties != null;
            }
        }
    }

}

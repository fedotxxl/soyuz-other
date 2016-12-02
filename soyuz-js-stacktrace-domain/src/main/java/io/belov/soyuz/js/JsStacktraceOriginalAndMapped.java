package io.belov.soyuz.js;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by fbelov on 14.06.15.
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class JsStacktraceOriginalAndMapped {

    @JsonProperty("o")
    private JsStacktrace original;
    @JsonProperty("m")
    private JsStacktrace mapped;

    public boolean hasMapped() {
        return mapped != null;
    }

    public JsStacktrace getOriginal() {
        return original;
    }

    public JsStacktrace getMapped() {
        return mapped;
    }
}

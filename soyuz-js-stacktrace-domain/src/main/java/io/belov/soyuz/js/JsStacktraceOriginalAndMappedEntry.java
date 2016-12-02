package io.belov.soyuz.js;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by fbelov on 11.10.15.
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class JsStacktraceOriginalAndMappedEntry {

    @JsonProperty("o")
    private JsStacktraceEntry original;
    @JsonProperty("m")
    private JsStacktraceEntry mapped;

}

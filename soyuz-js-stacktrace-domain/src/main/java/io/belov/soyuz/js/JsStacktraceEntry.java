package io.belov.soyuz.js;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by fbelov on 20.04.15.
 */
@EqualsAndHashCode
@ToString
public class JsStacktraceEntry {

    @JsonProperty("l")
    private int line = -1;
    @JsonProperty("m")
    private String method;
    @JsonProperty("f")
    private String file;
    @JsonProperty("c")
    private int column = -1;
    @JsonProperty("s")
    private String source;

    public JsStacktraceEntry() {
    }

    public JsStacktraceEntry(String source) {
        this.source = source;
    }

    public JsStacktraceEntry(String source, String method, String file, int line) {
        this.source = source;
        this.method = method;
        this.file = file;
        this.line = line;
    }

    public JsStacktraceEntry(String source, String method, String file, int line, int column) {
        this.source = source;
        this.method = method;
        this.file = file;
        this.line = line;
        this.column = column;
    }

    public boolean hasFile() {
        return StringUtils.isNotEmpty(file);
    }

    public boolean hasLineAndColumn() {
        return line > 0 && column > 0;
    }

    public int getLine() {
        return line;
    }

    public String getMethod() {
        return method;
    }

    public String getFile() {
        return file;
    }

    public int getColumn() {
        return column;
    }

    public String getSource() {
        return source;
    }
}

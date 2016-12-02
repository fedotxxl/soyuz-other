package io.belov.soyuz.js;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by fbelov on 07.06.15.
 */
public class JsStacktrace extends ArrayList<JsStacktraceEntry> {

    public JsStacktrace() {
        super();
    }

    public JsStacktrace(Collection<JsStacktraceEntry> c) {
        super(c);
    }

}

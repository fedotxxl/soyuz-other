package io.belov.soyuz.log;

import org.slf4j.event.Level;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by fbelov on 18.09.15.
 */
public class BootstrapLog {

    public BootstrapLog() {
        this(null, null);
    }

    public BootstrapLog(Collection<String> debugPackages, Collection<String> tracePackages) {
        Slf4jUtils.registerJulToSlf4j();

        trace(tracePackages);
        debug(debugPackages);
    }

    public void debug(String... debugPackages) {
        debug(Arrays.asList(debugPackages));
    }

    public void debug(Collection<String> debugPackages) {
        if (debugPackages != null && debugPackages.size() > 0) {
            LogbackUtils.setLogLevelForPackages(Level.DEBUG, debugPackages);
        }
    }

    public void trace(String... tracePackages) {
        trace(Arrays.asList(tracePackages));
    }

    public void trace(Collection<String> tracePackages) {
        if (tracePackages != null && tracePackages.size() > 0) {
            LogbackUtils.setLogLevelForPackages(Level.TRACE, tracePackages);
        }
    }

    public void debugJooq() {
        LogbackUtils.setLogLevelForPackages(Level.DEBUG, "org.jooq");
    }

}

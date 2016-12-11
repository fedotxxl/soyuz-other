package io.belov.soyuz.log;

import org.slf4j.event.Level;

import java.util.Collection;

/**
 * Created by fbelov on 18.09.15.
 */
public class BootstrapLog {

    public BootstrapLog(Collection<String> debugPackages, Collection<String> tracePackages) {
        Slf4jUtils.registerJulToSlf4j();

        if (debugPackages != null && debugPackages.size() > 0) {
            LogbackUtils.setLogLevelForPackages(Level.DEBUG, debugPackages);
        }

        if (tracePackages != null && tracePackages.size() > 0) {
            LogbackUtils.setLogLevelForPackages(Level.TRACE, tracePackages);
        }
    }

    public void debugJooq() {
        LogbackUtils.setLogLevelForPackages(Level.DEBUG, "org.jooq");
    }

}

package io.thedocs.soyuz.db.jooq.performance;

import io.thedocs.soyuz.log.LoggerEvents;
import io.thedocs.soyuz.to;

/**
 * Created on 21.10.17.
 */
public class JooqPerformancePrinter {

    private static final LoggerEvents loge = LoggerEvents.getInstance(JooqPerformancePrinter.class);

    private JooqPerformanceCollector performanceCollector;
    private boolean resetOnEachIteration;

    public JooqPerformancePrinter(JooqPerformanceCollector performanceCollector, int delayInSeconds, boolean resetOnEachIteration) {
        this.performanceCollector = performanceCollector;
        this.resetOnEachIteration = resetOnEachIteration;

        if (delayInSeconds > 0) {
            to.e.daemonForever("jooq-performance-printer", delayInSeconds * 1000, this::print, delayInSeconds * 1000).start();
        }
    }

    private void print() {
        loge.info("jooq.performance", to.map("p", "\n" + to.s(performanceCollector.getSlowest(), "\n")));

        if (resetOnEachIteration) {
            performanceCollector.reset();
        }
    }
}

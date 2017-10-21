package io.thedocs.soyuz.db.jooq.performance;

import com.google.common.base.Stopwatch;
import org.jooq.ExecuteContext;
import org.jooq.Query;
import org.jooq.impl.DefaultExecuteListener;

import java.util.concurrent.TimeUnit;

/**
 * Собирает статистику скорости запросов к БД
 */
public class JooqPerformanceListener extends DefaultExecuteListener {

    private JooqPerformanceCollector performanceCollector;
    private Stopwatch sw;

    public JooqPerformanceListener(JooqPerformanceCollector performanceCollector) {
        this.performanceCollector = performanceCollector;
    }

    @Override
    public void executeStart(ExecuteContext ctx) {
        sw = Stopwatch.createStarted();
        super.executeStart(ctx);
    }

    @Override
    public void executeEnd(ExecuteContext ctx) {
        super.executeEnd(ctx);

        Query query = ctx.query();

        if (query != null) {
            performanceCollector.add(ctx.query().getSQL(), sw.elapsed(TimeUnit.MILLISECONDS));
        }
    }

}

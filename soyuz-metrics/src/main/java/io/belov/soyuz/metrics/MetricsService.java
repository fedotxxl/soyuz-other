package io.belov.soyuz.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import java.util.concurrent.Callable;

/**
 * Created by fbelov on 03.09.15.
 */
public class MetricsService {

    private MetricRegistry registry = new MetricRegistry();

    public MetricRegistry getRegistry() {
        return registry;
    }

    public void doAndTime(String metric, Runnable action) {
        Timer.Context context = registry.timer(metric).time();

        try {
            action.run();
        } finally {
            context.stop();
        }
    }

    public <T> T doAndTime(String metric, Callable<T> action) {
        Timer.Context context = registry.timer(metric).time();

        try {
            return action.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            context.stop();
        }
    }

    public void count(String metric) {
        count(metric, 1);
    }

    public void count(String metric, long value){
        registry.counter(metric).inc(value);
    }

}

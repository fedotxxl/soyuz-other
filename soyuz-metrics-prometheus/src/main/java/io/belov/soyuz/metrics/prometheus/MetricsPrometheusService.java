package io.belov.soyuz.metrics.prometheus;

import io.belov.soyuz.utils.thread;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.PushGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Created by fbelov on 17.09.15.
 */
public class MetricsPrometheusService {

    private static final Logger log = LoggerFactory.getLogger(MetricsPrometheusService.class);

    public void pushMetricsEveryMillis(String url, long intervalInMillis) {
        if (url == null || intervalInMillis <= 0 || url.length() == 0) return;

        thread.startDaemon(() -> {
            PushGateway pg = new PushGateway(url);

            while (true) {
                try {
                    pg.pushAdd(CollectorRegistry.defaultRegistry, "app");
                } catch (IOException e) {
                    log.error("Exception on sending metrics to gateway - " + e.toString());
                }

                thread.sleep(intervalInMillis);
            }
        });
    }

    public void doAndTime(Histogram metric, Runnable action) {
        Histogram.Timer requestTimer = metric.startTimer();
        try {
            action.run();
        } finally {
            requestTimer.observeDuration();
        }
    }

    public <T> T doAndTime(Histogram metric, Callable<T> action) {
        Histogram.Timer requestTimer = metric.startTimer();

        try {
            return action.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            requestTimer.observeDuration();
        }
    }

}

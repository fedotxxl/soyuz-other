package io.belov.soyuz.metrics.prometheus;

import io.thedocs.soyuz.log.LoggerEvents;
import io.thedocs.soyuz.to;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.PushGateway;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Created by fbelov on 17.09.15.
 */
public class MetricsPrometheusService {

    private static final LoggerEvents loge = LoggerEvents.getInstance(MetricsPrometheusService.class);

    public void pushMetricsEveryMillis(String url, long intervalInMillis) {
        if (url == null || intervalInMillis <= 0 || url.length() == 0) return;

        PushGateway pg = new PushGateway(url);

        to.e.daemonForever("prometheus.push", intervalInMillis, () -> {
            try {
                pg.pushAdd(CollectorRegistry.defaultRegistry, "app");
            } catch (IOException e) {
                loge.error("prometheus.push.e", to.map("e", e.toString()));
            }
        }).start();
    }

    public void doAndTime(Histogram metric, Runnable action) {
        Histogram.Timer requestTimer = metric.startTimer();
        try {
            action.run();
        } finally {
            requestTimer.observeDuration();
        }
    }

    @SneakyThrows
    public <T> T doAndTime(Histogram metric, Callable<T> action) {
        Histogram.Timer requestTimer = metric.startTimer();

        try {
            return action.call();
        } finally {
            requestTimer.observeDuration();
        }
    }

}

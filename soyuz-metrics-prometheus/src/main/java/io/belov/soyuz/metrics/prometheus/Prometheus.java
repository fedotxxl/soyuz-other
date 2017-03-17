package io.belov.soyuz.metrics.prometheus;

import io.belov.soyuz.utils.is;
import io.belov.soyuz.utils.to;
import io.prometheus.client.Histogram;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created on 08.02.17.
 */
public class Prometheus {

    private static final Map<String, Histogram> HISTOGRAMS_BY_NAME = new HashMap<>();

    public static void doAndTime(String metricName, Runnable action) {
        doAndTime(getMetricByName(metricName), (String) null, action);
    }

    public static void doAndTime(Histogram metric, Runnable action) {
        doAndTime(metric, (String) null, action);
    }

    public static void doAndTime(Histogram metric, @Nullable String label, Runnable action) {
        doAndTime(metric, (is.t(label)) ? to.list(label) : null, action);
    }

    public static void doAndTime(Histogram metric, @Nullable List<String> labels, Runnable action) {
        Histogram.Timer timer;

        if (labels != null) {
            timer = metric.labels(to.arrOfStrings(labels)).startTimer();
        } else {
            timer = metric.startTimer();
        }

        try {
            action.run();
        } finally {
            timer.observeDuration();
        }
    }

    public static <T> T doAndTime(String metricName, Callable<T> action) {
        return doAndTime(getMetricByName(metricName), action);
    }

    public static <T> T doAndTime(Histogram metric, Callable<T> action) {
        return doAndTime(metric, (String) null, action);
    }

    public static <T> T doAndTime(Histogram metric, @Nullable String label, Callable<T> action) {
        return doAndTime(metric, (is.t(label)) ? to.list(label) : null, action);
    }

    public static <T> T doAndTime(Histogram metric, @Nullable List<String> labels, Callable<T> action) {
        Histogram.Timer timer;

        if (labels != null) {
            timer = metric.labels(to.arrOfStrings(labels)).startTimer();
        } else {
            timer = metric.startTimer();
        }

        try {
            return action.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            timer.observeDuration();
        }
    }

    private static synchronized Histogram getMetricByName(String metricName) {
        return HISTOGRAMS_BY_NAME.computeIfAbsent(metricName, k -> Histogram.build().name(metricName).help(metricName).register());
    }
}

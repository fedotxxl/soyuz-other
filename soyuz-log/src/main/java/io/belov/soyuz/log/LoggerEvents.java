package io.belov.soyuz.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by fbelov on 05.02.16.
 */
public class LoggerEvents {

    private static final Object[] EMPTY_PARAMS = new Object[] {};

    public static LoggerEvents getInstance(Logger logger) {
        return new LoggerEvents(logger);
    }

    public static LoggerEvents getInstance(Class clazz) {
        return new LoggerEvents(LoggerFactory.getLogger(clazz));
    }

    private Logger log;

    private LoggerEvents(Logger logger) {
        this.log = logger;
    }

    public void debug(String event) {
        debug(event, null);
    }

    public void debug(String event, Map params) {
        log.debug(toLogMessage(event, params), toLogParams(params));
    }

    public void info(String event) {
        info(event, null);
    }

    public void info(String event, Map params) {
        log.info(toLogMessage(event, params), toLogParams(params));
    }

    public void warn(String event) {
        warn(event, (Map) null);
    }

    public void warn(String event, Throwable throwable) {
        log.warn(event, throwable);
    }

    public void warn(String event, Map params) {
        log.warn(toLogMessage(event, params), toLogParams(params));
    }

    public void warn(String event, Map params, Throwable throwable) {
        log.warn(event + ": " + params.toString(), throwable);
    }

    public void error(String event) {
        error(event, (Map) null);
    }

    public void error(String event, Throwable throwable) {
        log.error(event, throwable);
    }

    public void error(String event, Map params) {
        log.error(toLogMessage(event, params), toLogParams(params));
    }

    public void error(String event, Map params, Throwable throwable) {
        log.error(event + ": " + params.toString(), throwable);
    }

    private String toLogMessage(String event, Map params) {
        if (params == null) {
            return event;
        } else {
            return event + ": {}";
        }
    }

    private Object[] toLogParams(Map params) {
        if (params == null) {
            return EMPTY_PARAMS;
        } else {
            return new Object[] { params };
        }
    }

}

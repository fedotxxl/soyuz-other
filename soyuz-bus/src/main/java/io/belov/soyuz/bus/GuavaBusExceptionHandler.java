package io.belov.soyuz.bus;

import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import io.belov.soyuz.log.LoggerEvents;

/**
 * Created by fbelov on 16.09.15.
 */
public class GuavaBusExceptionHandler implements SubscriberExceptionHandler {

    private static final LoggerEvents loge = LoggerEvents.getInstance(GuavaBusExceptionHandler.class);

    @Override
    public void handleException(Throwable t, SubscriberExceptionContext context) {
        loge.error("bus.e", ImmutableMap.of("e", context.getEvent()), t);
    }
}

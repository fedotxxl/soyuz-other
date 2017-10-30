package io.belov.soyuz.bus;

import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import io.thedocs.soyuz.log.LoggerEvents;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by fbelov on 16.09.15.
 */
public class GuavaBusService implements ApplicationListener<ContextRefreshedEvent> {

    private static final LoggerEvents loge = LoggerEvents.getInstance(GuavaBusService.class);

    private boolean isBeansRegistered = false;
    private ApplicationContext applicationContext;
    private EventBus bus;

    public GuavaBusService(ApplicationContext applicationContext, EventBus bus) {
        this.applicationContext = applicationContext;
        this.bus = bus;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!isBeansRegistered) {
            Collection<GuavaBusBean> beans = applicationContext.getBeansOfType(GuavaBusBean.class).values();

            loge.debug("bus.registerListeners", ImmutableMap.of("listeners", beans.stream().map(b -> b.getClass().getName()).collect(Collectors.toList())));

            for (GuavaBusBean bean : beans) {
                bus.register(bean);
            }

            isBeansRegistered = true;
        }
    }
}

package io.belov.soyuz.bus;

import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import io.thedocs.soyuz.log.LoggerEvents;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by fbelov on 16.09.15.
 */
public class GuavaBusService {

    private static final LoggerEvents loge = LoggerEvents.getInstance(GuavaBusService.class);

    private ApplicationContext applicationContext;
    private EventBus bus;

    public GuavaBusService(ApplicationContext applicationContext, EventBus bus) {
        this.applicationContext = applicationContext;
        this.bus = bus;
    }

    @PostConstruct
    private void setUp() {
        registerBusBeans(applicationContext);
    }

    public void registerBusBeans(ApplicationContext applicationContext) {
        Collection<GuavaBusBean> beans = applicationContext.getBeansOfType(GuavaBusBean.class).values();

        loge.debug("bus.registerListeners", ImmutableMap.of("listeners", beans.stream().map(b -> b.getClass().getName()).collect(Collectors.toList())));

        for (GuavaBusBean bean : beans) {
            bus.register(bean);
        }
    }
}

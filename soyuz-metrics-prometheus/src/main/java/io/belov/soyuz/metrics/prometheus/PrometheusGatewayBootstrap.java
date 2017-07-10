package io.belov.soyuz.metrics.prometheus;

import io.thedocs.soyuz.log.LoggerEvents;
import io.thedocs.soyuz.is;
import io.thedocs.soyuz.to;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.PushGateway;

import java.io.IOException;

/**
 * Created by fbelov on 06.04.16.
 */
public class PrometheusGatewayBootstrap {

    private static final LoggerEvents loge = LoggerEvents.getInstance(PrometheusGatewayBootstrap.class);

    public PrometheusGatewayBootstrap(String gatewayUrl, String job) {
        if (is.tt(gatewayUrl) && is.tt(job)) {
            pushMetricsTo(gatewayUrl, job);
        }
    }

    private void pushMetricsTo(String gatewayUrl, String job) {
        loge.debug("metrics.prometheus.configurePushGateway", to.map("url", gatewayUrl));

        Thread thread = new Thread(() -> {
            PushGateway pg = new PushGateway(gatewayUrl);

            try {
                while (true) {
                    try {
                        pg.pushAdd(CollectorRegistry.defaultRegistry, job);
                    } catch (IOException e) {
                        loge.error("metrics.promtheus.e", to.map("e", e.toString()));
                    }

                    Thread.sleep(5000);
                }
            } catch (InterruptedException e) {
            }
        });

        thread.setDaemon(true);
        thread.start();
    }
}

package io.belov.soyuz.rabbit;

import com.google.common.base.Throwables;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Created by fbelov on 04.11.15.
 */
public class RabbitChannelProvider {

    private ObjectPool<Channel> pool;

    public RabbitChannelProvider(Connection connection, int maxTotal) {
        RabbitChannelFactory factory = new RabbitChannelFactory(connection);
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(maxTotal);

        pool = new GenericObjectPool<>(factory, config);
    }

    Channel provide() {
        try {
            return pool.borrowObject();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    void release(Channel processor) {
        try {
            pool.returnObject(processor);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

}

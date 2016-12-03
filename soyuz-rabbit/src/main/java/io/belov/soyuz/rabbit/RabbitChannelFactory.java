package io.belov.soyuz.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * Created by fbelov on 15.11.15.
 */
public class RabbitChannelFactory extends BasePooledObjectFactory<Channel> {

    private Connection connection;

    public RabbitChannelFactory(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Channel create() throws Exception {
        return connection.createChannel();
    }

    @Override
    public PooledObject<Channel> wrap(Channel obj) {
        return new DefaultPooledObject<>(obj);
    }
}

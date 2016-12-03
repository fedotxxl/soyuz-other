package io.belov.soyuz.rabbit;

import com.google.common.base.Throwables;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import io.belov.soyuz.utils.thread;
import io.belov.soyuz.json.JacksonUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

/**
 * Created by fbelov on 15.11.15.
 */
public class Rabbit {

    private RabbitChannelProvider channelProvider;

    public Rabbit(String uri, int maxChannelsCount) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(uri);

            //Recommended settings
            factory.setRequestedHeartbeat(30);
            factory.setConnectionTimeout(30000);

            Connection connection = factory.newConnection();

            channelProvider = new RabbitChannelProvider(connection, maxChannelsCount);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public void createQueue(String queueName, boolean durable) {
        Channel channel = getChannel();

        try {
            channel.queueDeclare(queueName, durable, false, false, null);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        } finally {
            releaseChannel(channel);
        }
    }

    public <T> QueueSender<T> createQueueSender(String queueName) {
        return new QueueSender<>(this, queueName);
    }

    public <T> QueueListener<T> createQueueListener(String queueName, Class<T> parameterClass, Consumer<T> listener) {
        return new QueueListener<>(this, queueName, parameterClass, listener);
    }

    private Channel getChannel() {
        return channelProvider.provide();
    }

    private void releaseChannel(Channel channel) {
        channelProvider.release(channel);
    }


    public static class QueueSender<T> {
        private Rabbit rabbit;
        private String name;

        public QueueSender(Rabbit rabbit, String name) {
            this.rabbit = rabbit;
            this.name = name;
        }

        public void send(T o) {
            Channel channel = rabbit.getChannel();

            try {
                channel.basicPublish("", name, null, toBytes(o));
            } catch (IOException e) {
                throw Throwables.propagate(e);
            } finally {
                rabbit.releaseChannel(channel);
            }
        }

        private byte[] toBytes(T o) {
            try {
                return JacksonUtils.toJson(o).getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw Throwables.propagate(e);
            }
        }
    }

    public static class QueueListener<T> {
        private Rabbit rabbit;
        private String name;
        private Class<T> parameterClass;
        private Consumer<T> listener;
        private Thread listenerDaemon;
        private volatile boolean isEnabled = true;

        public QueueListener(Rabbit rabbit, String name, Class<T> parameterClass, Consumer<T> listener) {
            this.rabbit = rabbit;
            this.name = name;
            this.parameterClass = parameterClass;
            this.listener = listener;

            checkAndStartListening();
        }

        public void stop() {
            isEnabled = false;
            listenerDaemon = null;
        }

        private void checkAndStartListening() {
            if (listenerDaemon == null) {
                listenerDaemon = thread.startDaemon(() -> {
                    Channel consumerChannel = rabbit.getChannel();

                    try {
                        QueueingConsumer consumer = new QueueingConsumer(consumerChannel);
                        consumerChannel.basicConsume(name, true, consumer);

                        while (isEnabled) {
                            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                            T data = toObject(delivery.getBody());

                            listener.accept(data);
                        }
                    } catch (Exception e) {
                        throw Throwables.propagate(e);
                    } finally {
                        rabbit.releaseChannel(consumerChannel);
                    }
                });
            }
        }

        private T toObject(byte[] bytes) {
            try {
                return JacksonUtils.fromJson(new String(bytes, "UTF-8"), parameterClass);
            } catch (UnsupportedEncodingException e) {
                throw Throwables.propagate(e);
            }
        }
    }
}

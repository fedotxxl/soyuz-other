package io.belov.soyuz.queue;

/**
 * Created by fbelov on 13.10.15.
 */
public interface QueueProcessResult {

    QueueProcessResult SUCCESS = new SimpleQueueProcessResult(true);
    QueueProcessResult FAILURE = new SimpleQueueProcessResult(false);

    boolean isSuccess();

    class SimpleQueueProcessResult implements QueueProcessResult {

        private boolean result;

        private SimpleQueueProcessResult(boolean result) {
            this.result = result;
        }

        public boolean isSuccess() {
            return result;
        }
    }
}

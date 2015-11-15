package io.belov.soyuz.queue;

import org.jongo.MongoCursor;

import javax.annotation.Nullable;

/**
 * Created by fbelov on 01.06.15.
 */
public interface QueueLoader {

    @Nullable
    MongoCursor<QueueAction> listActions();

}

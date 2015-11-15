package io.belov.soyuz.mongo;


import org.bson.types.ObjectId;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by fbelov on 17.05.15.
 */
public class JongoId {

    private ObjectId _id;

    public static List<ObjectId> toList(Iterable<JongoId> ids) {
        return StreamSupport.stream(ids.spliterator(), false)
                .map(o -> o._id)
                .collect(Collectors.toList());
    }
}

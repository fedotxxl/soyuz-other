package io.belov.soyuz.tasks;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fbelov on 18.02.16.
 */
public class TasksQueueContextCreatorMap implements TasksQueueContextCreatorI<Map> {

    @Override
    public Map createContext(Task task) {
        return new HashMap<>();
    }

}

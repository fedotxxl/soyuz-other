package io.belov.soyuz.utils.exec;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by fbelov on 08.11.15.
 */
public class CollectingLogListener implements Consumer<String> {

    private List<String> entries = new LinkedList<>();

    @Override
    public void accept(String s) {
        entries.add(s);
    }

    public List<String> get() {
        return entries;
    }
}

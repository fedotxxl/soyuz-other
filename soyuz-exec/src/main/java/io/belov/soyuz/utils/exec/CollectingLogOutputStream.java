package io.belov.soyuz.utils.exec;

import org.apache.commons.exec.LogOutputStream;
import org.slf4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by fbelov on 07.11.15.
 */
public class CollectingLogOutputStream extends LogOutputStream {

    private final List<String> lines = new LinkedList<>();
    private final Logger log;
    private final Consumer<String> listener;

    public CollectingLogOutputStream(Consumer<String> listener) {
        this(listener, null);
    }

    public CollectingLogOutputStream(Consumer<String> listener, Logger log) {
        this.listener = listener;
        this.log = log;
    }

    @Override
    protected void processLine(String line, int level) {
        if (log != null) {
            log.info(line);
        }

        if (listener != null) {
            listener.accept(line);
        }

        lines.add(line);
    }

    public List<String> getLines() {
        return lines;
    }
}

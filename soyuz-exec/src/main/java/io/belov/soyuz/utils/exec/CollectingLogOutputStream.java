package io.belov.soyuz.utils.exec;

import org.apache.commons.exec.LogOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by fbelov on 07.11.15.
 */
public class CollectingLogOutputStream extends LogOutputStream {

    private static final Logger log = LoggerFactory.getLogger(CollectingLogOutputStream.class);

    private final List<String> lines = new LinkedList<>();
    private final Consumer<String> listener;

    public CollectingLogOutputStream(Consumer<String> listener) {
        this.listener = listener;
    }

    @Override
    protected void processLine(String line, int level) {
        log.info(line);

        if (listener != null) {
            listener.accept(line);
        }

        lines.add(line);
    }

    public List<String> getLines() {
        return lines;
    }
}

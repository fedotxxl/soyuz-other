package io.belov.soyuz.js;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fbelov on 11.10.15.
 */
public class JsStacktraceWithOriginalAndMappedEntries extends ArrayList<JsStacktraceOriginalAndMappedEntry> {

    public JsStacktraceWithOriginalAndMappedEntries() {
        super();
    }

    public JsStacktraceWithOriginalAndMappedEntries(Collection<JsStacktraceOriginalAndMappedEntry> c) {
        super(c);
    }

    public static JsStacktraceWithOriginalAndMappedEntries from(JsStacktrace original) {
        return from(new JsStacktraceOriginalAndMapped(original, null));
    }

    public static JsStacktraceWithOriginalAndMappedEntries from(JsStacktraceOriginalAndMapped stack) {
        JsStacktrace original = stack.getOriginal();
        JsStacktrace mapped = stack.getMapped();
        List<JsStacktraceOriginalAndMappedEntry> entries = new ArrayList<>(original.size());

        for (int i = 0; i < original.size(); i++) {
            JsStacktraceEntry o = original.get(i);
            JsStacktraceEntry m = (mapped == null) ? null : mapped.get(i);

            entries.add(new JsStacktraceOriginalAndMappedEntry(o, m));
        }

        return new JsStacktraceWithOriginalAndMappedEntries(entries);
    }

    public static List<JsStacktraceWithOriginalAndMappedEntries> from(List<JsStacktraceOriginalAndMapped> stacks) {
        return stacks
                .stream()
                .map(JsStacktraceWithOriginalAndMappedEntries::from)
                .collect(Collectors.toList());
    }
}

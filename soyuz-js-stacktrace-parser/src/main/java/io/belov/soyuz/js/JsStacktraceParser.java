package io.belov.soyuz.js;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by fbelov on 20.04.15.
 */
public class JsStacktraceParser {

    private static final Logger log = LoggerFactory.getLogger(JsStacktraceParser.class);

    private List<JsStacktracePattern> patterns;

    private static final Map<String, String> GROUP_SHORT = ImmutableMap.of(
            "method", "m",
            "file", "f",
            "line", "l",
            "column", "c"
    );

    public JsStacktraceParser() {
        patterns = Arrays.asList(
                new JsStacktracePattern("^(?: *)(?:at )?(?<method>.*?) [<(](?<file>.*?):(?<line>\\d+)(?::(?<column>\\d+))?[>)]$", "mflc"),
                new JsStacktracePattern("^(?: +)(?:at )(?<file>.*?):(?<line>\\d+)(?::(?<column>\\d+))?$", "flc"),
                new JsStacktracePattern("^(?: +)(?:at )(?<method>.*?) [<(](?<file>.*?)[>)]$", "mf")
        );
    }

    public JsStacktrace parse(String stacktrace) {
        String[] lines = stacktrace.split("\n");

        return new JsStacktrace(IntStream
                .range(0, lines.length)
                .mapToObj(i -> parseLine(lines[i], i))
                .collect(Collectors.toList()));
    }

    public List<JsStacktrace> parseAll(List<String> stacks) {
        return stacks.stream().map(this::parse).collect(Collectors.toList());
    }

    private JsStacktraceEntry parseLine(String entry, int entryNumber) {
        JsStacktracePatternWithMatcher patternWithMatcher = getPatternWithMatcherForLine(entry);

        if (patternWithMatcher == null) {
            if (entryNumber != 0) {
                log.warn("Can't parse line '{}'", entry);
            }

            return new JsStacktraceEntry(entry);
        } else {
            JsStacktracePattern pattern = patternWithMatcher.pattern;
            Matcher matcher = patternWithMatcher.matcher;

            String source;
            String method = (pattern.hasMethod) ? matcher.group("method") : null;
            String file = (pattern.hasFile) ? matcher.group("file") : null;
            int line = (pattern.hasLine) ? Integer.valueOf(matcher.group("line")) : -1;
            int column = (pattern.hasColumn) ? ((matcher.group("column") == null) ? -1 : Integer.valueOf(matcher.group("column"))) : -1;

            List<JsStacktraceGroupReplacement> groupReplacements = pattern.groups.stream()
                    .map(g -> new JsStacktraceGroupReplacement("{" + GROUP_SHORT.get(g) + "}", matcher.start(g), matcher.end(g), matcher.group(g)))
                    .sorted(Comparator.comparing(JsStacktraceGroupReplacement::getStart))
                    .collect(Collectors.toList());

            StringBuilder sb = new StringBuilder(entry);
            int replaceShift = 0;

            for (JsStacktraceGroupReplacement gr : groupReplacements) {
                if (gr.start >= 0) {
                    sb.replace(gr.start + replaceShift, gr.end + replaceShift, gr.replaceTo);
                    replaceShift += gr.replaceTo.length() - gr.value.length();
                }
            }

            source = sb.toString();

            return new JsStacktraceEntry(source, method, file, line, column);
        }
    }

    private JsStacktracePatternWithMatcher getPatternWithMatcherForLine(String entry) {
        for (JsStacktracePattern pattern : patterns) {
            Matcher matcher = pattern.pattern.matcher(entry);
            if (matcher.find()) {
                return new JsStacktracePatternWithMatcher(pattern, matcher);
            }
        }

        return null;
    }

    private static class JsStacktracePatternWithMatcher {
        private JsStacktracePattern pattern;
        private Matcher matcher;

        public JsStacktracePatternWithMatcher(JsStacktracePattern pattern, Matcher matcher) {
            this.pattern = pattern;
            this.matcher = matcher;
        }
    }

    private static class JsStacktracePattern {
        private Pattern pattern;
        private HashSet<String> groups;
        private boolean hasMethod;
        private boolean hasFile;
        private boolean hasLine;
        private boolean hasColumn;

        public JsStacktracePattern(String pattern, String elements) {
            this.pattern = Pattern.compile(pattern);
            this.hasMethod = elements.contains("m");
            this.hasFile = elements.contains("f");
            this.hasLine = elements.contains("l");
            this.hasColumn = elements.contains("c");
            this.groups = new HashSet<>();

            if (hasMethod) groups.add("method");
            if (hasFile) groups.add("file");
            if (hasLine) groups.add("line");
            if (hasColumn) groups.add("column");
        }
    }

    private static class JsStacktraceGroupReplacement {
        private String replaceTo;
        private String value;
        private int start;
        private int end;

        public JsStacktraceGroupReplacement(String replaceTo, int start, int end, String value) {
            this.replaceTo = replaceTo;
            this.start = start;
            this.end = end;
            this.value = value;
        }

        public int getStart() {
            return start;
        }
    }
}

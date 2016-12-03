package io.belov.soyuz.text;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by fbelov on 05.11.15.
 */
public class TextDataFromCommentsExtractor implements TextDataExtractor<List<String>> {

    private static final String TITLE_PREFIX = "title:";
    private static final int TITLE_PREFIX_LENGTH = TITLE_PREFIX.length();
    private static final String TAGS_PREFIX = "tags:";
    private static final int TAGS_PREFIX_LENGTH = TAGS_PREFIX.length();

    @Override
    public TextData extract(List<String> comments) {
        String title = null;
        Set<String> tags = new HashSet<>();

        for (String comment : comments) {
            comment = comment.trim();

            if (comment.startsWith(TITLE_PREFIX)) {
                title = comment.substring(TITLE_PREFIX_LENGTH).trim();
            } else if (comment.startsWith(TAGS_PREFIX)) {
                tags = splitTags(comment.substring(TAGS_PREFIX_LENGTH));
            }
        }

        return new TextData(title, tags);
    }

    private Set<String> splitTags(String tagsString) {
        Set<String> answer = new HashSet<>();
        String[] tags = tagsString.split(",");

        for (String tag : tags) {
            answer.add(tag.trim());
        }

        return answer;
    }
}

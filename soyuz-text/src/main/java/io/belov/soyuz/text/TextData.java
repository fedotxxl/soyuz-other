package io.belov.soyuz.text;

import lombok.Value;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by fbelov on 05.11.15.
 */
@Value
public class TextData {

    public static TextData EMPTY = new TextData(null, new HashSet<>());

    private String title;
    private Set<String> tags;

}

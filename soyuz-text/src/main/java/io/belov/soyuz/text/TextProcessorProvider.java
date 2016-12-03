package io.belov.soyuz.text;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fbelov on 26.11.15.
 */
public class TextProcessorProvider {

    private List<TextProcessor> textProcessors = new ArrayList<>();

    public TextProcessorProvider(List<TextProcessor> textProcessors) {
        this.textProcessors = textProcessors;
    }

    @Nullable
    public TextProcessor getFor(File file) {
        return textProcessors.stream().filter((tp) -> tp.isSupported(file)).findFirst().orElse(null);
    }

}

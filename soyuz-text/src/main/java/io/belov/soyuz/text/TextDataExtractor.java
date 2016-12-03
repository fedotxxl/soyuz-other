package io.belov.soyuz.text;

/**
 * Created by fbelov on 05.11.15.
 */
public interface TextDataExtractor<T> {

    TextData extract(T o);

}

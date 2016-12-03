package io.belov.soyuz.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fbelov on 28.06.15.
 */
public class JacksonUtils {

    private static final Logger log = LoggerFactory.getLogger(JacksonUtils.class);
    private static final ObjectWriter writer = new ObjectMapper().writer();
    private static final ObjectReader reader = new ObjectMapper().reader();
    private static final ObjectMapper jsReader;

    static {
        //http://stackoverflow.com/questions/26591359/parsing-non-json-javascript-object-with-jackson
        jsReader = new ObjectMapper();
        jsReader
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                .configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    }

    public static String toJson(Object o) {
        try {
            return writer.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw Throwables.propagate(e);
        }
    }

    public static String toJsonOrNull(Object o) {
        return (o != null) ? toJson(o) : null;
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return reader.withType(clazz).readValue(json);
        } catch (Throwable e) {
            log.warn("Exception on parsing JSON {} to {}", json, clazz);
            throw Throwables.propagate(e);
        }
    }

    public static <T> T fromJavaScript(String js, Class<T> clazz) {
        try {
            return jsReader.readValue(js, clazz);
        } catch (Throwable e) {
            log.warn("Exception on parsing JavaScript {} to {}", js, clazz);
            throw Throwables.propagate(e);
        }
    }

}

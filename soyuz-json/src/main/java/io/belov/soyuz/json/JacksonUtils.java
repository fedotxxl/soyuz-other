package io.belov.soyuz.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import io.belov.soyuz.log.LoggerEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * Created by fbelov on 28.06.15.
 */
public class JacksonUtils {

    private static final LoggerEvents loge = LoggerEvents.getInstance(JacksonUtils.class);

    private static final ObjectWriter writer = new ObjectMapper().writer();
    private static final ObjectReader reader = new ObjectMapper().reader();
    private static final ObjectMapper jsReader;

    public static final Consumer<ObjectMapper> CONFIGURER_CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES = (om) -> {
        om.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
    };

    public static final Consumer<ObjectMapper> CONFIGURER_FAIL_SAFE = (om) -> {
        om
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    };

    static {
        //http://stackoverflow.com/questions/26591359/parsing-non-json-javascript-object-with-jackson
        jsReader = new ObjectMapper();
        jsReader
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                .configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    }

    public static String toJson(Object o) {
        return toJson(o, writer);
    }

    public static String toJson(Object o, ObjectMapper objectMapper) {
        return toJson(o, objectMapper.writer());
    }

    public static String toJson(Object o, ObjectWriter writer) {
        try {
            return writer.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJsonOrNull(Object o) {
        return toJsonOrNull(o, writer);
    }

    public static String toJsonOrNull(Object o, ObjectMapper objectMapper) {
        return toJsonOrNull(o, objectMapper.writer());
    }

    public static String toJsonOrNull(Object o, ObjectWriter writer) {
        return (o != null) ? toJson(o, writer) : null;
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return fromJson(json, clazz, reader);
    }

    public static <T> T fromJson(String json, Class<T> clazz, ObjectMapper mapper) {
        return fromJson(json, clazz, mapper.reader());
    }

    public static <T> T fromJson(String json, Class<T> clazz, ObjectReader reader) {
        try {
            return reader.withType(clazz).readValue(json);
        } catch (Throwable e) {
            loge.error("json.from.e", ImmutableMap.of("json", json, "class", clazz), e);

            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJavaScript(String js, Class<T> clazz) {
        try {
            return jsReader.readValue(js, clazz);
        } catch (Throwable e) {
            loge.error("json.js.from.e", ImmutableMap.of("js", js, "class", clazz), e);

            throw new RuntimeException(e);
        }
    }

}

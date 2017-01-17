package io.belov.soyuz.json;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created on 16.01.17.
 */
public class JacksonService {

    private ObjectMapper objectMapper;

    public JacksonService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toJson(Object object) {
        return JacksonUtils.toJson(object, objectMapper);
    }

    public String toJsonOrNull(Object object) {
        return (object != null) ? toJson(object) : null;
    }

    public <T> T fromJson(String json, Class<T> clazz) {
        return JacksonUtils.fromJson(json, clazz, objectMapper);
    }
}

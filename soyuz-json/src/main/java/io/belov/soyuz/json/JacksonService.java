package io.belov.soyuz.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

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

    @Nullable
    public <T> T fromJson(@Nullable String json, Class<T> clazz) {
        return (json == null) ? null : JacksonUtils.fromJson(json, clazz, objectMapper);
    }

    @Nullable
    public <T> List<T> fromJsonList(@Nullable String json, Class<T> clazz) {
        return (json == null) ? null : JacksonUtils.fromJson(json, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz), objectMapper);
    }

    @Nullable
    public <T> Set<T> fromJsonSet(@Nullable String json, Class<T> clazz) {
        return (json == null) ? null : JacksonUtils.fromJson(json, objectMapper.getTypeFactory().constructCollectionType(Set.class, clazz), objectMapper);
    }

}

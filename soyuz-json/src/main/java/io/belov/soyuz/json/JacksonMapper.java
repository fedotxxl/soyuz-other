package io.belov.soyuz.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * Created on 16.01.17.
 */
public class JacksonMapper {

    private JacksonService jacksonService;

    public JacksonMapper() {
        this(new ObjectMapper());
    }

    public JacksonMapper(ObjectMapper objectMapper) {
        this.jacksonService = new JacksonService(objectMapper);
    }

    public String toJson(Object object) {
        return jacksonService.toJson(object);
    }

    public String toJsonOrNull(Object object) {
        return toJsonOrNull(object);
    }

    @Nullable
    public <T> T fromJson(@Nullable String json, Class<T> clazz) {
        return jacksonService.fromJson(json, clazz);
    }

    @Nullable
    public <T> List<T> fromJsonList(@Nullable String json, Class<T> clazz) {
        return jacksonService.fromJsonList(json, clazz);
    }

    @Nullable
    public <T> Set<T> fromJsonSet(@Nullable String json, Class<T> clazz) {
        return jacksonService.fromJsonSet(json, clazz);
    }

}

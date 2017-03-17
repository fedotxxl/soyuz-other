package io.belov.soyuz.db.jooq.jsonb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import io.belov.soyuz.json.JacksonUtils;
import org.jooq.Converter;

import java.io.IOException;

/**
 * http://stackoverflow.com/a/27146852/716027
 */
public class PostgresJSONJacksonJsonNodeConverter implements Converter<Object, JsonNode> {

    public static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();

        if (System.getProperty("jooq.jsonb.camelCase") == null) {
            JacksonUtils.CONFIGURER_CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES.accept(OBJECT_MAPPER);
        }
    }

    @Override
    public JsonNode from(Object t) {
        try {
            return t == null
                    ? NullNode.instance
                    : OBJECT_MAPPER.readTree(t + "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object to(JsonNode u) {
        try {
            return u == null || u.equals(NullNode.instance)
                    ? null
                    : OBJECT_MAPPER.writeValueAsString(u);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<Object> fromType() {
        return Object.class;
    }

    @Override
    public Class<JsonNode> toType() {
        return JsonNode.class;
    }
};
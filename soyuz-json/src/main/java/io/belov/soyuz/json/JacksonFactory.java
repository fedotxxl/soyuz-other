package io.belov.soyuz.json;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by fbelov on 22.01.16.
 */
public class JacksonFactory {

    public static ObjectMapper createObjectMapper(Param... params) {
        ObjectMapper answer = new ObjectMapper();

        for (Param param : params) {
            if (param == Param.FAIL_SAFE) {
                JacksonUtils.CONFIGURER_FAIL_SAFE.accept(answer);
            } else if (param == Param.UNDERSCORE_NAMING_CONVENTION) {
                JacksonUtils.CONFIGURER_CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES.accept(answer);
            }
        }

        return answer;
    }

    public enum Param {
        FAIL_SAFE, UNDERSCORE_NAMING_CONVENTION
    }

}

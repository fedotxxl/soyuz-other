package io.belov.soyuz.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by fbelov on 14.11.16.
 */
class FvUtils {

    public static class to {
        public static <T> List<T> list(T... value) {
            List<T> answer = new ArrayList<T>();
            Collections.addAll(answer, value);
            return answer;
        }
    }

}

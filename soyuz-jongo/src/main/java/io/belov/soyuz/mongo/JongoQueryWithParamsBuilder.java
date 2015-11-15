package io.belov.soyuz.mongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fbelov on 06.06.15.
 */
public class JongoQueryWithParamsBuilder {

    private List<JongoFieldData> fields = new ArrayList<>();

    public JongoQueryWithParamsBuilder field(String field, Object param) {
        fields.add(new JongoFieldData(field, null, param));

        return this;
    }

    public JongoQueryWithParamsBuilder fieldWithOperator(String field, String operator) {
        return this.fieldWithOperator(field, operator, null);
    }

    public JongoQueryWithParamsBuilder fieldWithOperator(String field, String operator, Object... params) {
        fields.add(new JongoFieldData(field, operator, params));
        return this;
    }

    public JongoQueryWithParams build() {
        int i = 0;
        List<Object> params = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        sb.append("{");

        for (JongoFieldData field : fields) {
            Object[] fieldParams = field.getParams();

            if (fieldParams != null) {
                params.addAll(Arrays.asList(fieldParams));
            }

            if (i > 0) {
                sb.append(", ");
            }

            if (field.hasOperator()) {
                sb.append(field.getField());
                sb.append(": ");
                sb.append(field.getOperator());
            } else {
                sb.append(field.getField());
                sb.append(": #");
            }

            i++;
        }

        sb.append("}");

        return new JongoQueryWithParams(sb.toString(), params.toArray());
    }

    private static class JongoFieldData {
        private String field;
        private Object[] params;
        private String operator;

        public JongoFieldData(String field, String operator, Object... params) {
            this.field = field;
            this.params = params;
            this.operator = operator;
        }

        public boolean hasOperator() {
            return operator != null;
        }

        public String getField() {
            return field;
        }

        public Object[] getParams() {
            return params;
        }

        public String getOperator() {
            return operator;
        }
    }
}

package io.belov.soyuz.mongo;

/**
 * Created by fbelov on 26.04.15.
 */
public class JongoQueryWithParams {

    private String q;
    private Object[] params;

    public JongoQueryWithParams(String q) {
        this.q = q;
    }

    public JongoQueryWithParams(String q, Object... params) {
        this.q = q;
        this.params = params;
    }

    public String getQ() {
        return q;
    }

    public Object[] getParams() {
        return params;
    }

    public static JongoQueryWithParamsBuilder builder() {
        return new JongoQueryWithParamsBuilder();
    }
}

package io.belov.soyuz.route;

import io.belov.soyuz.utils.is;

import javax.script.*;import javax.script.Bindings;import javax.script.ScriptEngine;import javax.script.ScriptEngineManager;import javax.script.ScriptException;import javax.script.SimpleBindings;
import java.io.InputStream;
import java.io.Reader;import java.lang.Object;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by fbelov on 01.04.15.
 */
public class Router {

    private final Routes routes;

    public Router(Reader reader) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        Bindings bindings = new SimpleBindings();
        engine.eval(reader, bindings);
        RouteBuilder routeBuilder = new RouteBuilder();

        Map<Object, Object> routes = (Map) ((Map) bindings.get("nashorn.global")).get("vRoutes");
        mapRoutes(routeBuilder, routes, "");

        this.routes = routeBuilder.build();
    }

    private void mapRoutes(RouteBuilder routeBuilder, Map<Object, Object> routes, String path) {
        for (Map.Entry e : routes.entrySet()) {
            Object value = e.getValue();
            String part = (String) e.getKey();

            String currentPath = (is.tt(path)) ? path + "." + part : part;
            if (value instanceof Map) {
                mapRoutes(routeBuilder, (Map<Object, Object>) value, currentPath);
            } else {
                routeBuilder.add(currentPath, (String) value);
            }
        }
    }

    public String getRoutePath(String url) {
        int vimeo = url.indexOf('?');
        if (vimeo >= 0) url = url.substring(0, vimeo);

        vimeo = url.indexOf(':');
        if (vimeo >= 0) {
            url = url.substring(0, vimeo);
            vimeo = url.lastIndexOf('/');
            if (vimeo >= 0) {
                url = url.substring(0, vimeo);
            }
        }

        Routes routes = this.routes;

        Iterable<String> parts = Utils.splitPath(url);
        for (String part : parts) {
            routes = routes.getChild(part);
            if (routes == null) break;
        }

        if (routes != null) {
            return routes.getRouteName();
        } else {
            return null;
        }
    }

    public static class Utils {
        public static Iterable<String> splitPath(String path) {
            return Arrays
                    .stream(path.split("/"))
                    .filter(is::tt)
                    .collect(Collectors.toList());
        }
    }
}

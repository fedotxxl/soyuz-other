package io.thedocs.soyuz.route;

import io.thedocs.soyuz.is;
import io.thedocs.soyuz.to;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fbelov on 01.04.15.
 */
public class Routes {

    private boolean isBuilt = false;
    private String path;
    private String routeName;
    private Map<String, Routes> children = new HashMap<>();
    private Routes paramRoute = null;

    public Routes() {
    }

    public Routes(String path) {
        this.path = path;
    }

    Routes addChildren(Routes re) {
        if (isBuilt) {
            throw new IllegalStateException("Can't change state of built object");
        }

        if (!re.hasPath()) {
            throw new IllegalStateException("Path can't be empty or null");
        }

        String path = re.getPath().trim();

        if (path.startsWith(":")) {
            if (paramRoute == null) {
                paramRoute = re;
            }

            re = paramRoute;
        } else {
            if (children.containsKey(path)) {
                throw new IllegalStateException("Already contains path " + path);
            } else {
                children.put(path, re);
            }
        }

        return re;
    }

    Routes getChild(String path) {
        return children.get(path);
    }

    Routes getChildOrParamRoute(String path) {
        return to.or(children.get(path), this::getParamRoute);
    }

    public String getPath() {
        return path;
    }

    public boolean hasPath() {
        return is.tt(path);
    }

    private Routes getParamRoute(){
        return paramRoute;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        if (isBuilt) {
            throw new IllegalStateException("Can't change state of built object");
        }

        this.routeName = routeName;
    }

    public void build() {
        children.forEach((p, c) -> c.build());
        if (paramRoute != null) paramRoute.build();
        isBuilt = true;
    }
    
}

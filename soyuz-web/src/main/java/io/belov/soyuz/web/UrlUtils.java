package io.belov.soyuz.web;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by fbelov on 28.06.15.
 */
public class UrlUtils {

    public static String getProtocolAndHost(String url) {
        int slashslash = url.indexOf("//") + 2;
        return url.substring(0, url.indexOf('/', slashslash));
    }

    public static String getPathAndQuery(String url) {
        int slashslash = url.indexOf("//") + 2;
        return url.substring(url.indexOf('/', slashslash));
    }

    public static boolean isValid(String url) {
        try {
            new URL(url);

            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static boolean isValid(String url, String... protocols) {
        boolean isValid = isValid(url);

        if (isValid) {
            for (String protocol : protocols) {
                if (url.startsWith(protocol + "://")) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String joinRelative(String rootUrl, String toAdd) {
        try {
            return new URL(new URL(rootUrl), toAdd).toURI().normalize().toString();
        } catch (Throwable t) {
            return null;
        }
    }

    public static String joinIfRelative(String url, String root) {
        if (isRelative(url)) {
            return joinRelative(root, url);
        } else {
            return url;
        }
    }

    public static boolean isRelative(String url) {
        return !isValid(url);
    }
}

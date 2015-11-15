package io.belov.soyuz.mongo;

/**
 * Created by fbelov on 25.04.15.
 */
public class MongoUtils {

    public static void logDebug() {
        // Enable MongoDB logging in general
        System.setProperty("DEBUG.MONGO", "true");

        // Enable DB operation tracing
        System.setProperty("DB.TRACE", "true");
    }

}

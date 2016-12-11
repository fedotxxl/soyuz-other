package io.belov.soyuz.log;

import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Created by fbelov on 10.07.15.
 */
public class Slf4jUtils {

    //http://stackoverflow.com/questions/9117030/jul-to-slf4j-bridge
    public static void registerJulToSlf4j() {
        // Optionally remove existing handlers attached to j.u.l root logger
        SLF4JBridgeHandler.removeHandlersForRootLogger();  // (since SLF4J 1.6.5)

        // add SLF4JBridgeHandler to j.u.l's root logger, should be done once during
        // the initialization phase of your application
        SLF4JBridgeHandler.install();
    }

}

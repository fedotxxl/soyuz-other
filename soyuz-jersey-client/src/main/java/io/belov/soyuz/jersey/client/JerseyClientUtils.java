package io.belov.soyuz.jersey.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import io.belov.soyuz.json.JacksonFactory;
import io.belov.soyuz.log.LogbackUtils;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.filter.LoggingFilter;
import org.slf4j.event.Level;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

/**
 * Created by fbelov on 07.07.15.
 */
public class JerseyClientUtils {

    private static final String LOGGING_FILTER_CLASS_NAME = LoggingFilter.class.getCanonicalName();

    public static Client getClientWithJacksonSupport(int connectionTimeoutInMillis, int readTimeoutInMillis) {
        ObjectMapper objectMapper = JacksonFactory.createObjectMapper(
                JacksonFactory.Param.FAIL_SAFE,
                JacksonFactory.Param.UNDERSCORE_NAMING_CONVENTION
        );

        return getClientWithJacksonSupport(connectionTimeoutInMillis, readTimeoutInMillis, objectMapper);
    }

    public static Client getClientWithJacksonSupport(int connectionTimeoutInMillis, int readTimeoutInMillis, ObjectMapper objectMapper) {
        JacksonJsonProvider jacksonJsonProvider = new JacksonJaxbJsonProvider();

        jacksonJsonProvider.setMapper(objectMapper);

        Client client = ClientBuilder.newClient().register(jacksonJsonProvider);

        client.property(ClientProperties.CONNECT_TIMEOUT, connectionTimeoutInMillis);
        client.property(ClientProperties.READ_TIMEOUT, readTimeoutInMillis);

        return client;
    }

    /**
     * Enables/disables jersey logging. Don't forget to call Slf4jUtils.registerJulToSlf4j();
     */
    public static void logRequestResponse(boolean isEnabled) {
        if (isEnabled) {
            LogbackUtils.setLogLevelForPackages(Level.INFO, LOGGING_FILTER_CLASS_NAME);
        } else {
            LogbackUtils.setLogLevelForPackages(Level.WARN, LOGGING_FILTER_CLASS_NAME);
        }
    }

    public static boolean isSuccess(Response response) {
        return isFamily(response, Response.Status.Family.SUCCESSFUL);
    }

    private static boolean isFamily(Response response, Response.Status.Family family) {
        return response.getStatusInfo().getFamily() == family;
    }

}

package io.belov.soyuz.jersey.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import io.belov.soyuz.log.LoggerEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by fbelov on 10.07.15.
 */
public class LogNotSuccessResponseFilter implements ClientResponseFilter {

    private static final Logger log = LoggerFactory.getLogger(LogNotSuccessResponseFilter.class);
    private static final LoggerEvents loge = LoggerEvents.getInstance(log);

    private boolean includeBody = true;

    public LogNotSuccessResponseFilter() {
    }

    public LogNotSuccessResponseFilter(boolean includeBody) {
        this.includeBody = includeBody;
    }

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        if (responseContext.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            if (responseContext.getStatus() == 404) {
                loge.info("response.notSuccess", ImmutableMap.of("url", requestContext.getUri(), "code", 404));
            } else if (includeBody && log.isWarnEnabled()) {
                byte[] bytes = ByteStreams.toByteArray(responseContext.getEntityStream());

                loge.warn("response.notSuccess", ImmutableMap.of("url", requestContext.getUri(), "code", responseContext.getStatus(), "body", new String(bytes, "UTF-8")));

                responseContext.setEntityStream(new ByteArrayInputStream(bytes));
            } else {
                loge.warn("response.notSuccess", ImmutableMap.of("url", responseContext.getLocation(), "code", responseContext.getStatus()));
            }
        }
    }

}

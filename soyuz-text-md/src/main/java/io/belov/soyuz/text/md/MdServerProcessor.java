package io.belov.soyuz.text.md;

import io.thedocs.soyuz.is;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

/**
 * Created by fbelov on 20.01.16.
 */
public class MdServerProcessor {

    private static final Logger log = LoggerFactory.getLogger(MdServerProcessor.class);

    private String serverUrl;
    private Client client;

    public MdServerProcessor(String serverUrl) {
        if (is.tt(serverUrl)) {
            this.client = ClientBuilder.newClient();
            this.serverUrl = serverUrl;
        }
    }

    @Nullable
    public String markdownToHtml(String markdownSource) {
        try {
            if (client == null) {
                return null;
            } else {
                return client
                        .target(serverUrl)
                        .path("/")
                        .request(MediaType.TEXT_PLAIN_TYPE)
                        .post(Entity.entity(markdownSource, MediaType.TEXT_PLAIN_TYPE), String.class);
            }
        } catch (Exception e) {
            log.error("Exception on parsing md by md-server: " + e.toString());

            return null;
        }
    }

}

package io.belov.soyuz.mail;

import java.util.Map;

/**
 * Created by fbelov on 06.04.16.
 */
public interface MailTemplateResolverI {

    MailData resolve(String templateKey, Map params);

    class MailData {
        private String title;
        private String message;

        public MailData(String title, String message) {
            this.title = title;
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public String getTitle() {
            return title;
        }
    }

}

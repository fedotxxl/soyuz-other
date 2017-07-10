package io.belov.soyuz.mail;

import com.sun.mail.smtp.SMTPTransport;
import io.thedocs.soyuz.log.LoggerEvents;
import io.thedocs.soyuz.to;
import io.prometheus.client.Counter;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.Security;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * Created by fbelov on 06.04.16.
 */
public class MailService {

    private static final Counter mailCounter = Counter.build().name("mail_send").labelNames("key").help("Send mails by key").register();

    private static final LoggerEvents loge = LoggerEvents.getInstance(MailService.class);

    private MailTemplateResolverI mailTemplateResolver;
    private Properties connectionSettings;
    private String username;
    private String password;
    private boolean dryRun;

    public MailService(Properties connectionSettings, String username, String password, MailTemplateResolverI mailTemplateResolver) {
        this(connectionSettings, username, password, mailTemplateResolver, false);
    }

    public MailService(Properties connectionSettings, String username, String password, MailTemplateResolverI mailTemplateResolver, boolean dryRun) {
        this.connectionSettings = connectionSettings;
        this.username = username;
        this.password = password;
        this.dryRun = dryRun;
        this.mailTemplateResolver = mailTemplateResolver;
    }

    public void send(String from, String recipientEmail, String templateKey, Map templateParams) {
        Map context = to.map("template", templateKey, "to", recipientEmail);

        if (dryRun) {
            loge.info("mail.send", to.map(context, "params", templateParams));
        } else {
            loge.debug("mail.send", context);

            MailTemplateResolverI.MailData data = mailTemplateResolver.resolve(templateKey, templateParams);

            doSend(from, recipientEmail, templateKey, data.getTitle(), data.getMessage());
        }
    }

    public void send(String from, String recipientEmail, String templateKey, String title, String message) {
        Map context = to.map("template", templateKey, "to", recipientEmail);

        if (dryRun) {
            loge.info("mail.send", context);
        } else {
            loge.debug("mail.send", context);

            doSend(from, recipientEmail, templateKey, title, message);
        }
    }

    private void doSend(String from, String recipientEmail, String templateKey, String title, String message) {
        mailCounter.labels(templateKey).inc();

        if (!dryRun) {
            try {
                Mail.sendAsHtml(connectionSettings, username, password, from, recipientEmail, title, message);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @author doraemon
     */
    private static class Mail {
        private Mail() {
        }

        /**
         * Send email using GMail SMTP server.
         *
         * @param username       GMail username
         * @param password       GMail password
         * @param recipientEmail TO recipient
         * @param title          title of the message
         * @param message        message to be sent
         * @throws AddressException   if the email address parse failed
         * @throws MessagingException if the connection is dead or not in the connected state or if the message is not a MimeMessage
         */
        public static void send(Properties props, final String username, final String password, String from, String recipientEmail, String title, String message) throws AddressException, MessagingException {
            send(props, username, password, from, recipientEmail, "", title, message, false);
        }

        public static void sendAsHtml(Properties props, final String username, final String password, String from, String recipientEmail, String title, String message) throws AddressException, MessagingException {
            send(props, username, password, from, recipientEmail, "", title, message, true);
        }

        /**
         * Send email using GMail SMTP server.
         *
         * @param username       GMail username
         * @param password       GMail password
         * @param recipientEmail TO recipient
         * @param ccEmail        CC recipient. Can be empty if there is no CC recipient
         * @param title          title of the message
         * @param message        message to be sent
         * @throws AddressException   if the email address parse failed
         * @throws MessagingException if the connection is dead or not in the connected state or if the message is not a MimeMessage
         */
        public static void send(Properties props, final String username, final String password, String from, String recipientEmail, String ccEmail, String title, String message, boolean isHtml) throws AddressException, MessagingException {
            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            Session session = Session.getInstance(props, null);

            // -- Create a new message --
            final MimeMessage msg = new MimeMessage(session);

            // -- Set the FROM and TO fields --
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));

            if (ccEmail.length() > 0) {
                msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
            }

            msg.setSubject(title);
            msg.setSentDate(new Date());

            if (isHtml) {
                msg.setContent(message, "text/html");
            } else {
                msg.setText(message, "utf-8");
            }

            SMTPTransport t = (SMTPTransport) session.getTransport("smtps");

            t.connect(props.getProperty("mail.smtps.host"), username, password);
            t.sendMessage(msg, msg.getAllRecipients());
            t.close();
        }
    }
}

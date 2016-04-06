/*
 * GoogleMail
 * Copyright (c) 2012 Cybervision. All rights reserved.
 */
package io.belov.soyuz.mail;

import java.util.Properties;

public class GoogleMail {

    private static Properties props;

    static {
        // Get a Properties object
        props = System.getProperties();
        props.setProperty("mail.smtps.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.setProperty("mail.smtps.auth", "true");

        /*
        If set to false, the QUIT command is sent and the connection is immediately closed. If set
        to true (the default), causes the transport to wait for the response to the QUIT command.

        ref :   http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html
                http://forum.java.sun.com/thread.jspa?threadID=5205249
                smtpsend.java - demo program from javamail
        */
        props.put("mail.smtps.quitwait", "false");
    }

    public static Properties getConnectionSettings() {
        return props;
    }
}

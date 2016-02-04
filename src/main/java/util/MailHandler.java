package util;
import entities.MailTemplate;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by robin on 2016-01-11.
 */
public class MailHandler {
    private String host = "localhost";
    private MailTemplate inviteTemplate;
    private MailTemplate pwdResetTemplate;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public MailTemplate getInviteTemplate() {
        return inviteTemplate;
    }

    public void setInviteTemplate(MailTemplate inviteTemplate) {
        this.inviteTemplate = inviteTemplate;
    }

    public MailTemplate getPwdResetTemplate() {
        return pwdResetTemplate;
    }

    public void setPwdResetTemplate(MailTemplate pwdResetTemplate) {
        this.pwdResetTemplate = pwdResetTemplate;
    }

    public boolean sendMail(String to, String type, HashMap<String, String> values) {
        if (type.equals("invite")) {
            return sendMail(to, inviteTemplate.getSender(), inviteTemplate.renderSubject(values), inviteTemplate.renderMessage(values));
        } else if (type.equals("pwdReset")) {
            return sendMail(to, pwdResetTemplate.getSender(), pwdResetTemplate.renderSubject(values), pwdResetTemplate.renderMessage(values));
        }
        return false;
    }

    public boolean sendMail(String to, String from, String subject, String msg) {
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        try{
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject(subject);

            // Now set the actual message
            message.setText(msg);

            // Send message
            Transport.send(message);

            return true;
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }

        return false;
    }
}

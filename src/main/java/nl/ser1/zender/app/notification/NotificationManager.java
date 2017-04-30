package nl.ser1.zender.app.notification;

import nl.ser1.zender.app.NonDisclosedSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static nl.ser1.zender.app.NonDisclosedSettings.*;

/**
 * Created by Robbert on 30-04-17.
 */
public class NotificationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationManager.class);

    public static void main(String[] args) {
        String subject = "Test van WolkenZender";
        String body = "Test geslaagd!";
        new NotificationManager().sendEmail(subject, body, false);
    }

    public void sendEmail(String subject, String body, boolean includeLastImage) {

        LOGGER.info(String.format("Going to send email to [%s] with subject [%s]", RECIPIENT, subject));

        Properties props = System.getProperties();

        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.user", EMAIL_LOGIN_USERNAME);
        props.put("mail.smtp.password", EMAIL_LOGIN_PASSWORD);
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.auth", true);

        Session session = Session.getInstance(props);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(EMAIL_FROM_ADDRESS));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(RECIPIENT));

            message.setSubject(subject);
            message.setText(body);
            Transport transport = session.getTransport("smtp");
            transport.connect(HOST, EMAIL_LOGIN_USERNAME, EMAIL_LOGIN_PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

            LOGGER.info("Email send!");

        } catch (MessagingException e) {
            LOGGER.error("Mail error", e);
        }

    }


}

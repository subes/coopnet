package bugreportmailsender;

import java.security.Security;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class BugReportMailSender {

    public static synchronized void sendBugReportMail(String subject, String body) throws AddressException, MessagingException{	
        //Setup things
        String from = "coopnetbugs@gmail.com";
        String to = "coopnetredirect@gmail.com";

        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");
        
        //Connect
        Session session = Session.getDefaultInstance(props,
                        new javax.mail.Authenticator() 
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            { 
                return new PasswordAuthentication("coopnetbugs", "newS3nDBug_1227");	
            }
        });		

        //Create message
        MimeMessage message = new MimeMessage(session);
        message.setSender(new InternetAddress(from));
        message.setSubject(subject);
        message.setContent(body, "text/plain");
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));

        //Send
        Transport.send(message);
    }
}

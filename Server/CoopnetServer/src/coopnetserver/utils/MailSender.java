package coopnetserver.utils;

import java.security.Security;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public final class MailSender {

    private MailSender(){}

    public static void sendPasswordRecoveryMail(String loginname, String email, String userspassword){
        String subject = "Coopnet password recovery service - password reset";

        String body = "Hello "+loginname+"," +
                "\n" +
                "\nwe have reset your password according to your request." +
                "\n" +
                "\nYour new password is:" +
                "\n" +
                "\n\t"+userspassword +
                "\n" +
                "\nYou should now be able to login again." +
                "\n" +
                "\nThank you for using Coopnet!" +
                "\n" +
                "\n" +
                "\n" +
                "\nThis mail was generated by the Coopnet password recovery service.";

        String user = "coopnetbugs";
        String password = "newS3nDBug_1227";
        String from = "coopnet_noreply@example.com";
        sendMail(user, password, from, email, subject, body);
    }
    
    public static void sendBugReportMail(final String subject, final String body){
        String user = "coopnetbugs";
        String password = "newS3nDBug_1227";
        String from = "coopnetbugs@gmail.com";
        String to = "coopnetredirect@gmail.com";
        sendMail(user, password, from, to, subject, body);
    }
    
    
    private static void sendMail(final String user, final String password, final String from, final String to, final String subject, final String body){
        new ErrThread(){
            @Override
            public void handledRun() throws Throwable {
                synchronized(MailSender.class){
                     //Setup things
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
                                    new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication()
                        {
                            return new PasswordAuthentication(user, password);
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
        }.start();
    }
}
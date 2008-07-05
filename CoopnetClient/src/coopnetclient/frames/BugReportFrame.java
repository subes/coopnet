/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
                    Kovacs Zsolt (kovacs.zsolt.85@gmail.com)

    This file is part of Coopnet.

    Coopnet is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Coopnet is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Coopnet.  If not, see <http://www.gnu.org/licenses/>.
*/

package coopnetclient.frames;

import coopnetclient.Globals;
import coopnetclient.modules.Colorizer;
import java.security.Security;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;

public class BugReportFrame extends javax.swing.JFrame {
    
    private Exception exc;
    private String trafficLog;
    
    /** Creates new form BugReport */
    public BugReportFrame(Exception e, String trafficLog) {
        //Exception mode
        initComponents();
        this.exc=e;
        this.trafficLog = trafficLog;
    }
    
    public BugReportFrame() {
        //Message mode
        initComponents();
        Colorizer.colorize(this);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    //Returns the final report as a String
    private String compileReport(){
        //Date
        Date date = new Date();
        String report =   "Date:\n\t" + date.toGMTString();
        
        //EMail
        if(tf_email.getText().length() > 0){
            report += "\n\nReporters E-Mail:\n\t" + tf_email.getText();
        }
                        
        report += "\n\n******************************************************************************************\n";
        
        //Short desc
        report += "\nShort description: " +
                  "\n\t" + tf_shortDescription.getText() +
                  "\n";
        
        //Long desc
        report += "\nDetailed description: ";
        
        for(String line : ta_LongDescription.getText().split("\n")){
            report += "\n\t"+line;
        }
        
        report += "\n\n******************************************************************************************\n";
        
        if(exc != null){
            //Stacktrace
            report += "\nStacktrace: ";
            report += "\n\t"+exc.getClass().toString()+": "+exc.getMessage();

            StackTraceElement[] trace = exc.getStackTrace();
            for(int i = 0; i < trace.length; i++){
                report += "\n\t\tat "+trace[i].toString();
            }

            //Log
            report += "\n\nLog snippet:";

            for(String line : trafficLog.split("\n")){
                report += "\n\t"+line;
            }        
        }else{
            report += "\nThere is no Exception for this report.";
        }
        
        return report;
    }
    
    //Sends the bugreport as mail
    private void sendMail(String subject, String body) throws AddressException, MessagingException{	
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
                return new PasswordAuthentication("coopnetbugs", "sendBug_1228");	
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
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbl_header = new javax.swing.JLabel();
        lbl_email = new javax.swing.JLabel();
        tf_email = new javax.swing.JTextField();
        lbl_longDescription = new javax.swing.JLabel();
        scrl_longDescription = new javax.swing.JScrollPane();
        ta_LongDescription = new javax.swing.JTextArea();
        btn_send = new javax.swing.JButton();
        btn_cancel = new javax.swing.JButton();
        lbl_emailOptional = new javax.swing.JLabel();
        lbl_info = new javax.swing.JLabel();
        tf_shortDescription = new javax.swing.JTextField();
        lbl_shortDescription = new javax.swing.JLabel();
        btn_review = new javax.swing.JButton();
        lbl_note = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bug report");
        setResizable(false);

        lbl_header.setText("<HTML><h2>Report a bug:</h2>");

        lbl_email.setText("<HTML>Please give us your E-Mail address,<br>so we may contact you if we need further information:</HTML>");

        lbl_longDescription.setText("<HTML>Here you can get more in detail and tell us how to reproduce the error,<br>so we can actually get some clues about how to fix it:</HTML>");

        ta_LongDescription.setColumns(20);
        ta_LongDescription.setRows(5);
        scrl_longDescription.setViewportView(ta_LongDescription);

        btn_send.setText("Send");
        btn_send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_sendActionPerformed(evt);
            }
        });

        btn_cancel.setText("Cancel");
        btn_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelActionPerformed(evt);
            }
        });

        lbl_emailOptional.setText("(optional)");

        lbl_info.setText("<HTML>You can help the development of this project by submitting a report.<br>Only known problems can be fixed and we appreciate your help.</HTML>");

        lbl_shortDescription.setText("<HTML>Write a short, but proper description of the problem:</HTML>");

        btn_review.setText("Review this report");
        btn_review.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_reviewActionPerformed(evt);
            }
        });

        lbl_note.setText("<html><b>Note:</b> You can review your final report with the review button!");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(tf_email, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_emailOptional))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lbl_longDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lbl_shortDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lbl_email, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGap(12, 12, 12)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(btn_send)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btn_cancel)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 239, Short.MAX_VALUE)
                                    .addComponent(btn_review))
                                .addGroup(layout.createSequentialGroup()
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(lbl_header))))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(scrl_longDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(tf_shortDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 454, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(lbl_note))
                            .addComponent(lbl_info, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_header, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_info)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_note)
                .addGap(25, 25, 25)
                .addComponent(lbl_email)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_emailOptional))
                .addGap(18, 18, 18)
                .addComponent(lbl_shortDescription)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tf_shortDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(lbl_longDescription)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrl_longDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_cancel)
                    .addComponent(btn_send)
                    .addComponent(btn_review))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelActionPerformed
        this.setVisible(false);
        this.dispose();
}//GEN-LAST:event_btn_cancelActionPerformed

    private void btn_reviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_reviewActionPerformed
        Globals.openTextPreviewFrame("Review bugreport", compileReport());
}//GEN-LAST:event_btn_reviewActionPerformed

    private void btn_sendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_sendActionPerformed

        if(tf_shortDescription.getText().length() < 1 || ta_LongDescription.getText().length() < 1){
            JOptionPane.showMessageDialog(null,
                        "Sorry, but please take some time to fill out the " +
                      "\nshort and detailed description fields." +
                      "\nWe can't guess what has happened if you don't tell us anything.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
        }else{
            boolean error = false;

            try{
                sendMail("BUGREPORT: "+tf_shortDescription.getText(), compileReport());
            }catch(AddressException e){
                error = true;
            }catch(MessagingException e){
                error = true;
            }

            if(error){
                JOptionPane.showMessageDialog(null,
                          "Sending bugreport failed!" +
                        "\nPlease verify that you are " +
                        "\nconnected to the internet " +
                        "\nand try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(null,
                        "Your bugreport was sent successfully,\nthank you for your help!",
                        "Success", JOptionPane.PLAIN_MESSAGE);
                
                Globals.getClientFrame().removeAllTabs();
                this.setVisible(false);
                this.dispose();
            }
        }
}//GEN-LAST:event_btn_sendActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_cancel;
    private javax.swing.JButton btn_review;
    private javax.swing.JButton btn_send;
    private javax.swing.JLabel lbl_email;
    private javax.swing.JLabel lbl_emailOptional;
    private javax.swing.JLabel lbl_header;
    private javax.swing.JLabel lbl_info;
    private javax.swing.JLabel lbl_longDescription;
    private javax.swing.JLabel lbl_note;
    private javax.swing.JLabel lbl_shortDescription;
    private javax.swing.JScrollPane scrl_longDescription;
    private javax.swing.JTextArea ta_LongDescription;
    private javax.swing.JTextField tf_email;
    private javax.swing.JTextField tf_shortDescription;
    // End of variables declaration//GEN-END:variables
    
}

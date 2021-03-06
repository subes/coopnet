/*  Copyright 2007  Edwin Stang (edwinstang@gmail.com),
 *                  Kovacs Zsolt (kovacs.zsolt.85@gmail.com)
 *
 *  This file is part of Coopnet.
 *
 *  Coopnet is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Coopnet is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Coopnet.  If not, see <http://www.gnu.org/licenses/>.
 */

package coopnetclient.frames;

import bugreportmailsender.BugReportMailSender;
import coopnetclient.Client;
import coopnetclient.Globals;
import coopnetclient.frames.clientframetabs.TabOrganizer;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.utils.ui.Colorizer;
import coopnetclient.utils.Logger;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Date;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

public class BugReportFrame extends javax.swing.JFrame {

    private Throwable exc;
    private String trafficLog;

    /** Creates new form BugReport */
    public BugReportFrame(Throwable e, String trafficLog) {
        //Exception mode
        initComponents();
        this.exc = e;
        this.trafficLog = trafficLog;
        AbstractAction act = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                btn_cancel.doClick();
            }
        };
        getRootPane().getActionMap().put("close", act);
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
    }

    public BugReportFrame() {
        //Message mode
        initComponents();
        Colorizer.colorize(this);
        setLocationRelativeTo(null);
        setVisible(true);

        this.trafficLog = Logger.getEndOfLog();

        AbstractAction act = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                btn_cancel.doClick();
            }
        };
        getRootPane().getActionMap().put("close", act);
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
    }

    //Returns the final report as a String
    private String compileReport() {
        //Date
        Date date = new Date();
        String report = "Date:" +
                "\n\t" + date.toLocaleString() +
                "\n\t" + date.toGMTString();

        report += "\n\nClient version:\n\t" + Globals.getClientVersion();

        report += "\n\nProtocol version:\n\t" + Globals.getCompatibilityVersion();

        report += "\n\nJava version: \n\t" + System.getProperty("java.vm.name")+ " " + System.getProperty("java.runtime.version");

        String osName = System.getProperty("os.name");
        report += "\n\nOperating System: \n\t" + osName;
        if(!osName.toUpperCase().equals(Globals.getOperatingSystem().toString())){
            report += " ("+Globals.getOperatingSystem().toString()+")";
        }

        report += "\n\nCountry and Language codes:\n\t" + System.getProperty("user.country") + "-" + System.getProperty("user.language");

        report += "\n\nHamachi:\n\t" + ((Client.getInterfaceAddress(Globals.HAMACHI_INTERFACE_NAME).length()>0)?"found":"not found");
        report += "\n\nTunngle:\n\t" + ((Client.getInterfaceAddress(Globals.TUNNGLE_INTERFACE_NAME).length()>0)?"found":"not found");

        //EMail
        if (tf_email.getText().length() > 0) {
            report += "\n\nReporters E-Mail:\n\t" + tf_email.getText();
        }

        report += "\n\n******************************************************************************************\n";

        //Short desc
        report += "\nShort description: " +
                "\n\t" + tf_shortDescription.getText() +
                "\n";

        //Long desc
        report += "\nDetailed description: ";

        for (String line : ta_LongDescription.getText().split("\n")) {
            report += "\n\t" + line;
        }

        report += "\n\n******************************************************************************************\n";

        if (exc != null) {
            //Stacktrace
            report += "\nException that caused this report: ";
            report += "\n\t" + exc.getClass().toString() + ": " + exc.getMessage();

            StackTraceElement[] trace = exc.getStackTrace();
            for (int i = 0; i < trace.length; i++) {
                report += "\n\t\tat " + trace[i].toString();
            }

            if (exc.getCause() != null) {
                report += "\nThis exception has a cause, look at the log snippet to see it.";
            }
        } else {
            report += "\nThere is no Exception for this report.";
        }

        //Log
        report += "\n\nLog snippet:";

        for (String line : trafficLog.split("\n")) {
            report += "\n\t" + line;
        }

        return report;
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

        setTitle("Bug report");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        lbl_header.setText("<HTML><h2>Report a bug:</h2>");

        lbl_email.setText("<HTML>Please give us your E-Mail address,<br>so we may contact you if we need further information:</HTML>");

        tf_email.setNextFocusableComponent(tf_shortDescription);

        lbl_longDescription.setText("<HTML>Here you can get more in detail and tell us how to reproduce the error,<br>so we can actually get some clues about how to fix it:</HTML>");

        ta_LongDescription.setColumns(20);
        ta_LongDescription.setRows(5);
        ta_LongDescription.setNextFocusableComponent(btn_send);
        scrl_longDescription.setViewportView(ta_LongDescription);

        btn_send.setText("Send");
        btn_send.setNextFocusableComponent(btn_cancel);
        btn_send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_sendActionPerformed(evt);
            }
        });

        btn_cancel.setText("Cancel");
        btn_cancel.setNextFocusableComponent(btn_review);
        btn_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelActionPerformed(evt);
            }
        });

        lbl_emailOptional.setText("(optional)");

        lbl_info.setText("<HTML>You can help the development of this project by submitting a report.<br>Only known problems can be fixed and we appreciate your help.</HTML>");

        tf_shortDescription.setNextFocusableComponent(ta_LongDescription);

        lbl_shortDescription.setText("<HTML>Write a short, but proper description of the problem:</HTML>");

        btn_review.setMnemonic(KeyEvent.VK_R);
        btn_review.setText("Review this report");
        btn_review.setNextFocusableComponent(tf_email);
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
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btn_send)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_cancel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 245, Short.MAX_VALUE)
                                .addComponent(btn_review))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbl_header))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(lbl_note))
                            .addComponent(lbl_info, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(scrl_longDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(tf_shortDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)))
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

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_cancel, btn_review, btn_send});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelActionPerformed
        FrameOrganizer.closeBugReportFrame();
}//GEN-LAST:event_btn_cancelActionPerformed

    private void btn_reviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_reviewActionPerformed
        FrameOrganizer.openTextPreviewFrame("Review bugreport", compileReport());
}//GEN-LAST:event_btn_reviewActionPerformed

    private void btn_sendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_sendActionPerformed

        if (tf_shortDescription.getText().length() < 1 || ta_LongDescription.getText().length() < 1) {
            JOptionPane.showMessageDialog(FrameOrganizer.getBugReportFrame(),
                    "Sorry, but please take some time to fill out the " +
                    "\nshort and detailed description fields." +
                    "\nWe can't guess what has happened if you don't tell us anything.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            boolean error = false;

            try {
                BugReportMailSender.sendBugReportMail("CLIENT BUGREPORT: " + tf_shortDescription.getText(), compileReport());
            } catch (AddressException e) {
                error = true;
            } catch (MessagingException e) {
                error = true;
            }

            if (error) {
                JOptionPane.showMessageDialog(FrameOrganizer.getBugReportFrame(),
                        "Sending bugreport failed!" +
                        "\nPlease verify that you are " +
                        "\nconnected to the internet " +
                        "\nand try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(FrameOrganizer.getBugReportFrame(),
                        "Your bugreport was sent successfully,\nthank you for your help!",
                        "Success", JOptionPane.PLAIN_MESSAGE);

                //TabOrganizer.closeAllTabs();
                TabOrganizer.closeErrorPanel();
                FrameOrganizer.closeBugReportFrame();
            }
        }
}//GEN-LAST:event_btn_sendActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        FrameOrganizer.closeBugReportFrame();
    }//GEN-LAST:event_formWindowClosing

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

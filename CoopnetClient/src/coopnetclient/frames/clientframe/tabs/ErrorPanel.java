/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
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

package coopnetclient.frames.clientframe.tabs;

import coopnetclient.Globals;
import coopnetclient.enums.ErrorPanelStyle;
import coopnetclient.frames.clientframe.ClosableTab;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.utils.FrameIconFlasher;
import coopnetclient.utils.Logger;
import java.awt.event.KeyEvent;

public class ErrorPanel extends javax.swing.JPanel implements ClosableTab{
    
    private Exception exception;
    private String trafficLog;

    //Message constants
    private static final String CONNECTION_REFUSED = "<HTML><p style=\"text-align: center;\"><b>Unable to connect to the server!</b><BR>" +
            "Please make sure you are connected to the internet and Coopnet is allowed to access it.<BR>" +
            "If you still cannot connect, the server may be down for maintenance, please try again later.</p> ";
    private static final String UNKNOWN = "<HTML><p style=\"text-align: center;\"><b>An unknown error occured!</b><BR>" +
            "Please help us fix this problem by sending a bug report.</p>";
    private static final String CONNECTION_RESET = "<HTML><p style=\"text-align: center;\"><b>Connection to the server was lost!</b><BR></p>";
    private static final String UNKNOWN_IO = "<HTML><p style=\"text-align: center;\"><b>An unknown IO error occured!</b><BR></p>";
    
    public ErrorPanel(ErrorPanelStyle mode, Exception exception) {
        initComponents();
        FrameIconFlasher.flash("data/icons/error.png", "An error occured!", true);
        
        coopnetclient.utils.Colorizer.colorize(this);
        switch (mode) {
            case UNKNOWN: {
                lbl_errorText.setText(UNKNOWN);
                break;
            }
            case CONNECTION_REFUSED: {
                lbl_errorText.setText(CONNECTION_REFUSED);
                btn_report.setVisible(false);
                break;
            }
            case CONNECTION_RESET: {
                lbl_errorText.setText(CONNECTION_RESET);
                btn_report.setVisible(false);
                break;
            }
            case UNKNOWN_IO: {
                lbl_errorText.setText(UNKNOWN_IO + exception.getMessage());
                btn_report.setVisible(false);
                break;
            }
        }
        
        if(btn_report.isVisible()){
            this.exception = exception;
            this.trafficLog = Logger.getEndOfLog();
        }
    }
    
    public boolean hasException(){
        if(exception != null){
            return true;
        }else{
            return false;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnl_top = new javax.swing.JPanel();
        lbl_errorText = new javax.swing.JLabel();
        pnl_button = new javax.swing.JPanel();
        btn_report = new javax.swing.JButton();

        setFocusable(false);
        setLayout(new java.awt.GridBagLayout());

        pnl_top.setFocusable(false);
        pnl_top.setLayout(new java.awt.GridBagLayout());

        lbl_errorText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_errorText.setText("<html><p style=\"text-align: center\"><b>Error header!</b><br>Error description, error description, error description.</p></html>");
        lbl_errorText.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        lbl_errorText.setFocusable(false);
        lbl_errorText.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lbl_errorText.setIconTextGap(0);
        lbl_errorText.setMaximumSize(new java.awt.Dimension(1000, 1000));
        lbl_errorText.setMinimumSize(new java.awt.Dimension(100, 100));
        lbl_errorText.setName("text"); // NOI18N
        lbl_errorText.setPreferredSize(new java.awt.Dimension(100, 100));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnl_top.add(lbl_errorText, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.4;
        add(pnl_top, gridBagConstraints);

        pnl_button.setFocusable(false);
        pnl_button.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 10));

        btn_report.setMnemonic(KeyEvent.VK_R);
        btn_report.setText("Report this bug");
        btn_report.setAlignmentX(0.5F);
        btn_report.setAlignmentY(0.0F);
        btn_report.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_report.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        btn_report.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_reportActionPerformed(evt);
            }
        });
        pnl_button.add(btn_report);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.6;
        add(pnl_button, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_reportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_reportActionPerformed
        Globals.openBugReportFrame(exception, trafficLog);
}//GEN-LAST:event_btn_reportActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_report;
    private javax.swing.JLabel lbl_errorText;
    private javax.swing.JPanel pnl_button;
    private javax.swing.JPanel pnl_top;
    // End of variables declaration//GEN-END:variables

    @Override
    public void closeTab() {
        TabOrganizer.closeErrorPanel();
    }
    
}
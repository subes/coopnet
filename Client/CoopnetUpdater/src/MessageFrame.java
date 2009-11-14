
import java.awt.CardLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

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
public class MessageFrame extends javax.swing.JFrame {

    private static long fullDownloadSize = 0;
    private static CardLayout cards;
    private static javax.swing.JFrame me;

    /** Creates new form MessageFrame */
    public MessageFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("SystemLAF not found! Will revert to CrossPlatformLAF.");
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception exc) {
                System.err.println("CrossPlatformLAF not found!?");
                ex.printStackTrace();
            }
        }
        initComponents();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        cards = (CardLayout) getContentPane().getLayout();
        me = this;
    }

    public static void setMessage(String message) {
        cards.show(me.getContentPane(), "messagePanel");
        lbl_message.setText(message);
    }

    public static void startedDownload() {
        cards.show(me.getContentPane(), "downloadPanel");
    }

    public static void setTotalDownloadSize(long fullSize) {
        fullDownloadSize = fullSize;
    }

    public static void setDownloadProgress(final long bytesread) {
        SwingUtilities.invokeLater(
            new Runnable() {

                @Override
                public void run() {
                    lbl_progress.setText(bytesread + " / " + fullDownloadSize + " bytes");
                    downloadProgress.setValue((int) (( (1.0 * bytesread ) / fullDownloadSize) * 100));
                }
            });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_downloadPanel = new javax.swing.JPanel();
        lbl_downloadMessage = new javax.swing.JLabel();
        downloadProgress = new javax.swing.JProgressBar();
        lbl_progress = new javax.swing.JLabel();
        pnl_messagePanel = new javax.swing.JPanel();
        lbl_message = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Coopnet Updater");
        getContentPane().setLayout(new java.awt.CardLayout());

        lbl_downloadMessage.setText(" Downloading update:");

        lbl_progress.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_progress.setText("0 / ??? bytes");

        javax.swing.GroupLayout pnl_downloadPanelLayout = new javax.swing.GroupLayout(pnl_downloadPanel);
        pnl_downloadPanel.setLayout(pnl_downloadPanelLayout);
        pnl_downloadPanelLayout.setHorizontalGroup(
            pnl_downloadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_downloadPanelLayout.createSequentialGroup()
                .addGroup(pnl_downloadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_downloadPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(downloadProgress, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE))
                    .addGroup(pnl_downloadPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lbl_progress, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(lbl_downloadMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
        );
        pnl_downloadPanelLayout.setVerticalGroup(
            pnl_downloadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_downloadPanelLayout.createSequentialGroup()
                .addComponent(lbl_downloadMessage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(downloadProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_progress)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(pnl_downloadPanel, "downloadPanel");

        lbl_message.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_message.setText("<html><b>Updating Coopnet<br>please wait...");

        javax.swing.GroupLayout pnl_messagePanelLayout = new javax.swing.GroupLayout(pnl_messagePanel);
        pnl_messagePanel.setLayout(pnl_messagePanelLayout);
        pnl_messagePanelLayout.setHorizontalGroup(
            pnl_messagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbl_message, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
        );
        pnl_messagePanelLayout.setVerticalGroup(
            pnl_messagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbl_message, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
        );

        getContentPane().add(pnl_messagePanel, "messagePanel");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JProgressBar downloadProgress;
    private javax.swing.JLabel lbl_downloadMessage;
    private static javax.swing.JLabel lbl_message;
    private static javax.swing.JLabel lbl_progress;
    private javax.swing.JPanel pnl_downloadPanel;
    private javax.swing.JPanel pnl_messagePanel;
    // End of variables declaration//GEN-END:variables
}
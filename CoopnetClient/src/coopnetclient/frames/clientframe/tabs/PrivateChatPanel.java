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

import coopnetclient.protocol.out.Protocol;
import coopnetclient.enums.ChatStyles;
import coopnetclient.enums.MuteBanStatuses;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.utils.MuteBanList;
import coopnetclient.frames.listeners.ChatInputKeyListener;
import coopnetclient.frames.listeners.HyperlinkMouseListener;
import javax.swing.text.StyledDocument;

public class PrivateChatPanel extends javax.swing.JPanel {

    private String partner;

    /** Creates new form PrivateChatPanel */
    public PrivateChatPanel(String partner) {
        this.partner = partner;
        initComponents();
        coopnetclient.utils.Colorizer.colorize(this);

        tp_chatInput.addKeyListener(new ChatInputKeyListener(2, partner));
        tp_chatOutput.addMouseListener(new HyperlinkMouseListener());
        
        updateMuteBanStatus();
    }
    
    
    public String getPartner(){
        return partner;
    }
    
    public void updateMuteBanStatus(){
        MuteBanStatuses status = MuteBanList.getMuteBanStatus(partner);
        if (status == null) {
            btn_mute.setText("Mute");
        } else if( status == MuteBanStatuses.MUTED ) {
            btn_mute.setText("UnMute");
        }
    }

    @Override
    public void requestFocus() {
        tp_chatInput.requestFocus();
    }

    public void customCodeForColorizer() {
        if (coopnetclient.utils.Settings.getColorizeText()) {
            tp_chatInput.setForeground(coopnetclient.utils.Settings.getUserMessageColor());
        }

        //Fix color of current/next input
        if (tp_chatInput.getText().length() > 0) {
            tp_chatInput.setText(tp_chatInput.getText());
        } else {
            tp_chatInput.setText("\n");
            tp_chatInput.setText("");
        }

        if (coopnetclient.utils.Settings.getColorizeBody()) {
            tp_chatOutput.setBackground(coopnetclient.utils.Settings.getBackgroundColor());
        }
    }

    public void append(String sender, String message, ChatStyles style) {
        StyledDocument doc = tp_chatOutput.getStyledDocument();
        coopnetclient.utils.ColoredChatHandler.addColoredText(sender,
                message, style,
                doc, scrl_chatOutput, tp_chatOutput);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_close = new javax.swing.JButton();
        sp_chatVertical = new javax.swing.JSplitPane();
        scrl_chatOutput = new javax.swing.JScrollPane();
        tp_chatOutput = new javax.swing.JTextPane();
        scrl_chatInput = new javax.swing.JScrollPane();
        tp_chatInput = new javax.swing.JTextPane();
        btn_mute = new javax.swing.JButton();

        setFocusable(false);

        btn_close.setText("Close");
        btn_close.setAlignmentX(0.5F);
        btn_close.setFocusable(false);
        btn_close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close(evt);
            }
        });

        sp_chatVertical.setBorder(null);
        sp_chatVertical.setDividerSize(3);
        sp_chatVertical.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        sp_chatVertical.setResizeWeight(1.0);
        sp_chatVertical.setFocusable(false);

        scrl_chatOutput.setFocusable(false);
        scrl_chatOutput.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                scrl_chatOutputComponentResized(evt);
            }
        });

        tp_chatOutput.setEditable(false);
        tp_chatOutput.setFocusCycleRoot(false);
        tp_chatOutput.setMinimumSize(new java.awt.Dimension(6, 24));
        tp_chatOutput.setNextFocusableComponent(tp_chatInput);
        tp_chatOutput.setPreferredSize(new java.awt.Dimension(6, 24));
        tp_chatOutput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tp_chatOutputKeyTyped(evt);
            }
        });
        scrl_chatOutput.setViewportView(tp_chatOutput);

        sp_chatVertical.setLeftComponent(scrl_chatOutput);

        scrl_chatInput.setFocusable(false);

        tp_chatInput.setMinimumSize(new java.awt.Dimension(6, 24));
        tp_chatInput.setNextFocusableComponent(tp_chatInput);
        scrl_chatInput.setViewportView(tp_chatInput);

        sp_chatVertical.setRightComponent(scrl_chatInput);

        btn_mute.setText("Mute");
        btn_mute.setFocusable(false);
        btn_mute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_muteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_chatVertical, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(208, 208, 208)
                .addComponent(btn_mute)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_close))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_close)
                    .addComponent(btn_mute))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_chatVertical, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_close, btn_mute});

    }// </editor-fold>//GEN-END:initComponents

    private void close(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close
        TabOrganizer.closePrivateChatPanel(this);
    }//GEN-LAST:event_close

private void tp_chatOutputKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tp_chatOutputKeyTyped
    char c = evt.getKeyChar();
    if (!evt.isControlDown()) {
        tp_chatInput.setText(tp_chatInput.getText() + c);
        tp_chatInput.requestFocus();
    }
}//GEN-LAST:event_tp_chatOutputKeyTyped

private void btn_muteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_muteActionPerformed
    if (btn_mute.getText().equals("Mute")) {
            Protocol.mute(partner);
        } else if (btn_mute.getText().equals("UnMute")) {
            Protocol.unMute(partner);
        } 
}//GEN-LAST:event_btn_muteActionPerformed

private void scrl_chatOutputComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_scrl_chatOutputComponentResized
    int start, end;
    start = tp_chatOutput.getSelectionStart();
    end = tp_chatOutput.getSelectionEnd();
    tp_chatOutput.setSelectionStart(start-1);
    tp_chatOutput.setSelectionEnd(end-1);
    tp_chatOutput.setSelectionStart(start);
    tp_chatOutput.setSelectionEnd(end);
}//GEN-LAST:event_scrl_chatOutputComponentResized

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_close;
    private javax.swing.JButton btn_mute;
    private javax.swing.JScrollPane scrl_chatInput;
    private javax.swing.JScrollPane scrl_chatOutput;
    private javax.swing.JSplitPane sp_chatVertical;
    private javax.swing.JTextPane tp_chatInput;
    private javax.swing.JTextPane tp_chatOutput;
    // End of variables declaration//GEN-END:variables
}

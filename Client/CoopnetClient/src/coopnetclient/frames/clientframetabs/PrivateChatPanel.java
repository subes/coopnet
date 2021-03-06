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
package coopnetclient.frames.clientframetabs;

import coopnetclient.protocol.out.Protocol;
import coopnetclient.enums.ChatStyles;
import coopnetclient.enums.MuteBanStatuses;
import coopnetclient.frames.components.HistoryLogger;
import coopnetclient.frames.interfaces.ClosableTab;
import coopnetclient.utils.MuteBanList;
import coopnetclient.frames.listeners.ChatInputKeyListener;
import java.awt.event.KeyEvent;
import coopnetclient.Globals;
import coopnetclient.utils.settings.Settings;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrivateChatPanel extends javax.swing.JPanel implements ClosableTab {

    private String partner;
    private ChatInputKeyListener keyListener;
    private HistoryLogger history;

    /** Creates new form PrivateChatPanel */
    public PrivateChatPanel(String partner) throws IOException {
        this.partner = partner;
        initComponents();
        history = new HistoryLogger(Globals.getThisPlayerLoginName(),partner);
        scrl_chatOutput.updateStyle();
        coopnetclient.utils.ui.Colorizer.colorize(this);

        keyListener = new ChatInputKeyListener(ChatInputKeyListener.PRIVATE_CHAT_MODE, partner);
        tp_chatInput.addKeyListener(keyListener);
        if(Settings.getShowHistoryLog()){
            if(!history.isEmpty()){
                List<String> messages = history.printall();
                for(int i=0;i<messages.size();i++){
                    scrl_chatOutput.printChatMessage("", messages.get(i), ChatStyles.HISTORY);
                }
            }
        }
            
        
        scrl_chatOutput.getTextPane().addKeyListener(new java.awt.event.KeyAdapter() {

            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!evt.isControlDown()) {
                    tp_chatInput.setText(tp_chatInput.getText() + c);
                    tp_chatInput.requestFocusInWindow();
                    scrl_chatOutput.getTextPane().setSelectionStart(scrl_chatOutput.getTextPane().getDocument().getLength());
                    scrl_chatOutput.getTextPane().setSelectionEnd(scrl_chatOutput.getTextPane().getDocument().getLength());
                }
            }
        });

        updateMuteBanStatus();
    }
    
    
    public String getPartner(){
        return partner;
    }

    public void setPartner(String name){
        partner = name;
        keyListener.setPrefix(partner);
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
        tp_chatInput.requestFocusInWindow();
    }

    public void customCodeForColoring() {
        if (coopnetclient.utils.settings.Settings.getColorizeText()) {
            tp_chatInput.setForeground(coopnetclient.utils.settings.Settings.getUserMessageColor());
        }

        //Fix color of current/next input
        if (tp_chatInput.getText().length() > 0) {
            tp_chatInput.setText(tp_chatInput.getText());
        } else {
            tp_chatInput.setText("\n");
            tp_chatInput.setText("");
        }

        if (coopnetclient.utils.settings.Settings.getColorizeBody()) {
            scrl_chatOutput.getTextPane().setBackground(coopnetclient.utils.settings.Settings.getBackgroundColor());
        }
    }
    
    
    

    public void append(String sender, String message, ChatStyles style){
        scrl_chatOutput.printChatMessage(sender, message, style);
        if(Settings.getShowHistoryLog()){
            try {
                history.addMessage(sender+": "+message);
            } catch (IOException ex) {
                Logger.getLogger(PrivateChatPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
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

        sp_chatVertical = new javax.swing.JSplitPane();
        pnl_chatInput = new javax.swing.JPanel();
        btn_mute = new javax.swing.JButton();
        scrl_chatInput = new javax.swing.JScrollPane();
        tp_chatInput = new javax.swing.JTextPane();
        scrl_chatOutput = new coopnetclient.frames.components.ChatOutput();

        setFocusable(false);

        sp_chatVertical.setBorder(null);
        sp_chatVertical.setDividerSize(3);
        sp_chatVertical.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        sp_chatVertical.setResizeWeight(1.0);
        sp_chatVertical.setFocusable(false);

        pnl_chatInput.setLayout(new java.awt.GridBagLayout());

        btn_mute.setMnemonic(KeyEvent.VK_M);
        btn_mute.setText("Mute");
        btn_mute.setFocusable(false);
        btn_mute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_muteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        pnl_chatInput.add(btn_mute, gridBagConstraints);

        scrl_chatInput.setFocusable(false);

        tp_chatInput.setMinimumSize(new java.awt.Dimension(6, 24));
        tp_chatInput.setNextFocusableComponent(tp_chatInput);
        scrl_chatInput.setViewportView(tp_chatInput);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnl_chatInput.add(scrl_chatInput, gridBagConstraints);

        sp_chatVertical.setRightComponent(pnl_chatInput);
        sp_chatVertical.setLeftComponent(scrl_chatOutput);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_chatVertical, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_chatVertical, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

private void btn_muteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_muteActionPerformed
    if (btn_mute.getText().equals("Mute")) {
            Protocol.mute(partner);
        } else if (btn_mute.getText().equals("UnMute")) {
            Protocol.unMute(partner);
        } 
}//GEN-LAST:event_btn_muteActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_mute;
    private javax.swing.JPanel pnl_chatInput;
    private javax.swing.JScrollPane scrl_chatInput;
    private coopnetclient.frames.components.ChatOutput scrl_chatOutput;
    private javax.swing.JSplitPane sp_chatVertical;
    private javax.swing.JTextPane tp_chatInput;
    // End of variables declaration//GEN-END:variables

    @Override
    public void closeTab() {
        TabOrganizer.closePrivateChatPanel(this);
    }

    @Override
    public boolean isCurrentlyClosable() {
        return true;
    }
    
    public void updateHighlights(){
        scrl_chatOutput.updateHighlights();
    }

    public void updateStyle(){
        scrl_chatOutput.updateStyle();
    }
}

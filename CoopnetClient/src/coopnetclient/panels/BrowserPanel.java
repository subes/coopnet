/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
                    Kovacs Zolt (kovacs.zsolt.85@gmail.com)

    This file is part of CoopNet.

    CoopNet is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    CoopNet is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CoopNet.  If not, see <http://www.gnu.org/licenses/>.
*/

package coopnetclient.panels;

import coopnetclient.Client;
import coopnetclient.coloring.Colorizer;
import java.io.IOException;

public class BrowserPanel extends javax.swing.JPanel {

    public static String url = "http://kecske85.valodi.hu/coopnet/guide.html";

    /** Creates new form browser */
    public BrowserPanel() {
        initComponents();
        Colorizer.colorize(this);
        try {
            tp_browser.setPage(url);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrl_browser = new javax.swing.JScrollPane();
        tp_browser = new javax.swing.JTextPane();
        btn_close = new javax.swing.JButton();

        tp_browser.setEditable(false);
        scrl_browser.setViewportView(tp_browser);

        btn_close.setText("Close");
        btn_close.setAlignmentX(0.5F);
        btn_close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_closeclose(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_close))
            .addComponent(scrl_browser, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btn_close)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrl_browser, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void btn_closeclose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_closeclose
    Client.mainFrame.removeGuideTab();
}//GEN-LAST:event_btn_closeclose
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_close;
    private javax.swing.JScrollPane scrl_browser;
    private javax.swing.JTextPane tp_browser;
    // End of variables declaration//GEN-END:variables
}

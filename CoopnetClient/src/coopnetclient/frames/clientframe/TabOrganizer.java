/*	
Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
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

package coopnetclient.frames.clientframe;

import coopnetclient.ErrorHandler;
import coopnetclient.Globals;
import coopnetclient.enums.ChatStyles;
import coopnetclient.frames.clientframe.panels.BrowserPanel;
import coopnetclient.frames.clientframe.panels.ChannelPanel;
import coopnetclient.frames.clientframe.panels.ErrorPanel;
import coopnetclient.frames.clientframe.panels.FileTransferRecievePanel;
import coopnetclient.frames.clientframe.panels.FileTransferSendPanel;
import coopnetclient.frames.clientframe.panels.LoginPanel;
import coopnetclient.frames.clientframe.panels.PrivateChatPanel;
import coopnetclient.frames.clientframe.panels.RoomPanel;
import coopnetclient.frames.clientframe.panels.TestGameDataEditor;
import coopnetclient.modules.ColoredChatHandler;
import coopnetclient.modules.Settings;
import coopnetclient.modules.listeners.TabbedPaneColorChangeListener;
import coopnetclient.utils.gamedatabase.GameDatabase;
import java.awt.Component;
import java.io.File;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;

public class TabOrganizer {

    private static JTabbedPane tabHolder;
    
    private static Vector<ChannelPanel> channelPanels = new Vector<ChannelPanel>();
    private static RoomPanel roomPanel;
    private static Vector<PrivateChatPanel> privateChatPanels = new Vector<PrivateChatPanel>();
    private static BrowserPanel browserPanel;
    private static ErrorPanel errorPanel;
    private static LoginPanel loginPanel;
    private static Component gamedataeditor;
    private static Vector<FileTransferSendPanel> fileTransferSendPanels = new Vector<FileTransferSendPanel>();
    private static Vector<FileTransferRecievePanel> fileTransferReceivePanels = new Vector<FileTransferRecievePanel>();
    
    static{
        tabHolder = Globals.getClientFrame().getTabHolder();    
    }
    
    public static void openChannelPanel(String channelname) {
        int index = -1;
        index = tabHolder.indexOfTab(channelname);
        if (index != -1) {
            return;
        }
        ChannelPanel currentchannel = new ChannelPanel(channelname);
        tabHolder.add(currentchannel, 0);
        tabHolder.setTitleAt(0, channelname);
        channelPanels.add(currentchannel);
        Globals.getClientFrame().repaint();
        //check if the game is installed
        if (!Globals.getLauncher().isLaunchable(channelname)) {
            currentchannel.disablebuttons();
            currentchannel.printMainChatMessage("SYSTEM",
                    "The game couldn't be detected, please set the path manually at " +
                    "options/manage games to enable playing this game!",
                    ChatStyles.SYSTEM);
        } else {
            currentchannel.setLaunchable(true);
        }
        if (GameDatabase.isBeta(channelname)) {
            currentchannel.printMainChatMessage("SYSTEM", "Support for this game is experimental," +
                    " email coopnetbugs@gmail.com if you have problems!",
                    ChatStyles.SYSTEM);
        }
        
        tabHolder.setSelectedComponent(currentchannel);
    }
    
    public static void closeChannelPanel(String channelName) {
        closeChannelPanel(getChannelPanel(channelName));
    }
    
    public static void closeChannelPanel(ChannelPanel which) {
        channelPanels.remove(which);
        tabHolder.remove(which);
    }
    
    public static ChannelPanel getChannelPanel(String channelName) {
        int index = -1;
        index = tabHolder.indexOfTab(channelName);
        if (index != -1) {
            return (ChannelPanel) tabHolder.getComponentAt(index);
        } else {
            if (tabHolder.getComponentCount() != 0) {
                if (tabHolder.getComponentAt(0) instanceof ChannelPanel) {
                    return (ChannelPanel) tabHolder.getComponentAt(0);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }
    
    public static ChannelPanel getChannelPanel(int index){
        if(index < channelPanels.size()){
            return channelPanels.get(index);
        }else{
            return null;
        }
    }
    
    public static void openRoomPanel(boolean isHost, String channel, String modindex, String ip, boolean compatible, String hamachiIp, int maxPlayers){
        if(roomPanel == null){
            roomPanel = new RoomPanel(isHost, channel, modindex, ip, compatible, hamachiIp, maxPlayers);
            
            tabHolder.insertTab("Room", null, roomPanel, null, channelPanels.size());
        
            tabHolder.setSelectedComponent(roomPanel);
        
            for (ChannelPanel cp : channelPanels) {
                cp.disablebuttons();
            }
            
        }else{
            if(Globals.getDebug()){
                System.out.println("[W]\tClose the current RoomPanel before opening a new one!");
            }
        }
    }
    
    public static void closeRoomPanel(){
        if(roomPanel != null){
            if(Globals.getLauncher() != null){
                Globals.getLauncher().stop();
            }else{
                if(Globals.getDebug()){
                    System.out.println("[W]\tLauncher should not be set to null!");
                }
            }
            Globals.closeGameSettingsFrame();
            
            tabHolder.remove(roomPanel);

            int index = tabHolder.indexOfTab(roomPanel.channel);
            if(index != -1){
                tabHolder.setSelectedIndex(index);
            }
            for (ChannelPanel cp : channelPanels) {
                cp.enablebuttons();
            }
            
            roomPanel = null;
        }
    }
    
    public static RoomPanel getRoomPanel(){
        return roomPanel;
    }

    public static void openPrivateChatPanel(String title, boolean setFocus) {
        int index = tabHolder.indexOfTab(title);
        if (index == -1) {
            PrivateChatPanel pc = new PrivateChatPanel(title);
            tabHolder.add(title, pc);
            privateChatPanels.add(pc);
            if(setFocus){
                tabHolder.setSelectedComponent(pc);
                pc.requestFocus();
            }else{
                //Workaround for wrong color @ new tab that doesnt get focus
                if (Settings.getColorizeBody()) {
                    ChangeListener[] listeners = tabHolder.getChangeListeners();
                    for (int i = 0; i < listeners.length; i++) {
                        if (listeners[i] instanceof TabbedPaneColorChangeListener) {
                            TabbedPaneColorChangeListener cl = (TabbedPaneColorChangeListener) listeners[i];
                            cl.updateBG();
                            break;
                        }
                    }
                }
            }
        }else{
            if(setFocus){
                tabHolder.setSelectedIndex(tabHolder.indexOfTab(title));
                tabHolder.getSelectedComponent().requestFocus();
            }
        }
    }
    
    public static void closePrivateChatPanel(PrivateChatPanel which){
        tabHolder.remove(which);
        privateChatPanels.remove(which);
    }
    
    public static PrivateChatPanel getPrivateChatPanel(String title) {
        int index = tabHolder.indexOfTab(title);
        if(index != -1){
            JPanel panel = (JPanel) tabHolder.getComponentAt(index);
            if(panel instanceof PrivateChatPanel){
                return (PrivateChatPanel) panel;
            }else{
                if(Globals.getDebug()){
                    System.out.println("[W]\tThe Panel \""+title+"\" is not a PrivateChatPanel!");
                }
            }
        }
        
        return null;
    }
    
    public static void openBrowserPanel(String url) {
        if(browserPanel == null){
            browserPanel = new BrowserPanel(url);
            
            //panelHolder.addTab("Browser", browserPanel);
            tabHolder.addTab("Beginner's Guide", browserPanel); //For now this is ok
            tabHolder.setSelectedComponent(browserPanel);
        }else{
            tabHolder.setSelectedComponent(browserPanel);
            browserPanel.openUrl(url);
        }

        Globals.getClientFrame().repaint();
    }

    public static void closeBrowserPanel() {
        tabHolder.remove(browserPanel);
        browserPanel = null;
    }
    
    public static void openErrorPanel(int mode, Exception e) {
        if(errorPanel == null || errorPanel.hasException() == false && e != null){
            errorPanel = new ErrorPanel(mode, e);
            tabHolder.addTab("Error", errorPanel);
            tabHolder.setSelectedComponent(errorPanel);
        }else{
            if(Globals.getDebug()){
                System.out.println("[W]\tWe don't need another error tab, this error may be caused by the first one!");
            }
            tabHolder.setSelectedComponent(errorPanel);
        }
        
        Globals.getClientFrame().repaint();
    }
    
    public static void openLoginPanel() {
        if(loginPanel == null){
            //Thread is needed here to get rid of an exception at startup
            SwingUtilities.invokeLater(new Thread() {

                @Override
                public void run() {
                    try{
                        loginPanel = new LoginPanel();
                        tabHolder.addTab("Login", loginPanel);
                        tabHolder.setSelectedComponent(loginPanel);
                    }catch(Exception e){
                        ErrorHandler.handleException(e);
                    }
                }
            });
        }else{
            if(Globals.getDebug()){
                System.out.println("[W]\tThere's an open LoginPanel already!");
                tabHolder.setSelectedComponent(loginPanel);
            }
        }
    }

    public static void closeLoginPanel() {
        tabHolder.remove(loginPanel);
        loginPanel = null;
    }
    
    public static void openFileTransferSendPanel(String reciever, File file) {
        FileTransferSendPanel panel = new FileTransferSendPanel(reciever, file);
        fileTransferSendPanels.add(panel);
        tabHolder.add("Send file to " + reciever, panel);
    }
    
    public static void closeFileTransferSendPanel(FileTransferSendPanel which){
        fileTransferSendPanels.remove(which);
        tabHolder.remove(which);
    }
    
    public static FileTransferSendPanel getFileTransferSendPanel(String receiver, String fileName){
        for(int i = 0; i < fileTransferSendPanels.size(); i++){
            if(fileTransferSendPanels.get(i).getFilename().equals(fileName) && fileTransferSendPanels.get(i).getReciever().equals(receiver)){
                return fileTransferSendPanels.get(i);
            }
        }
        return null;
    }

    public static void openFileTransferReceivePanel(String sender, String size, String filename,String ip,String port) {
        FileTransferRecievePanel panel = new FileTransferRecievePanel(sender, new Long(size), filename,ip,port);
        fileTransferReceivePanels.add(panel);
        tabHolder.add("Recieve file from " + sender, panel);
    }
    
    public static void closeFileTransferReceivePanel(FileTransferRecievePanel which) {
        fileTransferReceivePanels.remove(which);
        tabHolder.remove(which);
    }
    
    public static FileTransferRecievePanel getFileTransferReceivePanel(String sender, String fileName){
        for(int i = 0; i < fileTransferReceivePanels.size(); i++){
            if(fileTransferReceivePanels.get(i).getFilename().equals(fileName) && fileTransferReceivePanels.get(i).getSender().equals(sender)){
                return fileTransferReceivePanels.get(i);
            }
        }
        return null;
    }
    
    public static void openGameDataEditor(){
        GameDatabase.load(null,GameDatabase.testdatafilepath );
        if(gamedataeditor == null){
            gamedataeditor = new JScrollPane(new TestGameDataEditor());
            tabHolder.add("TestGameData Editor", gamedataeditor);
        }
        else{
            putFocusOnTab("TestGameData Editor");            
        }
    }
    
    public static void closeGameDataEditor(){
        tabHolder.remove(gamedataeditor);
        gamedataeditor = null;
        GameDatabase.saveTestData();
    }
    
    /*******************************************************************/

    public static void updateTitleOnTab(String oldTitle, String newTitle){
        int index = -1;
        while((index = tabHolder.indexOfTab(oldTitle)) != -1){
            tabHolder.setTitleAt(index, newTitle);
        }
    }
    
    public static void putFocusOnTab(String title) {
        if(title != null){
            int index = tabHolder.indexOfTab(title);
            if (index != -1) {
                tabHolder.setSelectedIndex(index);
                tabHolder.getSelectedComponent().requestFocus();
            }
        }else{
            if(tabHolder.getSelectedComponent() != null){
                tabHolder.getSelectedComponent().requestFocus();
            }
        }
    }
    
    public static void updateSleepMode() {
        for (ChannelPanel cp : channelPanels) {
            cp.updateSleepMode();
        }
    }
    
    public static void closeAllTabs() {
        tabHolder.removeAll();
        
        channelPanels.clear();
        roomPanel = null;
        privateChatPanels.clear();
        errorPanel = null;
        browserPanel = null;
        loginPanel = null;
        fileTransferSendPanels.clear();
        fileTransferReceivePanels.clear();
    }
    
}

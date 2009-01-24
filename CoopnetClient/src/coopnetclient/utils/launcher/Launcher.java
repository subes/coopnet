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

package coopnetclient.utils.launcher;

import coopnetclient.Globals;
import coopnetclient.enums.ChatStyles;
import coopnetclient.enums.LaunchMethods;
import coopnetclient.enums.LogTypes;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.frames.clientframe.tabs.RoomPanel;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.utils.Logger;
import coopnetclient.utils.Settings;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.launcher.launchhandlers.JDPlayLaunchHandler;
import coopnetclient.utils.launcher.launchhandlers.LaunchHandler;
import coopnetclient.utils.launcher.launchhandlers.ParameterLaunchHandler;
import coopnetclient.utils.launcher.launchinfos.DirectPlayLaunchInfo;
import coopnetclient.utils.launcher.launchinfos.LaunchInfo;
import coopnetclient.utils.launcher.launchinfos.ParameterLaunchInfo;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class Launcher {
    
    private static boolean isInitialized;
    private static LaunchInfo launchedGameInfo;
    private static LaunchHandler launchHandler;
    private static SwingWorker delayedReinitThread;    

    public static boolean isInitialized(){
        return isInitialized;
    }
    
    public static boolean isPlaying(){
        return launchedGameInfo != null;
    }

    public static boolean isPlayingInstantLaunch(){
        return launchedGameInfo != null && launchedGameInfo.isInstantLaunch();
    }

    public static String getLaunchedGame(){
        if(launchedGameInfo == null){
            return null;
        }
        return launchedGameInfo.getGameName();
    }
    
    public static void initialize(LaunchInfo launchInfo){

        if(launchInfo == null){
            throw new IllegalArgumentException("launchInfo must not be null!");
        }

        if(delayedReinitThread != null){
            delayedReinitThread.cancel(true);
        }

        if(!isPlaying()){
            if(launchInfo instanceof DirectPlayLaunchInfo){
                launchHandler = new JDPlayLaunchHandler();
            }else
            if(launchInfo instanceof ParameterLaunchInfo){
                launchHandler = new ParameterLaunchHandler();
            }

            TempGameSettings.initalizeGameSettings(launchInfo.getGameName(), launchInfo.getModName());

            synchronized(launchHandler){
                isInitialized = launchHandler.initialize(launchInfo);

                if(TabOrganizer.getRoomPanel() != null){
                    int numSettings = GameDatabase.getGameSettings(launchInfo.getGameName(), launchInfo.getModName()).size();
                    if(isInitialized && numSettings == 0){
                        TabOrganizer.getRoomPanel().initDone();
                    }else{
                        TabOrganizer.getRoomPanel().initDoneReadyDisabled();
                    }
                }
            }

            if(launchInfo instanceof ParameterLaunchInfo){
                if(!launchInfo.isInstantLaunch()
                    && TabOrganizer.getRoomPanel()!= null
                    && GameDatabase.getGameSettings(launchInfo.getGameName(), launchInfo.getModName()).size() > 0){
                    //Frame decides if visible
                    Globals.openGameSettingsFrame(launchInfo.getGameName(), launchInfo.getModName(),launchInfo.isHost(),false);
                }
            }
        }else{
            determineInitializeActionWhenAlreadyPlaying(launchInfo);
        }
    }

    private static void determineInitializeActionWhenAlreadyPlaying(LaunchInfo curLaunchInfo){
        if(curLaunchInfo.isInstantLaunch()){
            //now joining/hosting instant launch
            //not allowed
            //print warning
            //done via joptionpane
        }else
        if(launchedGameInfo.isHost() && curLaunchInfo.isHost()
                || curLaunchInfo.getRoomID().equals(launchedGameInfo.getRoomID())){
            //Previously hosted and now hosting again
            //or previously joined and now joining same room again
            //already initialized, so just ok
            if(TabOrganizer.getRoomPanel() != null){
                TabOrganizer.getRoomPanel().initDone();
            }
        }else{
            //Anything else, delayed reinit
            //previously instantlaunched and now joining/hosting normal room
            //previously hosted, now joining
            //previously joined, now hosting
            if(TabOrganizer.getRoomPanel() != null){
                TabOrganizer.getRoomPanel().displayDelayedReinit();
                delayedReinitThread = new SwingWorker() {
                    RoomPanel roomPanelToDelayReinitOn = TabOrganizer.getRoomPanel();

                    @Override
                    protected Object doInBackground() throws Exception {
                        if(roomPanelToDelayReinitOn != null){
                            while(isPlaying()){
                                Thread.sleep(1000);
                            }
                        }
                        return null;
                    }
                    @Override
                    protected void done() {
                        if(       !isCancelled()
                                && roomPanelToDelayReinitOn != null
                                && roomPanelToDelayReinitOn.equals(TabOrganizer.getRoomPanel())){
                            roomPanelToDelayReinitOn.displayReInit();
                            roomPanelToDelayReinitOn.initLauncher();
                        }
                    }
                };
                delayedReinitThread.execute();
            }
        }

            
    }
    
    public static boolean predictSuccessfulLaunch(){
        if(isPlaying()){
            return true;
        }else
        if(!isInitialized ){
            Logger.log(LogTypes.LAUNCHER, "predictSuccessfulLaunch() called while Launcher was not initialized!");
            return false;
        }else{
            synchronized(launchHandler){
                return launchHandler.predictSuccessfulLaunch();
            }
        }
    }
    
    public static void launch(){

        if(isInitialized()){
            for (int i = 0; TabOrganizer.getChannelPanel(i) != null; i++) {
                TabOrganizer.getChannelPanel(i).disableButtons();
            }
            synchronized (launchHandler) {
                launchedGameInfo = launchHandler.getLaunchInfo();

                if (Settings.getSleepEnabled()) {
                    Globals.setSleepModeStatus(true);
                }

                boolean launchResult = launchHandler.launch();
                

                Globals.setSleepModeStatus(false);

                boolean doPrint = true;
                if(launchHandler instanceof JDPlayLaunchHandler){
                    JDPlayLaunchHandler handler = (JDPlayLaunchHandler) launchHandler;
                    if(handler.isSearchAborted()){
                        handler.resetAbortSearchFlag();
                        doPrint = false;
                    }
                }

                if(doPrint){
                    if(!launchResult){
                        Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Launch failed, maybe the game is not setup properly or a process closed unexpectedly!", ChatStyles.SYSTEM,false);
                    }
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM",
                                    "Game closed.",
                                    ChatStyles.SYSTEM,false);
                }

                launchedGameInfo = null;
                for (int i = 0; TabOrganizer.getChannelPanel(i) != null; i++) {
                    TabOrganizer.getChannelPanel(i).enableButtons();
                }                
            }
        }else{
            throw new IllegalStateException("The game has to be initialized before launching it!");
        }
    }
    
    public static void deInitialize(){
        if(launchHandler != null && launchHandler instanceof JDPlayLaunchHandler){
            JDPlayLaunchHandler handler = (JDPlayLaunchHandler)launchHandler;
            handler.abortSearch();
        }
        
        isInitialized = false;
        launchHandler = null;
    }
    
    public static void updatePlayerName(){
        LaunchHandler toWorkOn = launchHandler;
        if(toWorkOn != null){
            synchronized(toWorkOn){
                toWorkOn.updatePlayerName();
            }
        }
    }

    public static boolean initInstantLaunch(final String gameName, final String mod, final String hostIP, final int maxPlayers, final boolean isHost, final String roomName, String password) {
        Globals.getClientFrame().printToVisibleChatbox("SYSTEM",
                "Initializing game ...",
                ChatStyles.SYSTEM, false);

        ParameterLaunchInfo launchInfo;

        LaunchMethods method = GameDatabase.getLaunchMethod(gameName, mod);

        if (method == LaunchMethods.PARAMETER) {
            launchInfo = new ParameterLaunchInfo(gameName, mod, hostIP, isHost, true, roomName, password, RoomPanel.ROOMID_UNSUPPORTED);
        } else {
            throw new IllegalArgumentException("You can't instantlaunch from "+method.toString()+" channel! GameName: " + gameName + " ModName: " + mod);
        }

        if(isPlaying()){
            boolean showJOptionPane = false;
            if(!gameName.equals(launchedGameInfo.getGameName())){
                showJOptionPane = true;
            }else
            if(!hostIP.equals(launchedGameInfo.getHostIP())){
                showJOptionPane = true;
            }else
            if(!(mod == null && launchInfo.getModName() == null)){
                if(!mod.equals(launchedGameInfo.getModName())){
                    showJOptionPane = true;
                }
            }

            if(showJOptionPane){
                while(isPlaying()){
                    int option = JOptionPane.showConfirmDialog(null,
                            "<html>Coopnet has detected that the game \"<b>"+launchHandler.getBinaryName()+"</b>\" is already running.<br>" +
                            "You have to <b>close the game</b> to proceed launching.<br>" +
                            "<br>" +
                            "Press ok to retry or press cancel to abort the launch.",
                            "WARNING: Another game is already running",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE);

                    if(option == JOptionPane.CANCEL_OPTION){
                        return false;
                    }
                }
            }
        }

        Launcher.initialize(launchInfo);

        if (!Launcher.isInitialized()) {
            Protocol.closeRoom();
            Protocol.gameClosed(gameName);
            TabOrganizer.getChannelPanel(gameName).enableButtons();
        }

        return Launcher.isInitialized();
    }

    public static void instantLaunch(String channel){
        instantLaunch(channel, false);
    }

    public static void instantLaunch(String channel, boolean launchClickedFromGameSettingsFrame) {
        if (Launcher.isInitialized()) {
            TabOrganizer.getChannelPanel(channel).disableButtons();

            if(launchHandler.getLaunchInfo().isHost() && (isPlaying() || launchHandler.processExists())){
                JOptionPane.showMessageDialog(null,
                    "<html>Coopnet has detected that the game \"<b>"+launchHandler.getBinaryName()+"</b>\" is already running.<br>" +
                    "Please make sure the other players can <b>connect to a running server</b> there<br>" +
                    "or <b>close the game</b> before confirming this message.<br>" +
                    "<br>" +
                    "<br>If the game is still running after you have confirmed this message," +
                    "<br>Coopnet will create the room without launching your game.",
                    "WARNING: Game is already running",
                    JOptionPane.WARNING_MESSAGE);

                while(isPlaying()){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {}
                }
            } else if ( !launchHandler.getLaunchInfo().isHost() && !launchClickedFromGameSettingsFrame ) {
                //room data is dummy , its not gona be used in this case at all
                Globals.openGameSettingsFrame(channel, launchHandler.getLaunchInfo().getModName(), "", "", -1, -1,launchHandler.getLaunchInfo().isHost());
            } else {
                Launcher.launch();
                Launcher.deInitialize();
                Protocol.gameClosed(channel);
                TabOrganizer.getChannelPanel(channel).enableButtons();
            }
        }
    }
    
}

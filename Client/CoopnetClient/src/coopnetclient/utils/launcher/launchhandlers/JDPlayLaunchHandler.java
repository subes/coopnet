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
package coopnetclient.utils.launcher.launchhandlers;

import coopnetclient.Globals;
import coopnetclient.enums.ChatStyles;
import coopnetclient.enums.LogTypes;
import coopnetclient.enums.OperatingSystems;
import coopnetclient.frames.FrameOrganizer;
import coopnetclient.frames.clientframetabs.TabOrganizer;
import coopnetclient.frames.clientframetabs.RoomPanel;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.utils.Logger;
import coopnetclient.utils.ProcessHelper;
import coopnetclient.utils.launcher.launchinfos.DirectPlayLaunchInfo;
import coopnetclient.utils.launcher.launchinfos.LaunchInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class JDPlayLaunchHandler extends LaunchHandler {

    //1 try = 5 secs; 12 tries = 60 secs
    public static final int JDPLAY_MAXSEARCHRETRIES = 12;
    //set to 1 to be sure the joined session is not a temp one, though slower launch!
    public static final int JDPLAY_SEARCHVALIDATIONCOUNT = 1;
    private static final String DONE_COMMAND = "DONE";


    private static Process jdplay;
    private static OutputStream out;
    private static BufferedReader in;
    private static boolean isSearching;
    private static boolean abortSearch;
    private static boolean sessionFound;
    private static Thread progressBarHider;
    private DirectPlayLaunchInfo launchInfo;
    private String lastRead = "";

    public boolean isSearchAborted() {
        return abortSearch;
    }

    public void resetAbortSearchFlag() {
        abortSearch = false;
    }

    @Override
    public boolean doInitialize(LaunchInfo launchInfo) {
        if (jdplay == null) {
            //Workaround for wine, it puts "" to the playername if the playername doesn't contain a space
            String playerName;
            if (Globals.getThisPlayerInGameName().contains(" ")) {
                playerName = "\"" + Globals.getThisPlayerInGameName() + "\"";
            } else {
                playerName = Globals.getThisPlayerInGameName();
            }

            String command = "";

            if (Globals.getOperatingSystem() == OperatingSystems.LINUX) {
                command += Globals.getWineCommand();
            }

            command += " lib/jdplay.exe" +
                    " --playerName " + playerName +
                    " --maxSearchRetries " + JDPLAY_MAXSEARCHRETRIES +
                    " --searchValidationCount " + JDPLAY_SEARCHVALIDATIONCOUNT +
                    " --debug";

            //print exec string
            Logger.log(LogTypes.LAUNCHER, command);

            //run
            try {
                jdplay = Runtime.getRuntime().exec(command);
                out = jdplay.getOutputStream();
                in = new BufferedReader(new InputStreamReader(jdplay.getInputStream()));
            } catch (IOException e) {
                Logger.log(e);

                FrameOrganizer.getClientFrame().printSystemMessage(
                        "Error while initializing:" + e.getMessage(), false);

                return false;
            }
        }

        sessionFound = false;

        if (!(launchInfo instanceof DirectPlayLaunchInfo)) {
            throw new IllegalArgumentException("expected launchInfo to be " + DirectPlayLaunchInfo.class.toString() + ", but got " + launchInfo.getClass().toString());
        }

        this.launchInfo = (DirectPlayLaunchInfo) launchInfo;

        if (!write("INITIALIZE" +
                " gameGUID:" + this.launchInfo.getGameGUID() +
                " hostIP:" + this.launchInfo.getRoomData().getIP() +
                " isHost:" + this.launchInfo.getRoomData().isHost() +
                " maxPlayers:" + this.launchInfo.getRoomData().getMaxPlayers())) {
            return false;
        }

        String[] toRead = {"FIN", "ERR"};
        int ret = read(toRead);
        if (ret == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean doLaunch() {

        boolean doSearch = launchInfo.isSearchEnabled();

        if (!write("LAUNCH doSearch:" + doSearch + " startGame:true")) {
            return false;
        }

        if (doSearch && !launchInfo.getRoomData().isHost() && !sessionFound) {

            FrameOrganizer.getClientFrame().printSystemMessage(
                    "Connecting to host ...", false);

            if (TabOrganizer.getRoomPanel() != null) {
                TabOrganizer.getRoomPanel().getConnectingProgressBar().setProgress(0, JDPLAY_MAXSEARCHRETRIES);
                TabOrganizer.getRoomPanel().getConnectingProgressBar().setVisible(true);
            } else {
                return false;
            }

            boolean done = false;

            JDPlayLaunchHandler.isSearching = true;

            while (!done && !abortSearch) {
                boolean noSessionFound = false;

                String[] toRead = {"SEARCHTRY", "NOTFOUND", "FOUND"};
                switch (read(toRead)) {
                    case 0:
                        String progress = lastRead.substring(10);
                        int cur = Integer.parseInt(progress.split("/")[0]);
                        int max = Integer.parseInt(progress.split("/")[1]);
                        if (TabOrganizer.getRoomPanel() != null) {
                            TabOrganizer.getRoomPanel().getConnectingProgressBar().setProgress(cur, max);
                        } else {
                            abortSearch();
                        }
                        break;
                    case 1:
                        noSessionFound = true;
                        break;
                    case 2:
                        sessionFound = true;
                        done = true;
                        break;
                    default:
                    //nothing, though might happen on abort
                }

                if (noSessionFound) {
                    abortSearch = true;
                    if (TabOrganizer.getRoomPanel() != null) {
                        FrameOrganizer.getClientFrame().printSystemMessage(
                                "Launch failed! Found no session to join! The host maybe failed to launch or a firewall blocked your join attempt.", false);
                        TabOrganizer.getRoomPanel().getConnectingProgressBar().setVisible(false);
                    }
                    return false;
                } else if (abortSearch) {
                    return false;
                }
            }

            JDPlayLaunchHandler.isSearching = false;

            if (TabOrganizer.getRoomPanel() != null) {
                TabOrganizer.getRoomPanel().getConnectingProgressBar().setDone();
                progressBarHider = new Thread() {

                    @Override
                    public void run() {
                        try {
                            sleep(30000);
                        } catch (InterruptedException ex) {
                        }
                        if (TabOrganizer.getRoomPanel() != null) {
                            TabOrganizer.getRoomPanel().getConnectingProgressBar().setVisible(false);
                        }
                    }
                };
                progressBarHider.start();
            }
        }

        FrameOrganizer.getClientFrame().printSystemMessage(
                "Launching game, please wait ...", false);

        if (launchInfo.getRoomData().isHost() && !launchInfo.getRoomData().isInstant()) {
            Protocol.launch();
        }

        String[] toRead2 = {"FIN", "ERR"};
        boolean ret = read(toRead2) == 0;

        //TODO: Use this when startGame:true, though have to revise this too :P
//        if(ret == false){
//            return false;
//        }
//
//
//        Process p = null;
//        try {
//            String command = "";
//
//            if(Globals.getOperatingSystem() == OperatingSystems.LINUX){
//                command += Globals.getWineCommand();
//            }
//
//            command += " "+launchInfo.getBinaryPath() +
//                    " /dplay_ipc_guid:" + launchInfo.getGameGUID() +
//                    " " + launchInfo.getParameters();
//
//            Runtime rt = Runtime.getRuntime();
//            Logger.log(LogTypes.LAUNCHER, command);
//            File installdir = new File(launchInfo.getInstallPath());
//            p = rt.exec(command, null, installdir);
//
//            try{
//                p.exitValue();
//                throw new Exception("Game exited too fast!"); //caught by outer catch
//            }catch(IllegalStateException e){}
//
//            if(launchInfo.getRoomData().isHost() && !launchInfo.getRoomData().isInstant()){
//                Protocol.launch();
//            }
//
//            try {
//                p.waitFor();
//            } catch (InterruptedException ex) {}
//        } catch (Exception e) {
//            FrameOrganizer.getClientFrame().printSystemMessage(
//                    "Error while launching: " + e.getMessage()+"\nAborting launch!",
//                    ChatStyles.SYSTEM, false);
//            Logger.log(e);
//            return false;
//        }

        if (Globals.getOperatingSystem() == OperatingSystems.LINUX) {
            try {
                Process pkill = Runtime.getRuntime().exec("pkill -f dplaysvr.exe");
                pkill.waitFor();
                ProcessHelper.closeStreams(pkill);
            } catch (Exception e) {
                Logger.log(e);
            }
        }

        if (progressBarHider != null) {
            progressBarHider.interrupt();
        }

        return /*(p.exitValue() == 0 ? true : false)*/ ret;
    }

    @Override
    public void updatePlayerName() {
        write("UPDATE playerName:" + Globals.getThisPlayerInGameName());
    }

    private boolean read(String toRead) {
        String[] asArray = {toRead};
        if (read(asArray) == 0) {
            return true;
        } else {
            return false;
        }

    }
    //Reads one of the possibilities given in toRead and gives the found index

    private int read(String[] toRead) {
        try {
            do {
                String ret = in.readLine();
                Logger.log(LogTypes.LAUNCHER, "IN: " + ret);

                if (ret == null) {
                    Logger.log(LogTypes.LAUNCHER, "Read null, jdplay.exe closed");
                    reinitJDPlay();
                    return -1;
                }

                for (int i = 0; i < toRead.length; i++) {
                    if (ret.contains(toRead[i])) {
                        lastRead = ret;
                        return i;
                    }
                }
            } while (true);
        } catch (Exception e) {
            reinitJDPlay();
            printError(e);
            return -1;
        }
    }

    private synchronized boolean write(String toWrite) {
        String newToWrite = toWrite.trim();

        //wait until jdplay is ready
        read("RDY");

        try {
            if (!newToWrite.endsWith("\n")) {
                newToWrite += "\n";
            }

            //write
            Logger.log(LogTypes.LAUNCHER, "OUT: " + newToWrite);
            out.write(newToWrite.getBytes());
            out.flush();

            if (!newToWrite.equals(DONE_COMMAND + "\n")) {
                newToWrite = DONE_COMMAND + "\n";
                Logger.log(LogTypes.LAUNCHER, "OUT: " + newToWrite);
                out.write(newToWrite.getBytes());
                out.flush();

                //verify if jdplay understood
                String[] toRead = {"ACK", "NAK"};
                int ret = read(toRead);
                return ret == 0;
            }

            return true;
        } catch (Exception e) {
            reinitJDPlay();
            printError(e);
            return false;
        }
    }

    private void printError(Exception e) {
        if (e == null) {
            FrameOrganizer.getClientFrame().printSystemMessage(
                    "Undetermined DirectPlay error.\nRecovering ...", false);
        } else {
            Logger.log(e);
            FrameOrganizer.getClientFrame().printSystemMessage(
                    "DirectPlay error: " + e.getMessage() + "\nRecovering ...", false);
        }
    }

    private void reinitJDPlay() {

        RoomPanel room = TabOrganizer.getRoomPanel();
        if (room != null) {
            room.displayReInit();
        }

        if (out != null) {
            try {
                out.close();
            } catch (IOException ex) {
            }
            out = null;
        }

        if (in != null) {
            try {
                in.close();
            } catch (IOException ex) {
            }
            in = null;
        }

        if (jdplay != null) {
            ProcessHelper.destroy(jdplay);
            jdplay = null;
        }


        if (room != null) {
            room.initLauncher();
        }
    }

    @Override
    public boolean predictSuccessfulLaunch() {
        boolean ret = jdplay != null && out != null && in != null;

        if (ret) {
            try {
                jdplay.exitValue();
            } catch (NullPointerException e) {
                ret = false;
            } catch (IllegalThreadStateException e) {
            }
        }

        if (ret) {
            write("STILLALIVETEST");
            write(DONE_COMMAND);
        }

        if (!ret) {
            reinitJDPlay();
        }

        return ret;
    }

    public void abortSearch() {
        if (JDPlayLaunchHandler.isSearching) {
            Logger.log(LogTypes.LAUNCHER, "Aborting search");
            abortSearch = true;
            reinitJDPlay();
        }
    }
}

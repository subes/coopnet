package coopnetclient.utils;

import coopnetclient.Globals;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.utils.launcher.Launcher;
import javax.swing.JFrame;

public class InactivityWatcher extends Thread {

    private int inactivetime = 0;
    private boolean isAway = false;

    public InactivityWatcher() {
        super();
    }

    @Override
    public void run() {
        while (true) {
            try {
                sleep(1000);
            } catch (Exception e) {
            }
            if (Globals.getLoggedInStatus()) {
                if (Launcher.isPlaying()) {
                    continue;
                }
                boolean isActive = false;
                JFrame frame = Globals.getClientFrame();
                if (frame != null) {
                    isActive = isActive || frame.isActive();
                }
                frame = Globals.getBugReportFrame();
                if (frame != null) {
                    isActive = isActive || frame.isActive();
                }
                frame = Globals.getChangePasswordFrame();
                if (frame != null) {
                    isActive = isActive || frame.isActive();
                }
                frame = Globals.getEditProfileFrame();
                if (frame != null) {
                    isActive = isActive || frame.isActive();
                }
                frame = Globals.getGameSettingsFrame();
                if (frame != null) {
                    isActive = isActive || frame.isActive();
                }
                frame = Globals.getMuteBanTableFrame();
                if (frame != null) {
                    isActive = isActive || frame.isActive();
                }
                frame = Globals.getManageGamesFrame();
                if (frame != null) {
                    isActive = isActive || frame.isActive();
                }
                if (!isActive) {
                    inactivetime++;
                } else {
                    if (isAway) {//was away, not anymore
                        isAway = false;
                        Protocol.unSetAwayStatus();
                        inactivetime = 0;
                        continue;
                    }
                }
                if (inactivetime >= 10) {
                    if (!isAway) {//just became afk
                        isAway = true;
                        Protocol.setAwayStatus();
                    }
                }
            }
        }
    }
}

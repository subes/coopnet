package coopnetclient.launchers.launchhandlers;

import coopnetclient.launchers.launchinfos.LaunchInfo;

public class ParameterLaunchHandler extends LaunchHandler {

    @Override
    public boolean doInitialize(LaunchInfo launchInfo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean doLaunch() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updatePlayerName(String playerName) {
        //do nothing, because parameter based games don't support this
    }

}

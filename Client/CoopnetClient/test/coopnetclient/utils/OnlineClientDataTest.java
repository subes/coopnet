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

package coopnetclient.utils;

import coopnetclient.test.AbstractCoopnetClientTest;
import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author subes
 */
public class OnlineClientDataTest extends AbstractCoopnetClientTest {

    public OnlineClientDataTest() {
    }

    @Test
    public void testGetClientVersion() throws Exception {
        String result = OnlineClientData.getClientVersion();
        assertNotNull(result);
        Verification.verifyClientVersion(result);
    }

    @Test
    public void testReadServerAddress() {
        OnlineClientData.readServerAddress();
    }

    @Test
    public void testOpenFaq() {
        OnlineClientData.openFaq();
    }

    @Test
    public void testOpenBeginnersGuide() {
        OnlineClientData.openBeginnersGuide();
    }

    @Test
    public void testDownloadLatestUpdater() {
        String fileName = "testDownloadLatestUpdater.jar";
        OnlineClientData.downloadLatestUpdater(fileName);

        File file = new File(fileName);
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    public void testCheckAndUpdateGameData() {
        OnlineClientData.checkAndUpdateGameData();
    }

}
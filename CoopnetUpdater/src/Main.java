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

import java.io.File;

public class Main {

    public static void main(String[] args) {
        MessageFrame mf = null;
        try {
            mf = new MessageFrame();
            mf.setVisible(true);
            String updateurl = Settings.getupdateURL();
            if (updateurl == null) {
                throw new Exception("Can't find update URL!");
            }
            FileDownloader.downloadFile(updateurl, new File("./dist.zip").getCanonicalPath());
            UnZipper.UnZip("./dist.zip", new File("..").getCanonicalPath());
            new File("./dist.zip").delete();
            mf.setVisible(false);
            try {
                Runtime rt = Runtime.getRuntime();
                rt.exec("java -jar CoopnetClient.jar");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            MessageFrame.setMessage("<html>An error occured while updating:<br>" + e.getLocalizedMessage());
        }
    }
}

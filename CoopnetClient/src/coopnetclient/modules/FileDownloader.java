/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
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

package coopnetclient.modules;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

public class FileDownloader {

    /**
     * downloads the file at url in local file destionation
     */
    public static boolean downloadFile(String url, String destination) {

        BufferedInputStream bi = null;
        BufferedOutputStream bo = null;
        File destfile;
        try {
            java.net.URL fileurl;
            try {
                fileurl = new java.net.URL(url);
            } catch (MalformedURLException e) {
                return false;
            }

            bi = new BufferedInputStream(fileurl.openStream());

            destfile = new File(destination);
            if (!destfile.createNewFile()) {
                destfile.delete();
                destfile.createNewFile();
            }

            bo = new BufferedOutputStream(new FileOutputStream(destfile));

            int readedbyte;

            while ((readedbyte = bi.read()) != -1) {
                bo.write(readedbyte);
            }
            bo.flush();

        } catch (IOException ex) {
            return false;
        } finally {
            try {
                bi.close();
                bo.close();
            } catch (Exception ex) {
            }
        }
        return true;
    }
}

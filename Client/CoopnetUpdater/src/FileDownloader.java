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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URLConnection;

public class FileDownloader {

    /**
     * downloads the file at url in local file destionation
     */
    public static boolean downloadFile(String url, String destination) throws Exception {

        BufferedInputStream bi = null;
        BufferedOutputStream bo = null;
        File destfile;
        byte BUFFER[] = new byte[100];

        java.net.URL fileurl;
        URLConnection conn;

        fileurl = new java.net.URL(url);
        conn = fileurl.openConnection();

        long fullsize = conn.getContentLength();
        long onepercent = fullsize/100;
        MessageFrame.setTotalDownloadSize(fullsize);
        bi = new BufferedInputStream(conn.getInputStream());

        destfile = new File(destination);
        if (!destfile.createNewFile()) {
            destfile.delete();
            destfile.createNewFile();
        }

        bo = new BufferedOutputStream(new FileOutputStream(destfile));

        int read = 0;
        int sum = 0;
        long i = 0;

        while ((read = bi.read(BUFFER)) != -1) {
            bo.write(BUFFER,0,read);
            sum += read;
            i+=read;
            if(i >  onepercent ){
                i = 0;
                MessageFrame.setDownloadProgress(sum);
            }
        }
        bi.close();
        bo.close();
        MessageFrame.setDownloadProgress(fullsize);
        return true;
    }
}

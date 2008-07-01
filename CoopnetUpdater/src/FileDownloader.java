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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class FileDownloader {

    /**
     * downloads the file at url in local file destionation
     */
    public static boolean downloadFile(String url, String destination) throws Exception {

        BufferedInputStream bi = null;
        BufferedOutputStream bo = null;
        File destfile;

        java.net.URL fileurl;

        System.out.println("az url:"+url);
        fileurl = new java.net.URL(url);

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
        bi.close();
        bo.close();
        return true;
    }
}

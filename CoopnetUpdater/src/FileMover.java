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

// Parts of this file are taken from: 
// http://www.roseindia.net/java/example/java/io/MovingFile.shtml


import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileMover {

    public static void copyDirectory(File sourceDir, File destDir)
            throws IOException {

        if (!destDir.exists()) {

            destDir.mkdir();

        }

        File[] children = sourceDir.listFiles();

        for (File sourceChild : children) {

            String name = sourceChild.getName();

            File destChild = new File(destDir, name);

            if (sourceChild.isDirectory()) {

                copyDirectory(sourceChild, destChild);

            } else {

                copyFile(sourceChild, destChild);

            }

        }

    }

    public static void copyFile(File source, File dest) throws IOException {

        if (!dest.exists()) {

            dest.createNewFile();

        }

        InputStream in = null;

        OutputStream out = null;

        try {

            in = new FileInputStream(source);

            out = new FileOutputStream(dest);

            byte[] buf = new byte[1024];

            int len;

            while ((len = in.read(buf)) > 0) {

                out.write(buf, 0, len);

            }

        } finally {

            in.close();

            out.close();

        }

    }

    public static boolean delete(File resource) throws IOException {

        if (resource.isDirectory()) {

            File[] childFiles = resource.listFiles();

            for (File child : childFiles) {

                delete(child);

            }

        }

        return resource.delete();

    }
}

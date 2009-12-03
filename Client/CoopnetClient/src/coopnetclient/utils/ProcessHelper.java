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

import java.io.IOException;

/**
 *
 * @author subes
 */
public class ProcessHelper {

    public static void closeStreams(Process p) {
        if(p == null){
            return;
        }

        try {
            p.getErrorStream().close();
            p.getInputStream().close();
            p.getOutputStream().close();
        } catch (IOException ex) {
            Logger.log(ex);
        }
    }

    public static void destroy(Process p){
        if(p == null){
            return;
        }

        closeStreams(p);
        p.destroy();
    }
}

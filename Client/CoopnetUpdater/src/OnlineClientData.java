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


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public final class OnlineClientData {

    private static final String ONLINE_CLIENT_DATA =
            "http://coopnet.svn.sourceforge.net/viewvc/coopnet/trunk/misc/OnlineClientData/";

    private OnlineClientData() {
    }

    private static String extractFirstLine(String urlToFile) {
        try {
            URL url = new URL(urlToFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.
                    openStream()));
            return br.readLine();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getLatestClientUrl(){
        return extractFirstLine(ONLINE_CLIENT_DATA + "LatestClient.txt");
    }
}

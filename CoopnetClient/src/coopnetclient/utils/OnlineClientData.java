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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public final class OnlineClientData {

    private static final String ONLINE_CLIENT_DATA = "http://coopnet.svn.sourceforge.net/viewvc/coopnet/trunk/misc/OnlineClientData/";

    private OnlineClientData(){}

    public static String getClientVersion() throws Exception{
        URL url = new URL(ONLINE_CLIENT_DATA+"LatestClientVersion.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        return br.readLine();
    }

    public static void getCoopnetServer(){

    }

    public static void getFAQ(){

    }

    public static void getBeginnersGuide(){

    }

    public static String getLatestUpdater(){
        return ONLINE_CLIENT_DATA+"latestUpdater.php";
    }

    public static void getGameData(){
        
    }

}

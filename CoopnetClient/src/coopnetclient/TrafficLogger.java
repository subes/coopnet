/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
                    Kovacs Zsolt (kovacs.zsolt.85@gmail.com)

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

package coopnetclient;

public class TrafficLogger {

    private static String log = "";
    private static final int taillength = 20;
    

    public static String getEndOfLog() {
        String st = "";
        String[] tmp = log.split("\n");
        int i = 0;
        i = tmp.length;
        if (i < taillength) {
            return log;
        }
        for (int j = i - taillength; j <= i - 1; j++) {
            st += tmp[j] + "\n";
        }
        return st;
    }

    public static void append(String message) {
        log = getEndOfLog() + message + "\n";
    }
}

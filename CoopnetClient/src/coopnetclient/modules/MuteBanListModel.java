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
package coopnetclient.modules;

import coopnetclient.enums.MuteBanStatuses;
import java.util.HashMap;

public class MuteBanListModel {

    private static HashMap<String, MuteBanStatuses> muteBanList = new HashMap<String, MuteBanStatuses>();

    public static String getElementAt(int index){
        return muteBanList.keySet().toArray()[index].toString();
    }

    public static MuteBanStatuses getMuteBanStatus(String name) {
        MuteBanStatuses status = muteBanList.get(name);
        return status;
    }

    public static void clear() {
        muteBanList.clear();
    }

    public static int size() {
        return muteBanList.size();
    }

    public static void mute(String name) {
        MuteBanStatuses status = muteBanList.get(name);
        if (status == null) {
            muteBanList.put(name, MuteBanStatuses.MUTED);
        } else {
            if (status == MuteBanStatuses.BANNED) {
                muteBanList.put(name, MuteBanStatuses.BOTH);
            }
        }
    }

    public static void ban(String name) {
        MuteBanStatuses status = muteBanList.get(name);
        if (status == null) {
            muteBanList.put(name, MuteBanStatuses.BANNED);
        } else {
            if (status == MuteBanStatuses.MUTED) {
                muteBanList.put(name, MuteBanStatuses.BOTH);
            }
        }
    }

    public static void unMute(String name) {
        MuteBanStatuses status = muteBanList.get(name);
        if (status == null) {
            return;
        }
        switch (status) {
            case MUTED:
                muteBanList.remove(name);
                break;
            case BOTH:
                muteBanList.put(name, MuteBanStatuses.BANNED);
                break;
        }
    }

    public static void unBan(String name) {
        MuteBanStatuses status = muteBanList.get(name);
        if (status == null) {
            return;
        }
        switch (status) {
            case BANNED:
                muteBanList.remove(name);
                break;
            case BOTH:
                muteBanList.put(name, MuteBanStatuses.MUTED);
                break;
        }
    }
}

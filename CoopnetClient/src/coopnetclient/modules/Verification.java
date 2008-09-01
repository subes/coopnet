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

import coopnetclient.*;
import javax.swing.JOptionPane;

public class Verification {

    public static boolean verifyClientVersion(String checkAgainst) {
        //Here we have a number scheme x.x.x
        String[] checkAgainstSplit = checkAgainst.split("\\.");
        String[] clientVersionSplit = Globals.getClientVersion().split("\\.");

        for (int i = 0; i < clientVersionSplit.length; i++) {
            if (Integer.parseInt(clientVersionSplit[i]) < Integer.parseInt(checkAgainstSplit[i])) {
                return false;
            }
        }

        return true;
    }

    public static boolean verifyPassword(String password) {
        if (password.length() < 5 || password.length() > 30) {
            JOptionPane.showMessageDialog(Globals.getClientFrame(), "Your password must have 5 to 30 characters.",
                    "Registration error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public static boolean verifyUsername(String username) {

        boolean valid = true;

        if (username.length() < 5 || username.length() > 30) {
            valid = false;
        }

        for (int i = 0; i < username.length(); i++) {
            int cur = Integer.valueOf(username.charAt(i));

            if (!(cur >= 64 && cur <= 90 // @ A-Z 
                    || cur >= 97 && cur <= 126 // a-z { | } ~
                    || cur >= 48 && cur <= 57 // 0-9
                    || cur >= 60 && cur <= 62 // < = >
                    || cur == 40 || cur == 41 // ( )
                    || cur == 45 || cur == 95 // - _
                    || cur == 91 || cur == 93 // [ ]
                    )) {
                valid = false;
            }
        }

        if (valid == false) {
            JOptionPane.showMessageDialog(Globals.getClientFrame(), "Your login name must have 5 to 30 characters.\n" +
                    "The following characters are allowed:\n" +
                    "  A-Z a-z 0-9 " +
                    "@ ~ - _ = | " +
                    "<> () [] {}", "Registration error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}

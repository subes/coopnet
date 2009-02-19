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

import coopnetclient.Globals;
import java.io.File;

public final class Verification {
    //See http://www.regular-expressions.info/email.html (more practical implementation of RFC 2822)

    private static final String EMAIL_REGEXP = "[a-z0-9!#$%&\'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)" +
            "*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
    private static final String LOGINNAME_REGEXP = "\\p{Graph}{3,30}";
    private static final int MAX_COUNTRY_LENGTH = 60;
    private static final int MAX_EMAIL_LENGTH = 320;
    private static final int MAX_GROUPNAME_LENGTH = 30;
    private static final int MAX_INGAMENAME_LENGTH = 30;
    private static final int MAX_PASSWORD_LENGTH = 5;
    private static final int MAX_WEBSITE_LENGTH = 320;
    private static final int MIN_EMAIL_LENGTH = 5;
    private static final int MIN_GROUPNAME_LENGTH = 1;
    private static final int MIN_INGAMENAME_LENGTH = 1;
    private static final String VERSION_SPLIT_CHARACTER = "\\.";

    private Verification() {
    }

    public static boolean verifyCompatibilityVersion(String version) {
        try {
            int v = Integer.parseInt(version);
            if (v != Globals.COMPATIBILITY_VERSION) {
                return false;
            }
        } catch (Exception e) {
            Logger.log(e);
            return false;
        }

        return true;
    }

    public static boolean verifyClientVersion(String checkAgainst) {
        try {
            //Here we have a number scheme x.x.x
            String[] checkAgainstSplit = checkAgainst.split(VERSION_SPLIT_CHARACTER);
            String[] clientVersionSplit = Globals.CLIENT_VERSION.split(VERSION_SPLIT_CHARACTER);

            for (int i = 0; i < clientVersionSplit.length; i++) {
                if (Integer.parseInt(clientVersionSplit[i]) < Integer.parseInt(checkAgainstSplit[i])) {
                    return Integer.parseInt(clientVersionSplit[i - 1]) > Integer.parseInt(checkAgainstSplit[i - 1]);
                }
            }
        } catch (Exception e) {
            Logger.log(e);
            return false;
        }

        return true;
    }

    public static boolean verifyPassword(String password) {
        if (password.length() < MAX_PASSWORD_LENGTH) {
            return false;
        }

        return true;
    }

    public static boolean verifyLoginName(String loginName) {
        return loginName.matches(LOGINNAME_REGEXP);
    }

    public static boolean verifyIngameName(String ingameName) {
        if (ingameName.length() < MIN_INGAMENAME_LENGTH
                || ingameName.length() > MAX_INGAMENAME_LENGTH) {
            return false;
        }
        return true;
    }

    public static boolean verifyEMail(String email) {
        if (email.length() < MIN_EMAIL_LENGTH
                || email.length() > MAX_EMAIL_LENGTH) {
            return false;
        }

        return email.matches(EMAIL_REGEXP);
    }

    public static boolean verifyWebsite(String website) {
        if (website.length() > MAX_WEBSITE_LENGTH) {
            return false;
        }

        return true;
    }

    public static boolean verifyCountry(String country) {
        if (country.length() > MAX_COUNTRY_LENGTH) {
            return false;
        }

        return true;
    }

    public static boolean verifyGroupName(String groupName) {
        if (groupName.length() < MIN_GROUPNAME_LENGTH
                || groupName.length() > MAX_GROUPNAME_LENGTH) {
            return false;
        }

        return true;
    }

    public static boolean verifyDirectory(String dirName) {
        File dir = new File(dirName);
        return dir.isDirectory() && dir.exists();
    }

    public static boolean verifyFile(String fileName) {
        File file = new File(fileName);
        return file.isFile() && file.exists();
    }
}

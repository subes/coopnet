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

package coopnetclient.utils.settings;

import coopnetclient.Globals;
import coopnetclient.enums.OperatingSystems;
import coopnetclient.utils.Logger;
import coopnetclient.utils.RegistryReader;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public final class SettingsHelper {
    public static final String SETTINGS_DIR; //Gets set OS-Specific
    private static final Properties DATA;    // Load the settings at first usage
    private static final String SETTINGS_FILE;

    static {
        if (Globals.getOperatingSystem() == OperatingSystems.WINDOWS) {
            SETTINGS_DIR = System.getenv("APPDATA") + "/Coopnet";

            String recvdir = RegistryReader.read(
                    "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows" +
                    "\\CurrentVersion\\Explorer\\Shell Folders\\Desktop");
            if (recvdir == null) {
                recvdir = System.getenv("HOMEPATH");
            }

            Settings.setRecieveDestination(recvdir);
        } else {
            final String home = System.getenv("HOME");
            SETTINGS_DIR = home + "/.coopnet";
            Settings.setRecieveDestination(home);
        }

        SETTINGS_FILE = SETTINGS_DIR + "/settings";

        DATA = new java.util.Properties();
        load();
        Favourites.loadFavourites();
    }

    private SettingsHelper(){}

    public static void resetSettings() {
        DATA.clear();
        save();
        Favourites.resetFavourites();
    }

        /**
     * store the settings in options file
     */
    protected static void save() {
        try {
            DATA.store(new FileOutputStream(SETTINGS_FILE), "Coopnet settings");
        } catch (FileNotFoundException ex) {
            Logger.log(ex);
        } catch (IOException ex) {
            Logger.log(ex);
        }
    }

    /**
     * Load the settings from the options file, or create one with default values if it doesn exist
     */
    protected static void load() {
        try {
            if (!new File(SETTINGS_DIR).exists()) {
                new File(SETTINGS_DIR).mkdir();
            }
            DATA.load(new FileInputStream(SETTINGS_FILE));
        } catch (Exception ex) {
            Logger.log("Settings file not file, resetting to defaults.");
            //settings will be restored to default when they cant be read via a getter
        }
    }

    /**
     * settings readers used by the real getters
     */
    //Generic getter for Strings. Private, coz used by the real getters
    protected static String readString(String entry, String defaultValue) {
        String ret = DATA.getProperty(entry);

        if (ret == null) {
            //reset setting to default value
            writeSetting(entry, defaultValue);
            ret = defaultValue;
        }

        return ret;
    }

    //Generic getter for integer. Private, coz used by the real getters
    protected static int readInteger(String entry, int defaultValue) {
        boolean error = false;

        int ret = 0;
        String get = DATA.getProperty(entry);

        if (get != null) {
            try {
                ret = Integer.parseInt(get);
            } catch (NumberFormatException e) {
                error = true;
            }
        }

        if (error || get == null) {
            //reset setting to default value
            writeSetting(entry, String.valueOf(defaultValue));
            return defaultValue;
        }

        return ret;
    }

    //Generic getter for integer. Private, coz used by the real getters
    protected static float readFloat(String entry, float defaultValue) {
        boolean error = false;

        float ret = 0;
        String get = DATA.getProperty(entry);

        if (get != null) {
            try {
                ret = Float.parseFloat(get);
            } catch (NumberFormatException e) {
                error = true;
            }
        }

        if (error || get == null) {
            //reset setting to default value
            writeSetting(entry, String.valueOf(defaultValue));
            return defaultValue;
        }

        return ret;
    }


    //Generic getter for boolean. Private, coz used by the real getters
    protected static boolean readBoolean(String entry, boolean defaultValue) {
        boolean error = false;

        boolean ret = false;
        String get = DATA.getProperty(entry);

        if (get != null) {
            //Specially testing for "true" and "false", to reset value if garbage was found
            if (get.equalsIgnoreCase("true")) {
                ret = true;
            } else if (get.equalsIgnoreCase("false")) {
                ret = false;
            } else {
                //Garbage found
                error = true;
            }
        }

        if (error || get == null) {
            //reset setting to default value
            writeSetting(entry, String.valueOf(defaultValue));
            return defaultValue;
        }

        return ret;
    }

    //Generic getter for Color. Private, coz used by the real getters
    protected static Color readColor(String entry, Color defaultValue) {
        boolean error = false;

        Color ret = null;
        try {
            ret = new Color(Integer.parseInt(DATA.getProperty(entry)));
        } catch (Exception e) {
            error = true;
        }

        if (error || ret == null) {
            //reset setting to default value
            ret = defaultValue;
            writeSetting(entry, String.valueOf(defaultValue.getRGB()));
        }

        return ret;
    }

    /**
     * settings writer used by the real setters
     */
    protected static void writeSetting(String entry, String value) {
        //using this one, so everytime the settings get saved on a change
        DATA.setProperty(entry, value);
        save();
    }
}

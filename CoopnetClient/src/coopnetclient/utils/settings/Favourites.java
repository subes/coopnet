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

import coopnetclient.frames.FrameOrganizer;
import coopnetclient.utils.gamedatabase.GameDatabase;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Vector;

/**
 *
 * @author subes
 */
public final class Favourites {

    private static final Vector<String> FAVOURITES = new Vector<String>();
    private static final String FAVOURITES_FILE = SettingsHelper.SETTINGS_DIR;

    private Favourites(){}

    public static void addFavouriteByName(String channel) {
        String id = GameDatabase.getIDofGame(channel);
        if (!FAVOURITES.contains(id)) {
            FAVOURITES.add(id);
            saveFavourites();
        }
    }

    public static Vector<String> getFavouritesByID() {
        Vector<String> favs = new Vector<String>();
        favs.addAll(FAVOURITES);
        return favs;
    }

    public static Vector<String> getFavouritesByName() {
        Vector<String> favs = new Vector<String>();
        for (String id : FAVOURITES) {
            if (GameDatabase.getGameName(id) != null) {
                favs.add(GameDatabase.getGameName(id));
            }
        }
        Collections.sort(favs);
        return favs;
    }

    public static void removeFavourite(String id) {
        FAVOURITES.remove(id);
        saveFavourites();
    }

    protected static void saveFavourites() {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(FAVOURITES_FILE));
        } catch (Exception ex) {
        }
        for (String s : FAVOURITES) {
            pw.println(s);
        }
        pw.close();

        if (FrameOrganizer.getClientFrame() != null) {
            FrameOrganizer.getClientFrame().refreshFavourites();
        }
    }

    protected static void loadFavourites() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(FAVOURITES_FILE));
        } catch (FileNotFoundException ex) {
            return;
        }
        FAVOURITES.clear();
        //reading data
        Boolean done = false;
        String input;
        while (!done) {
            try {
                input = br.readLine();
                if (input == null) {
                    done = true;
                    continue;
                }
            } catch (IOException ex) {
                return;
            }
            if (input.length() == 3) {
                FAVOURITES.add(input);
            } else {
                FAVOURITES.add(GameDatabase.getIDofGame(input));
            }
        }
        try {
            br.close();
        } catch (Exception e) {
        }
    }

    protected static void resetFavourites() {
        FAVOURITES.clear();
        saveFavourites();
    }
}

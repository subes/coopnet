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
package coopnetclient.utils.gamedatabase;

import coopnetclient.Globals;
import coopnetclient.enums.LogTypes;
import coopnetclient.utils.Logger;
import java.io.File;
import java.io.IOException;
import javax.swing.SwingUtilities;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class XMLReader {

    public static final int LOAD_GAMEDATA = 1;
    public static final int DETECT_GAMES = 2;
    private String loadFrom;

    public XMLReader() {
        super();
    }

    public void parseGameData(String gamename, String datafilepath, int action) throws ParserConfigurationException, SAXException, IOException {
        loadFrom = datafilepath;
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        final SAXParser sp = spf.newSAXParser();
        switch (action) {
            case DETECT_GAMES:
                SwingUtilities.invokeLater(new Thread() {

                    @Override
                    public void run() {
                        try {
                            Logger.log(LogTypes.LOG, "Detecting games");
                            sp.parse(new File(loadFrom), new XmlHandler_DetectGames());
                            Logger.log(LogTypes.LOG, "Done detecting games");
                            if(Globals.getClientFrame()!=null){
                                Globals.getClientFrame().refreshInstalledGames();
                            }
                        } catch (Exception e) {
                            Logger.log(LogTypes.ERROR, "Game detection failed!");
                            Logger.log(e);
                        }
                    }
                });
                break;
            case LOAD_GAMEDATA:
                sp.parse(new File(loadFrom), new XmlHandler_LoadGameData(gamename));
                break;
        }
    }
}

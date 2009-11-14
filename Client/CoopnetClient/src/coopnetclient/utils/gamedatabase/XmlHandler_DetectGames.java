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

import coopnetclient.utils.RegistryReader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlHandler_DetectGames extends DefaultHandler{

    private String tmpID;
    private StringBuilder tempVal = new StringBuilder();
    private boolean inMod = false;
    
    public XmlHandler_DetectGames(){
    }

    //Event Handlers
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal.delete(0, tempVal.length());
        if (qName.equalsIgnoreCase("GameData")) {            
            inMod = false;
            tmpID = null;
        } else if (qName.equalsIgnoreCase("Mod")) {            
            inMod = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
         if (qName.equalsIgnoreCase("Mod")) {
            inMod = false;

        } else if (qName.equalsIgnoreCase("ChannelID")) {
            tmpID = tempVal.toString();
        } else if (qName.equalsIgnoreCase("RegKey")) {
            if (!inMod) {
                String path = RegistryReader.read(tempVal.toString());
                if(path!=null && path.length() >0){
                    GameDatabase.addIDToInstalledList(tmpID);
                }
            }
        } 
    }
}

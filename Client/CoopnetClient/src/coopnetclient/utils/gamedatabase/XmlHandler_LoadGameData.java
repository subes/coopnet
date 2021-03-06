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

import coopnetclient.enums.SettingTypes;
import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlHandler_LoadGameData extends DefaultHandler{

    private String gameName;
    //general var to hold data
    private StringBuilder tempVal = new StringBuilder();
    //setting specific data holders
    private String tmpSettingName;
    private SettingTypes tmpSettingType;
    private String tmpSettingKeyWord;
    private String tmpSettingDefVal;
    private String tmpSettingMinVal;
    private String tmpSettingMaxVal;
    private ArrayList<String> names;
    private ArrayList<String> values;
    private ArrayList<String> regkeys;
    private ArrayList<String> modregkeys;
    private boolean localSetting = false;
    //temp object holders
    private Game tmpGame;
    private Game tmpMod;
    private GameSetting tmpSetting;
    private String tmpID;
    private String propertyName = null;
    private String propertyValue = null;
    //state indicators
    private boolean beta = false;
    private boolean inMod = false;

    public XmlHandler_LoadGameData(String gameName){
        this.gameName = gameName;
    }

    //Event Handlers
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal.delete(0, tempVal.length());
        if (qName.equalsIgnoreCase("GameData")) {
            tmpGame = new Game();
            tmpGame.setInstantLauncable(false);
            beta = false;
            inMod = false;
            regkeys = new ArrayList<String>();
        } else if (qName.equalsIgnoreCase("Mod")) {
            tmpMod = new Game();
            modregkeys = new ArrayList<String>();
            inMod = true;
        } else if (qName.equalsIgnoreCase("ChoiceSetting") || qName.equalsIgnoreCase("TextSetting") || qName.equalsIgnoreCase("NumberSetting")) {
            localSetting = false;
            tmpSettingName = "";
            tmpSettingType = null;
            tmpSettingKeyWord = "";
            tmpSettingDefVal = "";
            tmpSettingMinVal = Integer.MIN_VALUE + "";
            tmpSettingMaxVal = Integer.MAX_VALUE + "";
            names = new ArrayList<String>();
            values = new ArrayList<String>();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("GameData")) {
            //register in gamedatabase
            GameDatabase.IDtoGameName.put(tmpID, tmpGame.getGameName());
            GameDatabase.IDtoLaunchMethod.put(tmpID, tmpGame.getLaunchMethod(null));
            if (gameName == null || tmpGame.getGameName().equals(gameName)) {
                if (GameDatabase.indexOfGame(tmpGame.getGameName()) > -1) { //remove old data
                    GameDatabase.gameData.remove(GameDatabase.indexOfGame(tmpGame.getGameName()));
                }
                GameDatabase.gameData.add(tmpGame);
                if (beta) {
                    GameDatabase.isExperimental.add(tmpID);
                }
            }
        } else if (qName.equalsIgnoreCase("Mod")) {
            inMod = false;
            tmpGame.addMod(tmpMod);
            tmpMod = null;
        } else if (qName.equalsIgnoreCase("GameName")) {
            if (inMod) {
                tmpMod.setGameName(tempVal.toString());
            } else {
                tmpGame.setGameName(tempVal.toString());
            }
        } else if (qName.equalsIgnoreCase("ShortName")) {
            if (inMod) {
                tmpMod.setShortName(tempVal.toString());
            } else {
                tmpGame.setShortName(tempVal.toString());
            }
        } else if (qName.equalsIgnoreCase("WelcomeMessage")) {
            if (inMod) {
                tmpMod.setWelcomeMessage(tempVal.toString());
            } else {
                tmpGame.setWelcomeMessage(tempVal.toString());
            }
        } else if (qName.equalsIgnoreCase("ChannelID")) {
            tmpID = tempVal.toString();
        } else if (qName.equalsIgnoreCase("LaunchMethod")) {
            tmpGame.setLaunchMethod(tempVal.toString());
        } else if (qName.equalsIgnoreCase("InstantLaunch")) {
            tmpGame.setInstantLauncable(true);
        } else if (qName.equalsIgnoreCase("Beta")) {//NoSpaces
            beta = true;
        } else if (qName.equalsIgnoreCase("NoSpaces")) {
            if (inMod) {
                tmpMod.setNoSpacesFlag(true);
            } else {
                tmpGame.setNoSpacesFlag(true);
            }
        } else if (qName.equalsIgnoreCase("RegKey")) {
            if (inMod) {
                modregkeys.add(tempVal.toString());
            }else{
                regkeys.add(tempVal.toString());
            }
        } else if (qName.equalsIgnoreCase("RegistryEntry")) {
            if (inMod) {
                tmpMod.setRegEntries(modregkeys);
            } else {
                tmpGame.setRegEntries(regkeys);
            }
        } else if (qName.equalsIgnoreCase("PropertyName")) {
            propertyName = tempVal.toString();
        } else if (qName.equalsIgnoreCase("PropertyValue")) {
            propertyValue = tempVal.toString();
        } else if (qName.equalsIgnoreCase("Property")) {
            if (inMod) {
                tmpMod.setFieldValue(propertyName, null, propertyValue);
            } else {
                tmpGame.setFieldValue(propertyName, null, propertyValue);
            }
            propertyValue = "";
            propertyName = "";
        } else if (qName.equalsIgnoreCase("PropertyValue")) {
            propertyValue = tempVal.toString();
        } else if (qName.equalsIgnoreCase("ChoiceSetting") || qName.equalsIgnoreCase("TextSetting") || qName.equalsIgnoreCase("NumberSetting")) {
            tmpSetting = new GameSetting(localSetting, tmpSettingName, tmpSettingType, tmpSettingKeyWord, tmpSettingDefVal);
            switch (tmpSettingType) {
                case CHOICE: {
                    tmpSetting.setComboboxSelectNames(names);
                    tmpSetting.setComboboxValues(values);
                    break;
                }
                case NUMBER: {
                    tmpSetting.setMinValue(Integer.valueOf(tmpSettingMinVal));
                    tmpSetting.setMaxValue(Integer.valueOf(tmpSettingMaxVal));
                    break;
                }
            }
            if (inMod) {
                tmpMod.addSetting(tmpSetting);
            } else {
                tmpGame.addSetting(tmpSetting);
            }
            tmpSetting = null;
        } else if (qName.equalsIgnoreCase("SettingName")) {
            tmpSettingName = tempVal.toString();
        } else if (qName.equalsIgnoreCase("KeyWord")) {
            tmpSettingKeyWord = tempVal.toString();
        } else if (qName.equalsIgnoreCase("IsLocalSetting")) {
            localSetting = true;
        } else if (qName.equalsIgnoreCase("ChoiceDisplayName")) {
            names.add(tempVal.toString());
        } else if (qName.equalsIgnoreCase("ChoiceRealValue")) {
            values.add(tempVal.toString());
        } else if (qName.equalsIgnoreCase("SettingDefaultValue")) {
            tmpSettingDefVal = tempVal.toString();
        } else if (qName.equalsIgnoreCase("SettingMinValue")) {
            tmpSettingMinVal = tempVal.toString();
        } else if (qName.equalsIgnoreCase("SettingMaxValue")) {
            tmpSettingMaxVal = tempVal.toString();
        }else if (qName.equalsIgnoreCase("SettingType")) {
            tmpSettingType = SettingTypes.valueOf(tempVal.toString());
        }
    }
}

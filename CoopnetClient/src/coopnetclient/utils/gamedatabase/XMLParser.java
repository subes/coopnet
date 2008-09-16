package coopnetclient.utils.gamedatabase;

import coopnetclient.enums.SettingTypes;
import java.util.ArrayList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class XMLParser extends DefaultHandler {

    private String gameName;
    private String loadFrom;
    //general var to hold data
    private String tempVal;
    //setting specific data holders
    private String tmpSettingName;
    private SettingTypes tmpSettingType;
    private String tmpSettingKeyWord;
    private String tmpSettingDefVal;
    private String tmpSettingMinVal;
    private String tmpSettingMaxVal;
    private ArrayList<String> names;
    private ArrayList<String> values;
    private boolean sharedSetting = false;
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

    public XMLParser(String gamename, String datafilepath) {
        this.gameName = gamename;
        loadFrom = datafilepath;
    }

    public void parseGameData() {
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser sp = spf.newSAXParser();
            sp.parse(loadFrom, this);
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    //Event Handlers
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("GameData")) {
            tmpGame = new Game();
            tmpGame.setInstantLauncable(false);
            beta = false;
            inMod = false;
        } else if (qName.equalsIgnoreCase("ChoiceItem")) {
            names = new ArrayList<String>();
            values = new ArrayList<String>();
        } else if (qName.equalsIgnoreCase("Mod")) {
            tmpMod = new Game();
            inMod = true;
        } else if (qName.equalsIgnoreCase("ChoiceSetting") || qName.equalsIgnoreCase("TextSetting") || qName.equalsIgnoreCase("NumberSetting")) {
            sharedSetting = false;
            tmpSettingName = "";
            tmpSettingType = null;
            tmpSettingKeyWord = "";
            tmpSettingDefVal = "";
            tmpSettingMinVal = Integer.MIN_VALUE + "";
            tmpSettingMaxVal = Integer.MAX_VALUE + "";
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("GameData")) {
            //register in gamedatabase
            GameDatabase.IDtoGameName.put(tmpID, tmpGame.getGameName());
            if (gameName == null || tmpGame.getGameName().equals(gameName)) {
                if (GameDatabase.indexOfGame(tmpGame.getGameName()) > -1) { //remove old data
                    GameDatabase.gameData.remove(GameDatabase.indexOfGame(tmpGame.getGameName()));
                }
                GameDatabase.gameData.add(tmpGame);
                if (beta) {
                    GameDatabase.isexperimental.add(tmpGame.getGameName());
                }
            }
        } else if (qName.equalsIgnoreCase("Mod")) {
            inMod = false;
            tmpGame.addMod(tmpMod);
            tmpMod = null;
        } else if (qName.equalsIgnoreCase("GameName")) {
            if (inMod) {
                tmpMod.setGameName(tempVal);
            } else {
                tmpGame.setGameName(tempVal);
            }
        } else if (qName.equalsIgnoreCase("ChannelID")) {
            tmpID = tempVal;
        } else if (qName.equalsIgnoreCase("LaunchMethod")) {
            tmpGame.setLaunchMethod(tempVal);
        } else if (qName.equalsIgnoreCase("InstantLaunch")) {
            tmpGame.setInstantLauncable(true);
        } else if (qName.equalsIgnoreCase("Beta")) {
            beta = true;
        } else if (qName.equalsIgnoreCase("PropertyName")) {
            propertyName = tempVal;
        } else if (qName.equalsIgnoreCase("PropertyValue")) {
            propertyValue = tempVal;
        } else if (qName.equalsIgnoreCase("Property")) {
            if (inMod) {
                tmpMod.setFieldValue(propertyName, null, propertyValue);
            } else {
                tmpGame.setFieldValue(propertyName, null, propertyValue);
            }
            propertyValue = "";
            propertyName = "";
        } else if (qName.equalsIgnoreCase("PropertyValue")) {
            propertyValue = tempVal;
        } else if (qName.equalsIgnoreCase("ChoiceSetting") || qName.equalsIgnoreCase("TextSetting") || qName.equalsIgnoreCase("NumberSetting")) {
            tmpSetting = new GameSetting(sharedSetting, tmpSettingName, tmpSettingType, tmpSettingKeyWord, tmpSettingDefVal);
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
            tmpSettingName = tempVal;
        } else if (qName.equalsIgnoreCase("KeyWord")) {
            tmpSettingKeyWord = tempVal;
        } else if (qName.equalsIgnoreCase("IsSharedSetting")) {
            sharedSetting = true;
        } else if (qName.equalsIgnoreCase("ChoiceDisplayName")) {
            names.add(tempVal);
        } else if (qName.equalsIgnoreCase("ChoiceRealValue")) {
            values.add(tempVal);
        } else if (qName.equalsIgnoreCase("SettingDefaultValue")) {
            tmpSettingDefVal = tempVal;
        } else if (qName.equalsIgnoreCase("SettingMinValue")) {
            tmpSettingMinVal = tempVal;
        } else if (qName.equalsIgnoreCase("SettingMaxValue")) {
            tmpSettingMaxVal = tempVal;
        }
    }
}

package coopnetclient.utils.gamedatabase;


import java.util.ArrayList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class XMLParser extends DefaultHandler {

    private String gameName;
    private String loadFrom;
    private String tempVal;
    private Game tmpGame;
    private ArrayList<String> names ;
    private ArrayList<String> values ;

    public XMLParser(String gamename, String datafilepath) {
        this.gameName = gamename;
        loadFrom = datafilepath;
        int idx = GameDatabase.indexOfGame(gamename);
        if (idx > -1) {
            GameDatabase.gameData.remove(idx);
        }
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
        } else if (qName.equalsIgnoreCase("ChoiceItem")) {
            names = new ArrayList<String>();
            values = new ArrayList<String>();
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
            GameDatabase.gameData.add(tmpGame);

        } else if (qName.equalsIgnoreCase("Name")) {
            //tempEmp.setName(tempVal);
        } else if (qName.equalsIgnoreCase("Id")) {
            //tempEmp.setId(Integer.parseInt(tempVal));
        } else if (qName.equalsIgnoreCase("Age")) {
            //tempEmp.setAge(Integer.parseInt(tempVal));
        }

    }
}





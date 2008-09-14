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

import coopnetclient.Client;
import coopnetclient.protocol.out.Protocol;
import java.util.ArrayList;
import coopnetclient.enums.SettingTypes;

public class GameSetting {   
    private String visibleName; //text of the label left to setting
    private SettingTypes type;       //setting tpye
    private String keyWord; //the keyword to replace in callString
    private boolean shared; //shared settings need to be sent to other roommembers
    private int minValue = Integer.MIN_VALUE;   //spinner property
    private int maxValue = Integer.MAX_VALUE;   //spinner property
    private String defaultValue;    //default value for all types
    private ArrayList<String> comboboxSelectNames = new ArrayList<String>();  //combobox property, the combobox model values
    private ArrayList<String> comboboxValues = new ArrayList<String>();      //combobox property, the value to be set(that replaces the keyword)
    private String currentValue = "";     //this is the actual setting at the given time(to replace the keyword on launch)

    public GameSetting(boolean shared, String name, SettingTypes type, String keyWord, String defaultValue) {
        this.visibleName = name;
        this.shared = shared;
        this.keyWord = keyWord;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public String getName(){
        return visibleName;
    }
    
    public void setName(String name){
        visibleName = name;
    }
    
    public String getKeyWord(){
        return keyWord;
    }
    
    public void setKeyWord(String keyword){
        this.keyWord = keyword;                
    }
    
    public String getDefaultValue(){
        return defaultValue;
    }
    
    public void setDefaultValue(String value){
        this.defaultValue = value;
    }
    
    public SettingTypes getType(){
        return type;
    }
    
    public void setType(SettingTypes type){
        this.type = type;
    }
    
    public int getMinValue(){
        return minValue;
    }
    
    public void setMinValue(int value){
        this.minValue = value;
    }
    
    public int getMaxValue(){
        return maxValue;
    }
    
    public void setMaxValue(int value){
        this.maxValue = value;
    }
    
    public boolean isShared(){
        return shared;
    }
    
    public void setShared(boolean val){
        shared = val;
    }
    
    public void reset(){
        currentValue = "";
    }
    
    public void setValue(String value,boolean broadcast) {
        switch (type) {
            case TEXT: {
                currentValue = value;
                break;
            }
            case CHOICE: {
                currentValue = comboboxValues.get(comboboxSelectNames.indexOf(value));
                break;
            }
            case NUMBER: {
                currentValue = value;
                break;
            }            
        }
        if(shared && broadcast){
            Client.send(Protocol.SendSetting(visibleName, value));
        }
    }
    
    public String getValue(){
        return currentValue;
    }

    public void setComboboxSelectNames(ArrayList<String> names) {
        comboboxSelectNames = new ArrayList<String>(names);
    }
    
    public ArrayList<String> getComboboxSelectNames(){
        return comboboxSelectNames;
    }

    public void setComboboxValues(ArrayList<String> values) {
        comboboxValues = new ArrayList<String>(values);
    }
    
    public ArrayList<String> getComboboxValues(){
        return comboboxValues;
    }
    
    @Override
    public String toString(){
        return visibleName;
    }
    
    protected String getStorageString(){
        String tmp = (shared?"shared":"") + visibleName + GameDatabase.SETTING_DELIMITER
                + type.toString() + GameDatabase.SETTING_DELIMITER
                + keyWord + GameDatabase.SETTING_DELIMITER
                +defaultValue ;
        switch (type) {            
            case CHOICE: {
                for(int i= 0; i< comboboxSelectNames.size();i++){
                    tmp += GameDatabase.SETTING_DELIMITER 
                            + comboboxSelectNames.get(i) + "=" + comboboxValues.get(i);
                }
                break;
            }
            case NUMBER: {
                tmp += GameDatabase.SETTING_DELIMITER + minValue 
                      +GameDatabase.SETTING_DELIMITER +maxValue;
                break;
            }            
        }
        return tmp;
    }
}

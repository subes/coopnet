/*	Copyright 2007 	Edwin Stang (edwinstang@gmail.com)

    This file is part of JDPlay.

    JDPlay is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JDPlay is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JDPlay.  If not, see <http://www.gnu.org/licenses/>.
*/

/****************************************************************************************************************/

/* Usage.java
 *  
 * 	This file demonstrates the usage of the JDPlay_jni.dll.
 *
 */

/****************************************************************************************************************/

package jdplay;
public class Usage {
    
    public static void main(String[] args) {
		//This loads the JDPlay_jni.dll on windows
		//On linux we have to use the JDPlay_rmt with wine or cedega
        System.loadLibrary("JDPlay_jni");
        
        /*
        //ip is ignored, when iamhost == true
        JDPlay jdplay = new JDPlay("subes", "{BC3A2ACD-FB46-4c6b-8B5C-CD193C9805CF}", "78.131.100.168", true, true);
        System.out.println("1. "+jdplay.isInitializedProperly());         
        jdplay.delete();
        
        jdplay = new JDPlay("subes", "{BC3A2ACD-FB46-4c6b-8B5C-CD193C9805CF}", "78.131.100.168", false, true);
        System.out.println("2. "+jdplay.isInitializedProperly());  
        jdplay.setPlayerName("invisible");
        jdplay.setMaxSearchRetries(0);
        if(jdplay.isInitializedProperly()){
            boolean noerror = jdplay.launch();
            System.out.println("joining: noerror = "+noerror);
        }
        jdplay.delete();
        */
        
        JDPlay jdplay = new JDPlay("subes", "{BC3A2ACD-FB46-4c6b-8B5C-CD193C9805CF}", "78.131.100.168", false, true);
        //System.out.println("3. "+jdplay.isInitializedProperly()+" -> launching and then relaunching");
        if(jdplay.isInitializedProperly()){
            boolean noerror = jdplay.launch(true);

            if(noerror){
                //System.out.println("all fine");
            }else{
                //System.out.println("check debug log for error, e.g. the code in [xxx] may show that a game is not installed or stuff");
            }  
            jdplay.setPlayerName("visible");
            //jdplay.launch();
        }
        jdplay.delete();
    }
    
}

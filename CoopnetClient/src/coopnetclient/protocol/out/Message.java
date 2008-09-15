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

package coopnetclient.protocol.out;

import coopnetclient.Client;
import coopnetclient.enums.ClientProtocolCommands;


public class Message {

    private String message;
    
    public Message(String message){
        StringBuilder sb = new StringBuilder(message);
        sb.append(Protocol.MESSAGE_DELIMITER);
        
        this.message = sb.toString();
        
        Client.send(this);
    }
    
    public Message(ClientProtocolCommands command){
        StringBuilder sb = new StringBuilder(command.ordinal());
        sb.append(Protocol.MESSAGE_DELIMITER);
        
        this.message = sb.toString();
        
        Client.send(this);
    }
    
    public Message(ClientProtocolCommands command, String information){
        initialize(command, information);
        Client.send(this);
    }
    
    public Message(ClientProtocolCommands command, String[] information){
        initialize(command, information);
        Client.send(this);
    }
    
    private void initialize(ClientProtocolCommands command, String information){
        StringBuilder sb = new StringBuilder(command.ordinal());
        sb.append(Protocol.INFORMATION_DELIMITER);
        sb.append(information);
        sb.append(Protocol.MESSAGE_DELIMITER);
        
        this.message = sb.toString();
    }
    
    private void initialize(ClientProtocolCommands command, String[] information){
        StringBuilder sb = new StringBuilder(command.ordinal());
        
        for(int i = 0; i < information.length; i++){
            sb.append(Protocol.INFORMATION_DELIMITER);
            sb.append(information[i]);
        }
        
        sb.append(Protocol.MESSAGE_DELIMITER);
        
        this.message = sb.toString();
    }
    
    public String getMessage(){
        return message;
    }
    
}

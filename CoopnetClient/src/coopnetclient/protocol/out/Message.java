package coopnetclient.protocol.out;

import coopnetclient.Client;
import coopnetclient.protocol.ClientProtocolCommands;
import coopnetclient.utils.gamedatabase.GameDatabase;

public class Message {

    private String message;
    
    public Message(String message){
        this.message = message + Protocol.MESSAGE_DELIMITER;
        Client.send(this);
    }
    
    public Message(ClientProtocolCommands command){
        this.message = command.ordinal() + Protocol.MESSAGE_DELIMITER;
        Client.send(this);
    }
    
    public Message(ClientProtocolCommands command, String information){
        initialize(command, information);
    }
    
    public Message(ClientProtocolCommands command, String[] information){
        initialize(command, information);
    }
    
    private void initialize(ClientProtocolCommands command, String information){
        StringBuilder sb = new StringBuilder();
        
        sb.append(command.ordinal());
        sb.append(Protocol.INFORMATION_DELIMITER);
        sb.append(information);
        sb.append(Protocol.MESSAGE_DELIMITER);
        
        this.message = sb.toString();
        
        Client.send(this);
    }
    
    private void initialize(ClientProtocolCommands command, String[] information){
        StringBuilder sb = new StringBuilder();
        
        sb.append(command.ordinal());
        
        for(int i = 0; i < information.length; i++){
            sb.append(Protocol.INFORMATION_DELIMITER);
            sb.append(information[i]);
        }
        
        sb.append(Protocol.MESSAGE_DELIMITER);
        
        this.message = sb.toString();
        
        Client.send(this);
    }
    
    public String getMessage(){
        return message;
    }
    
}


package coopnetclient.frames.components;

import coopnetclient.Globals;
import coopnetclient.enums.OperatingSystems;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Patroklos
 */


public final class HistoryLogger {
    
    private String me;
    private String partner;
    private List<String> messages;
    private PrintWriter out;
    private BufferedReader in;
    private String path;
    
    public HistoryLogger(String name, String partner) throws IOException{
        
        me = name;
        this.partner = partner;
        messages = new ArrayList<String>();
        
        if (Globals.getOperatingSystem() != OperatingSystems.LINUX) {
            path = System.getenv("APPDATA") + "/Coopnet";
        } else {
            final String home = System.getenv("HOME");
            path = home + "/.coopnet";
        }
        
        File f = new File(path+"/"+me+"_historylog");
        if (!f.exists()){
            f.mkdir();
        }
        
        
        
        try{
            in = new BufferedReader(new FileReader(path+"/"+me+"_historylog/"+this.partner+".log"));
            getPrevConversation();
            in.close();
        }catch (FileNotFoundException e){}
        
    }
    
    public void addMessage(String message) throws IOException{
        if(messages.size()<30){
            messages.add(message);
            out = new PrintWriter(new FileWriter(path+"/"+me+"_historylog/"+partner+".log",true));
            out.println(message);
            out.close();
        }else
            if(messages.size()==30){
                messages.remove(0);
                messages.add(message);
                out = new PrintWriter(new FileWriter(path+"/"+me+"_historylog/"+partner+".log"));
                for(int i=0;i<30;i++){
                    out.println(messages.get(i));
                }
                out.close();
            }
    }
    
    public void getPrevConversation() throws IOException{
        boolean EOF = false;
        while(!EOF){
            String temp = in.readLine();
            if(temp!=null){
                messages.add(temp);
            }
            else{
                EOF = true;
            }
        }
    }
    
    public boolean isEmpty(){
        if(messages.size()==0)
            return true;
        else
            return false;
    }
    
    public List<String> printall(){
        return messages;
    }
    
}

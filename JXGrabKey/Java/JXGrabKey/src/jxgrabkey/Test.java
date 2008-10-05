package jxgrabkey;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Test {

    public static void main(String[] args) throws IOException{
        File dir = new File(".");
        System.load(dir.getCanonicalPath()+"/libJXGrabKey.so");
        
        HotkeyListener listener = new HotkeyListener() {

            public void onHotkey(int id) {
                System.out.println("FIRED: "+id);
            }
        };
        
        JXGrabKey.getInstance().addHotkeyListener(listener);
        JXGrabKey.getInstance().registerHotkey(0, 0, 0);
        
        while(true){
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

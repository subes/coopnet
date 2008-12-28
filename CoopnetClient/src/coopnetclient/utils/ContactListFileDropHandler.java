package coopnetclient.utils;

import coopnetclient.Globals;
import coopnetclient.enums.ContactListElementTypes;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.frames.models.ContactListModel;
import coopnetclient.protocol.out.Protocol;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import javax.swing.JList;
import javax.swing.TransferHandler;

public class ContactListFileDropHandler extends TransferHandler {

    int action = TransferHandler.COPY; 

    public ContactListFileDropHandler() {        
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        if (!support.isDrop()) {
            return false;
        }

        // we only import files
        if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            return false;
        }
        
        //cant send to self:
        JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
        int index = dl.getIndex();
        JList list = (JList) support.getComponent();
        if (index < 0) {
            return false;
        }
        String subject = list.getModel().getElementAt(index).toString();
        if (subject == null || subject.equals(Globals.getThisPlayer_loginName())) {
            return false;
        }
        
        
        ContactListElementTypes status = Globals.getContactList().getStatus(subject);
        if(   !(   status == ContactListElementTypes.CHATTING 
                || status == ContactListElementTypes.IN_ROOM
                || status == ContactListElementTypes.PLAYING
                || status == ContactListElementTypes.PENDING_CONTACT )
                ){
            return false;
        }
        ContactListModel model = Globals.getContactList();
        if( status == ContactListElementTypes.PENDING_CONTACT && 
                !(model.groupOfContact(subject) != null )){
            return false;
        }

        boolean actionSupported = (action & support.getSourceDropActions()) == action;
        if (actionSupported) {
            support.setDropAction(action);
            return true;
        }

        return false;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        // if we can't handle the import, say so
        if (!canImport(support)) {
            return false;
        }

        // fetch the drop location
        JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();

        int index = dl.getIndex();
        Transferable t = support.getTransferable();
        try {
            java.util.List<File> l =
                    (java.util.List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
            JList list = (JList) support.getComponent();
            String subject = list.getModel().getElementAt(index).toString();
            if(subject == null || subject.equals(Globals.getThisPlayer_loginName())){
                return false;
            }
            for (File inputfile : l) {
                //process files
                Protocol.sendFile(subject, inputfile.getName(), inputfile.length() + "", coopnetclient.utils.Settings.getFiletTansferPort() + "");
                TabOrganizer.openFileTransferSendPanel(subject, inputfile);
            }
        } catch (UnsupportedFlavorException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
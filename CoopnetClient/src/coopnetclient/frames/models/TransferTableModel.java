/*  Copyright 2007  Edwin Stang (edwinstang@gmail.com),
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
package coopnetclient.frames.models;

import coopnetclient.Globals;
import coopnetclient.enums.LogTypes;
import coopnetclient.enums.TransferStatuses;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.frames.components.TransferStatusButtonComponent;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.utils.FileTransferHandler;
import coopnetclient.utils.Logger;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class TransferTableModel extends DefaultTableModel {

    public static final int RECIEVE_TYPE = 1;
    public static final int SEND_TYPE = 2;

    private class Transfer {

        private UUID ID;
        private int type;
        private String peerName;
        private String fileName;
        private TransferStatuses status;
        private TransferStatusButtonComponent render;
        private int progress = 0;
        private long timeLeft = 0;
        private int speed = 0;
        private FileTransferHandler handler;

        public Transfer(String peer, String filename, File sentFile) {
            this.type = SEND_TYPE;
            this.peerName = peer;
            this.fileName = filename;
            this.ID = UUID.randomUUID();
            this.handler = new FileTransferHandler(ID,peer, sentFile);
            this.status = TransferStatuses.Waiting;
            this.render = new TransferStatusButtonComponent();
        }

        public Transfer(String peerName, long size, String fileName, String ip, String port) {
            this.type = RECIEVE_TYPE;
            this.peerName = peerName;
            this.fileName = fileName;
            this.ID = UUID.randomUUID();
            this.status = TransferStatuses.Waiting;
            this.handler = new FileTransferHandler(ID, peerName, size, fileName, ip, port);
            this.render = new TransferStatusButtonComponent();
        }

        public void startSend(String ip, String port, long firstByte) {
            handler.startSend(ip, port, firstByte);
        }

        private void startRecieve() {
            handler.startRecieve();
        }
    }
    //end of inner class
    private String[] columnNames = {"Type", "Status", "Peer", "Filename",  "Progress", "Time left", "Speed"};
    private ArrayList<Transfer> transfers;


    {
        transfers = new ArrayList<Transfer>();
    }

    /** Creates a new instance of MyTableModel */
    public TransferTableModel() {
        super();
    }

    public boolean isAnyTransferActive(){
        for(Transfer t : transfers){
            if(t.status == TransferStatuses.Starting || t.status == TransferStatuses.Transferring){
                return true;
            }
        }
        return false;
    }

    public boolean addSendTransfer(String peer, String filename, File sentFile) {
        if (!findActiveSendTransfer(peer, filename)) {
            Transfer t = new Transfer(peer, filename, sentFile);
            transfers.add(0, t);
            fireTableRowsInserted(0,0);
            return true;
        } else {
            JOptionPane.showMessageDialog(Globals.getClientFrame(),
                    "You are already sending the file to " + peer + " !",
                    "Cannot send file!", JOptionPane.ERROR_MESSAGE);
            return false;
        }        
    }

    public void addRecieveTransfer(String sender, String size, String filename, String ip, String port) {
        Transfer t = new Transfer(sender, Long.valueOf(size), filename, ip, port);
        transfers.add(0, t);
        fireTableRowsInserted(0,0);
    }

    public void acceptFile(int index) {
        Transfer t = transfers.get(index);
        if (t.status == TransferStatuses.Waiting) {
            t.handler.startRecieve();
        }else{
            Logger.log(LogTypes.WARNING, "Cant accept file, bad status:" + t.status);
        }
        fireTableCellUpdated(index, 1);
    }

    public void refuseFile(int index) {
        Transfer t = transfers.get(index);
        if (t.status == TransferStatuses.Waiting) {
            Protocol.refuseTransfer(t.peerName, t.fileName);
            t.status = TransferStatuses.Refused;
        }
        fireTableCellUpdated(index, 1);
    }

    public void cancel(int index) {
        Transfer t = transfers.get(index);
        Protocol.cancelTransfer(t.peerName, t.fileName);
        t.handler.cancel();
        t.status = TransferStatuses.Cancelled;
        fireTableCellUpdated(index, 1);
    }

    public void startRecieve(int index) {
        transfers.get(index).startRecieve();
        fireTableCellUpdated(index, 1);
    }

    public void startSending(String ip, String peerName, String fileName, String port, long firstByte) {
        Transfer tf = null;
        for (Transfer t : transfers) {
            if (t.type == SEND_TYPE && t.peerName.equals(peerName) && t.fileName.equals(fileName) && (t.status == TransferStatuses.Waiting || t.status == TransferStatuses.Starting || t.status == TransferStatuses.Transferring)) {
                tf = t;
            }
        }
        if (tf != null) {
            tf.startSend(ip, port, firstByte);
            fireTableCellUpdated(transfers.indexOf(tf), 1);
        }
    }

    public void peerRefusedTransfer(String peerName, String fileName) {
        Transfer tf = null;
        for (Transfer t : transfers) {
            if (t.type == SEND_TYPE && t.peerName.equals(peerName) && t.fileName.equals(fileName) && t.status == TransferStatuses.Waiting) {
                tf = t;
            }
        }
        if (tf != null) {
            tf.status = TransferStatuses.Refused;
            fireTableCellUpdated(transfers.indexOf(tf), 1);
        }
    }

    public void peerCancelledTransfer(String peerName, String fileName) {
        Transfer tf = null;
        for (Transfer t : transfers) {
            if ( t.peerName.equals(peerName) && t.fileName.equals(fileName) &&(t.status == TransferStatuses.Waiting || t.status == TransferStatuses.Starting || t.status == TransferStatuses.Transferring || t.status == TransferStatuses.Error || t.status == TransferStatuses.Failed)) {
                tf = t;
            }
        }
        if (tf != null) {
            tf.status = TransferStatuses.Cancelled;
            tf.handler.cancel();
            fireTableCellUpdated(transfers.indexOf(tf), 1);
        }
    }

    public void turnAroundTransfer(String peerName, String fileName) {
        Transfer tf = null;
        for (Transfer t : transfers) {
            if (t.peerName.equals(peerName) && t.fileName.equals(fileName) && (t.status == TransferStatuses.Waiting || t.status == TransferStatuses.Starting || t.status == TransferStatuses.Transferring)) {
                tf = t;
            }
        }
        if (tf != null) {
            tf.status = TransferStatuses.Retrying;
            tf.handler.turnAround();
            fireTableCellUpdated(transfers.indexOf(tf), 1);
        }
    }

    public void cancelOrRefuseOnQuit() {
        for (Transfer t : transfers) {
            switch (t.status) {
                case Retrying:
                case Starting:
                case Transferring:
                    t.handler.cancel();
                    Protocol.cancelTransfer(t.peerName, t.fileName);
                    break;
                case Waiting:
                    if (t.type == SEND_TYPE) {
                        Protocol.cancelTransfer(t.peerName, t.fileName);
                    } else {
                        Protocol.refuseTransfer(t.peerName, t.fileName);
                    }
            }
            t.handler.cancel();
        }
    }

    public String getSavePath(UUID ID) {
        for (Transfer t : transfers) {
            if (t.ID.equals(ID)) {
                return t.handler.getSavePath();
            }
        }
        return null;
    }

    public String getSavePath(int index) {
        return transfers.get(index).handler.getSavePath();
    }

    public void setSavePath(int index, String value) {
        transfers.get(index).handler.setSavePath(value);
    }

    public TransferStatuses getTransferStatus(int index) {
        return transfers.get(index).status;
    }

    public File getDestFile(int index) throws IOException{
        switch(transfers.get(index).type){
            case RECIEVE_TYPE:
                return transfers.get(index).handler.getDestinationFile();
            case SEND_TYPE:
                return transfers.get(index).handler.getSentFile();
        }
        return null;
    }

    public void updateStatus(UUID ID, TransferStatuses status) {
        for (Transfer t : transfers) {
            if (t.ID.equals(ID) && t.status != TransferStatuses.Cancelled) {
                t.status = status;
                fireTableCellUpdated(transfers.indexOf(t), 1);
                TabOrganizer.markTab(TabOrganizer.getTransferPanel());
                return;
            }
        }
    }

    public void updateTime(UUID ID, long time) {
        for (Transfer t : transfers) {
            if (t.ID.equals(ID)) {
                t.timeLeft = time;
                fireTableCellUpdated(transfers.indexOf(t), 5);
                return;
            }
        }
    }

    public void updateProgress(UUID ID, int value) {
        for (Transfer t : transfers) {
            if (t.ID.equals(ID)) {
                if(t.progress < value){
                    t.progress = value;
                    fireTableCellUpdated(transfers.indexOf(t), 4);
                }
                return;
            }
        }
    }

    public void updateSpeed(UUID ID, int value) {
        for (Transfer t : transfers) {
            if (t.ID.equals(ID)) {
                t.speed = value;
                fireTableCellUpdated(transfers.indexOf(t), 6);
                return;
            }
        }
    }

    private boolean findActiveSendTransfer(String peer, String filename) {
        for (Transfer t : transfers) {
            if (t.type == SEND_TYPE && t.peerName.equals(peer) && t.fileName.equals(filename) && (t.status == TransferStatuses.Waiting || t.status == TransferStatuses.Starting || t.status == TransferStatuses.Transferring)) {
                return true;
            }
        }
        return false;
    }

    public long getFileSize(int idx) {
        return transfers.get(idx).handler.getFullSize();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        if (transfers != null) {
            return transfers.size();
        } else {
            return 0;
        }
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch (col) {
            case 0:
                return transfers.get(row).type;
            case 1:
                return transfers.get(row).render;
            case 2:
                return transfers.get(row).peerName;
            case 3:
                return transfers.get(row).fileName;
            case 4:
                return transfers.get(row).progress;
            case 5:
                return getTimeLeft(transfers.get(row).timeLeft);
            case 6:
                return transfers.get(row).speed + " KB/s";
        }
        return null;
    }

    @Override
    public Class getColumnClass(int col) {
        switch (col) {
            case 1:
                return TransferStatusButtonComponent.class;
            case 0:
                return Integer.class;
            case 4:
                return Float.class;
            default:
                return String.class;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        //table is not editable !
        fireTableCellUpdated(row, col);
    }

    public void removeTransfer(int index) {
        transfers.remove(index);
        fireTableRowsDeleted(index, index);
    }

    public void removeAllEndedTransfers(){
        for(int i = 0; i <transfers.size(); ){
            Transfer t = transfers.get(i);
            switch(t.status){
                case Cancelled:
                case Error:
                case Failed:
                case Finished:
                case Refused:
                    transfers.remove(t);
                    break;
                default:
                    ++i;
                    continue;
            }
        }
        fireTableDataChanged();
        TabOrganizer.getTransferPanel().UpdateDetails();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public void fireTableCellUpdated(int row, int col){
        super.fireTableCellUpdated(row, col);
        if(TabOrganizer.getTransferPanel()!=null &&col == 1){
            TabOrganizer.getTransferPanel().rowUpdated(row);
        }
    }

     @Override
    public void fireTableRowsDeleted(int start, int end){
        super.fireTableRowsDeleted(start, end);
        TabOrganizer.getTransferPanel().UpdateDetails();
    }

    public int getTransferType(int idx){
        return transfers.get(idx).type;
    }

    public boolean getResume(int idx){
        return transfers.get(idx).handler.getResuming();
    }

    public boolean setresume(int index, boolean value) {
        if (transfers.get(index).status != TransferStatuses.Transferring) {
            return transfers.get(index).handler.setResuming(value);
        }
        return false;
    }

    public void clear() {
        transfers.clear();
        fireTableDataChanged();
    }

    private String getTimeLeft(long timeInSeconds) {
        final int seconds;
        final int minutes;
        final long hours;
        seconds = (int) (timeInSeconds % 60);
        timeInSeconds = timeInSeconds / 60;   //scale to minutes
        minutes = (int) (timeInSeconds % 60);
        timeInSeconds = timeInSeconds / 60;
        hours = (int) timeInSeconds;
        return (hours + ":" + minutes + ":" + seconds);
    }
}

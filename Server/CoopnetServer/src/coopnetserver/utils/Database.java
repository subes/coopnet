package coopnetserver.utils;

import coopnetserver.data.player.ContactGroup;
import coopnetserver.data.player.Player;
import coopnetserver.enums.LogTypes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

public final class Database {

    private static final String CONNECTION = "jdbc:mysql://localhost:3306/coopnet";
    private static final String USER = "coopnet";
    private static final String PASSWORD = "c0OpnetS3Rv3RpW";
    
    private static Connection con;
    private static PreparedStatement stmt_LoginNameExists;
    private static PreparedStatement stmt_PIDExists;
    private static PreparedStatement stmt_GetLoginName;
    private static PreparedStatement stmt_GetPID;
    private static PreparedStatement stmt_GetPlayerDataByName;
    private static PreparedStatement stmt_GetPlayerDataByPID;
    private static PreparedStatement stmt_VerifyLogin;
    private static PreparedStatement stmt_UpdateEmail;
    private static PreparedStatement stmt_UpdateEmailPublicity;
    private static PreparedStatement stmt_UpdateWebsite;
    private static PreparedStatement stmt_UpdateCountry;
    private static PreparedStatement stmt_UpdatePassword;
    private static PreparedStatement stmt_UpdateIngameName;
    private static PreparedStatement stmt_UpdateLoginName;
    private static PreparedStatement stmt_UpdateLastLogin;
    private static PreparedStatement stmt_GetMutelist;
    private static PreparedStatement stmt_GetBanlist;
    private static PreparedStatement stmt_MutePlayer;
    private static PreparedStatement stmt_UnMutePlayer;
    private static PreparedStatement stmt_BanPlayer;
    private static PreparedStatement stmt_UnBanPlayer;
    private static PreparedStatement stmt_WhoMutedOrBannedMe;
    private static PreparedStatement stmt_GetContactList;
    private static PreparedStatement stmt_GetContactGroup;
    private static PreparedStatement stmt_GetContactRequests;
    private static PreparedStatement stmt_GetPendingContacts;
    private static PreparedStatement stmt_GetContactsWhoKnowMe;
    private static PreparedStatement stmt_UpdateGroupName;
    private static PreparedStatement stmt_CreateGroup;
    private static PreparedStatement stmt_GetGroupIDByName;
    private static PreparedStatement stmt_DeleteGroup;
    private static PreparedStatement stmt_AddContactRequest;
    private static PreparedStatement stmt_SetContactAccepted;
    private static PreparedStatement stmt_RemoveContact;
    private static PreparedStatement stmt_MoveContactToGroup;
    private static PreparedStatement stmt_CreatePlayer;

    private Database(){}

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connectToDB();
        } catch (Exception e) {
            Logger.log(e);
        }
    }

    private static void connectToDB() {
        try {
            Logger.log(LogTypes.LOG, "Connecting to DB...");
            if (con != null) {
                con.close();
            }
            con = DriverManager.getConnection(CONNECTION, USER, PASSWORD);
            stmt_LoginNameExists = con.prepareStatement("CALL loginNameExists(?)");
            stmt_PIDExists = con.prepareStatement("CALL pidExists(?)");
            stmt_GetLoginName = con.prepareStatement("CALL getLoginName(?)");
            stmt_GetPID = con.prepareStatement("CALL getPID(?)");
            stmt_GetPlayerDataByName = con.prepareStatement("CALL getPlayerDataByName(?)");
            stmt_GetPlayerDataByPID = con.prepareStatement("CALL getPlayerDataByID(?)");
            stmt_VerifyLogin = con.prepareStatement("CALL verifyLogin(?,?)");
            stmt_UpdateEmail = con.prepareStatement("CALL updateEmail(?,?)");
            stmt_UpdateEmailPublicity = con.prepareStatement("CALL updateEmailIsPublic(?,?)");
            stmt_UpdateWebsite = con.prepareStatement("CALL updateWebsite(?,?)");
            stmt_UpdateCountry = con.prepareStatement("CALL updateCountry(?,?)");
            stmt_UpdatePassword = con.prepareStatement("CALL updatePassword(?,?)");
            stmt_UpdateIngameName = con.prepareStatement("CALL updateIngameName(?,?)");
            stmt_UpdateLoginName = con.prepareStatement("CALL updateLoginName(?,?)");
            stmt_UpdateLastLogin = con.prepareStatement("CALL updateLastLogin(?)");
            stmt_GetMutelist = con.prepareStatement("CALL getMuteList(?)");
            stmt_GetBanlist = con.prepareStatement("CALL getBanList(?)");
            stmt_MutePlayer = con.prepareStatement("CALL mutePlayer(?,?)");
            stmt_UnMutePlayer = con.prepareStatement("CALL unMutePlayer(?,?)");
            stmt_BanPlayer = con.prepareStatement("CALL banPlayer(?,?)");
            stmt_UnBanPlayer = con.prepareStatement("CALL unBanPlayer(?,?)");
            stmt_WhoMutedOrBannedMe = con.prepareStatement("CALL whoMutedOrBannedMe(?)");
            stmt_GetContactList = con.prepareStatement("CALL getContactList(?)");
            stmt_GetContactGroup = con.prepareStatement("CALL getContactGroup(?,?)");
            stmt_GetContactRequests = con.prepareStatement("CALL getContactRequests(?)");
            stmt_GetPendingContacts = con.prepareStatement("CALL getPendingContacts(?)");
            stmt_GetContactsWhoKnowMe = con.prepareStatement("CALL getContactsWhoKnowMe(?)");
            stmt_UpdateGroupName = con.prepareStatement("CALL updateGroupName(?,?,?)");
            stmt_CreateGroup = con.prepareStatement("CALL createGroup(?,?)");
            stmt_DeleteGroup = con.prepareStatement("CALL deleteGroup(?,?)");
            stmt_AddContactRequest = con.prepareStatement("CALL addContactRequest(?,?,?)");
            stmt_SetContactAccepted = con.prepareStatement("CALL setContactAccepted(?,?)");
            stmt_RemoveContact = con.prepareStatement("CALL removeContact(?,?)");
            stmt_MoveContactToGroup = con.prepareStatement("CALL moveContactToGroup(?,?,?)");
            stmt_CreatePlayer = con.prepareStatement("CALL createPlayer(?,?,?,?,?,?)");
            stmt_GetGroupIDByName = con.prepareStatement("CALL getGroupIDByName(?,?)");            
            Logger.log(LogTypes.LOG, "DB connection established");
        } catch (SQLException e) {
            con = null;            
            Logger.log(LogTypes.ERROR, "DB connection failed");
            //Logger.log(e);
        }
    }

    public static void ensureConnection() throws SQLException {
        if (con == null || !con.isValid(5)) {
            connectToDB();
        }
        if(con == null){
            throw new SQLException("Can't connect to DB!");
        }
    }

    private static synchronized ResultSet executeQuery(PreparedStatement stmt) throws SQLException {
        try {
            return stmt.executeQuery();
        } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException exc) {
            Logger.log(LogTypes.ERROR, "DB link failure, reconnecting and retrying!");
            //Logger.log(exc);
            connectToDB();
            return stmt.executeQuery();
        }
    }

    public static boolean loginNameExists(String loginName) throws SQLException {
        ensureConnection();
        stmt_LoginNameExists.setString(1, loginName);
        ResultSet res = executeQuery(stmt_LoginNameExists);

        if(!res.first()){return false;}
        return res.getBoolean(1);
    }

    public static boolean pidExists(long pid) throws SQLException {
        ensureConnection();
        stmt_PIDExists.setLong(1, pid);
        ResultSet res = executeQuery(stmt_PIDExists);

        if(!res.first()){return false;}
        return res.getBoolean(1);
    }

    public static String getLoginName(long pid) throws SQLException {
        ensureConnection();
        stmt_GetLoginName.setLong(1, pid);
        ResultSet res = executeQuery(stmt_GetLoginName);

        if(!res.first()){return null;}
        return res.getString(1);
    }
    
    public static long getPID(String loginname) throws SQLException {
        ensureConnection();
        stmt_GetPID.setString(1, loginname);
        ResultSet res = executeQuery(stmt_GetPID);

        if(!res.first()){return -1;}
        return res.getLong(1);
    }

    public static String[] getPlayerDataByName(String loginName) throws SQLException {
        ensureConnection();
        stmt_GetPlayerDataByName.setString(1, loginName);
        ResultSet res = executeQuery(stmt_GetPlayerDataByName);

        if(!res.first()){return null;}

        String[] ret = new String[res.getMetaData().getColumnCount()];
        for (int i = 1; i <= res.getMetaData().getColumnCount(); i++) {
            ret[i - 1] = res.getString(i);
        }
        return ret;
    }

    public static String[] getPlayerDataByID(long pid) throws SQLException {
        ensureConnection();
        stmt_GetPlayerDataByPID.setLong(1, pid);
        ResultSet res = executeQuery(stmt_GetPlayerDataByPID);

        if(!res.first()){return null;}

        String[] ret = new String[res.getMetaData().getColumnCount()];
        for (int i = 1; i <= res.getMetaData().getColumnCount(); i++) {
            ret[i - 1] = res.getString(i);
        }
        return ret;
    }

    public static boolean verifyLogin(String loginName, String password) throws SQLException {
        ensureConnection();
        stmt_VerifyLogin.setString(1, loginName);
        stmt_VerifyLogin.setString(2, password);
        ResultSet res = executeQuery(stmt_VerifyLogin);

        if(!res.first()){return false;}
        return res.getBoolean(1);
    }

    public static void updateEmail(Player player, String email) throws SQLException {
        ensureConnection();
        stmt_UpdateEmail.setLong(1, player.getPid());
        stmt_UpdateEmail.setString(2, email);
        executeQuery(stmt_UpdateEmail);
    }

    public static void updateEmailIsPublic(Player player, boolean emailispublic) throws SQLException {
        ensureConnection();
        stmt_UpdateEmailPublicity.setLong(1, player.getPid());
        stmt_UpdateEmailPublicity.setBoolean(2, emailispublic);
        executeQuery(stmt_UpdateEmailPublicity);
    }

    public static void updateWebsite(Player player, String website) throws SQLException {
        ensureConnection();
        stmt_UpdateWebsite.setLong(1, player.getPid());
        stmt_UpdateWebsite.setString(2, website);
        executeQuery(stmt_UpdateWebsite);
    }

    public static void updateCountry(Player player, String country) throws SQLException {
        ensureConnection();
        stmt_UpdateCountry.setLong(1, player.getPid());
        stmt_UpdateCountry.setString(2, country);
        executeQuery(stmt_UpdateCountry);
    }

    public static void updatePassword(long pid, String password) throws SQLException {
        ensureConnection();
        stmt_UpdatePassword.setLong(1, pid);
        stmt_UpdatePassword.setString(2, password);
        executeQuery(stmt_UpdatePassword);
    }

    public static void updateIngameName(Player player, String ingameName) throws SQLException {
        ensureConnection();
        stmt_UpdateIngameName.setLong(1, player.getPid());
        stmt_UpdateIngameName.setString(2, ingameName);
        executeQuery(stmt_UpdateIngameName);
    }

    public static void updateLoginName(Player player, String loginName) throws SQLException {
        ensureConnection();
        stmt_UpdateLoginName.setLong(1, player.getPid());
        stmt_UpdateLoginName.setString(2, loginName);
        executeQuery(stmt_UpdateLoginName);
    }

    public static void updateLastLogin(Player player) throws SQLException {
        ensureConnection();
        stmt_UpdateLastLogin.setLong(1, player.getPid());
        executeQuery(stmt_UpdateLastLogin);
    }

    public static Vector<Long> getMuteList(Player player) throws SQLException {
        ensureConnection();
        stmt_GetMutelist.setLong(1, player.getPid());
        ResultSet res = executeQuery(stmt_GetMutelist);

        Vector<Long> muteList = new Vector<Long>();

        while (res.next()) {
            muteList.add(res.getLong(1));
        }

        return muteList;
    }

    public static Vector<Long> getBanList(Player player) throws SQLException {
        ensureConnection();
        stmt_GetBanlist.setLong(1, player.getPid());
        ResultSet res = executeQuery(stmt_GetBanlist);

        Vector<Long> banList = new Vector<Long>();

        while (res.next()) {
            banList.add(res.getLong(1));
        }

        return banList;
    }

    public static void mutePlayer(Player player, long mutedid) throws SQLException {
        ensureConnection();
        stmt_MutePlayer.setLong(1, player.getPid());
        stmt_MutePlayer.setLong(2, mutedid);
        executeQuery(stmt_MutePlayer);
    }

    public static void unMutePlayer(Player player, long unmutedid) throws SQLException {
        ensureConnection();
        stmt_UnMutePlayer.setLong(1, player.getPid());
        stmt_UnMutePlayer.setLong(2, unmutedid);
        executeQuery(stmt_UnMutePlayer);
    }

    public static void banPlayer(Player player, long bannedid) throws SQLException {
        ensureConnection();
        stmt_BanPlayer.setLong(1, player.getPid());
        stmt_BanPlayer.setLong(2, bannedid);
        executeQuery(stmt_BanPlayer);
    }

    public static void unBanPlayer(Player player, long unbannedid) throws SQLException {
        ensureConnection();
        stmt_UnBanPlayer.setLong(1, player.getPid());
        stmt_UnBanPlayer.setLong(2, unbannedid);
        executeQuery(stmt_UnBanPlayer);
    }

    public static ArrayList<Long> whoMutedOrBannedMe(Player player) throws SQLException {
        ensureConnection();
        stmt_WhoMutedOrBannedMe.setLong(1, player.getPid());
        ResultSet res = executeQuery(stmt_WhoMutedOrBannedMe);

        ArrayList<Long> whoMutedOrBannedMe = new ArrayList<Long>();

        while (res.next()) {
            whoMutedOrBannedMe.add(res.getLong(1));
        }

        return whoMutedOrBannedMe;
    }

    public static ArrayList<ContactGroup> getContactList(Player player) throws SQLException {
        ensureConnection();
        stmt_GetContactList.setLong(1, player.getPid());
        ResultSet res = executeQuery(stmt_GetContactList);

        //data is: [<gid>, <groupname>, <contactid>] ordered by gid
        ArrayList<ContactGroup> contactGroups = new ArrayList<ContactGroup>();

        if(!res.first()){return null;}

        boolean wasLast = false;
        byte gid = -1;
        String groupName = null;
        ArrayList<Long> contacts = null;
        while (!wasLast) {
            
            if (groupName == null) {
                gid = res.getByte(1);
                groupName = res.getString(2);
                contacts = new ArrayList<Long>();
            }

            if (res.getString(3) != null) {
                contacts.add(res.getLong(3));
            }
            
            wasLast = res.isLast();
            
            res.next();
            
            if (wasLast || gid != res.getByte(1)) {
                contactGroups.add(new ContactGroup(player, gid, groupName, contacts));
                groupName = null;
            }
        }
        
        /*
        for(int i = 0; i < contactGroups.size(); i++){
            Logger.log(LogTypes.LOG, "GROUP: "+contactGroups.get(i).getGroupName());
            for(int j = 0; j < contactGroups.get(i).getContacts().length; j++){
                Logger.log(LogTypes.LOG, contactGroups.get(i).getContacts()[j]);
            }
        }*/

        return contactGroups;
    }

    public static ArrayList<Long> getContactGroup(Player player, byte gid) throws SQLException {
        ensureConnection();
        stmt_GetContactGroup.setLong(1, player.getPid());
        stmt_GetContactGroup.setByte(2, gid);
        ResultSet res = executeQuery(stmt_GetContactGroup);

        ArrayList<Long> contacts = new ArrayList<Long>();

        while (res.next()) {
            contacts.add(res.getLong(1));
        }

        return contacts;
    }

    public static ArrayList<Long> getContactRequests(Player player) throws SQLException {
        ensureConnection();
        stmt_GetContactRequests.setLong(1, player.getPid());
        ResultSet res = executeQuery(stmt_GetContactRequests);

        ArrayList<Long> requests = new ArrayList<Long>();

        while (res.next()) {
            requests.add(res.getLong(1));
        }

        return requests;
    }

    public static ArrayList<Long> getPendingContacts(Player player) throws SQLException {
        ensureConnection();
        stmt_GetPendingContacts.setLong(1, player.getPid());
        ResultSet res = executeQuery(stmt_GetPendingContacts);

        ArrayList<Long> pending = new ArrayList<Long>();

        while (res.next()) {
            pending.add(res.getLong(1));
        }

        return pending;
    }

    public static ArrayList<Long> getContactsWhoKnowMe(Player player) throws SQLException {
        ensureConnection();
        stmt_GetContactsWhoKnowMe.setLong(1, player.getPid());
        ResultSet res = executeQuery(stmt_GetContactsWhoKnowMe);

        ArrayList<Long> knowme = new ArrayList<Long>();

        while (res.next()) {
            knowme.add(res.getLong(1));
        }

        return knowme;
    }

    public static void updateGroupName(Player player, byte gid, String groupName) throws SQLException {
        ensureConnection();
        stmt_UpdateGroupName.setLong(1, player.getPid());
        stmt_UpdateGroupName.setByte(2, gid);
        stmt_UpdateGroupName.setString(3, groupName);
        executeQuery(stmt_UpdateGroupName);
    }

    public static ContactGroup createGroup(Player player, String groupName) throws SQLException {
        ensureConnection();
        stmt_CreateGroup.setLong(1, player.getPid());
        stmt_CreateGroup.setString(2, groupName);
        executeQuery(stmt_CreateGroup);

        stmt_GetGroupIDByName.setLong(1, player.getPid());
        stmt_GetGroupIDByName.setString(2, groupName);
        ResultSet res = executeQuery(stmt_GetGroupIDByName);

        if(!res.first()){return null;}
        byte newGid = res.getByte(1);
        return new ContactGroup(player, newGid, groupName);
    }

    public static void deleteGroup(Player player, byte gid) throws SQLException {
        ensureConnection();

        stmt_DeleteGroup.setLong(1, player.getPid());
        stmt_DeleteGroup.setByte(2, gid);
        executeQuery(stmt_DeleteGroup);
    }

    public static void addContactRequest(Player player, long contactid, byte gid) throws SQLException {
        ensureConnection();
        stmt_AddContactRequest.setLong(1, player.getPid());
        stmt_AddContactRequest.setLong(2, contactid);
        stmt_AddContactRequest.setByte(3, gid);
        executeQuery(stmt_AddContactRequest);
    }

    public static void setContactAccepted(Long playerID, long contactid) throws SQLException {
        ensureConnection();
        stmt_SetContactAccepted.setLong(1, playerID);
        stmt_SetContactAccepted.setLong(2, contactid);
        executeQuery(stmt_SetContactAccepted);
    }

    public static void removeContact(Long playerID, long contactid) throws SQLException {
        ensureConnection();
        stmt_RemoveContact.setLong(1, playerID);
        stmt_RemoveContact.setLong(2, contactid);
        executeQuery(stmt_RemoveContact);
    }

    public static void moveContactToGroup(Player player, long contactid, byte gid) throws SQLException {
        ensureConnection();
        stmt_MoveContactToGroup.setLong(1, player.getPid());
        stmt_MoveContactToGroup.setLong(2, contactid);
        stmt_MoveContactToGroup.setByte(3, gid);
        executeQuery(stmt_MoveContactToGroup);
    }

    public static void createPlayer(String loginName, String password, String email, String ingameName, String country, String website) throws SQLException {
        ensureConnection();
        stmt_CreatePlayer.setString(1, loginName);
        stmt_CreatePlayer.setString(2, password);
        stmt_CreatePlayer.setString(3, email);
        stmt_CreatePlayer.setString(4, ingameName);
        stmt_CreatePlayer.setString(5, website);
        stmt_CreatePlayer.setString(6, country);
        executeQuery(stmt_CreatePlayer);
    }
}

DELIMITER $$

USE coopnet;$$

-------------------------PROCEDURES-----------------------

DROP PROCEDURE IF EXISTS loginNameExists;$$
DROP PROCEDURE IF EXISTS pidExists;$$
DROP PROCEDURE IF EXISTS getLoginName;$$
DROP PROCEDURE IF EXISTS getPID;$
DROP PROCEDURE IF EXISTS getPlayerDataByName;$$
DROP PROCEDURE IF EXISTS getPlayerDataByID;$$
DROP PROCEDURE IF EXISTS verifyLogin;$$
DROP PROCEDURE IF EXISTS updateEmail;$$
DROP PROCEDURE IF EXISTS updateEmailIsPublic;$$
DROP PROCEDURE IF EXISTS updateWebsite;$$
DROP PROCEDURE IF EXISTS updateCountry;$$
DROP PROCEDURE IF EXISTS updatePassword;$$
DROP PROCEDURE IF EXISTS updateIngameName;$$
DROP PROCEDURE IF EXISTS updateLoginName;$$
DROP PROCEDURE IF EXISTS updateLastLogin;$$
DROP PROCEDURE IF EXISTS getMuteList;$$
DROP PROCEDURE IF EXISTS getBanList;$$
DROP PROCEDURE IF EXISTS mutePlayer;$$
DROP PROCEDURE IF EXISTS unMutePlayer;$$
DROP PROCEDURE IF EXISTS banPlayer;$$
DROP PROCEDURE IF EXISTS unBanPlayer;$$
DROP PROCEDURE IF EXISTS whoMutedOrBannedMe;$$
DROP PROCEDURE IF EXISTS getContactList;$$
DROP PROCEDURE IF EXISTS getContactGroup;$$
DROP PROCEDURE IF EXISTS getContactRequests;$$
DROP PROCEDURE IF EXISTS getPendingContacts;$$
DROP PROCEDURE IF EXISTS getContactsWhoKnowMe;$$
DROP PROCEDURE IF EXISTS updateGroupName;$$
DROP PROCEDURE IF EXISTS createGroup;$$
DROP PROCEDURE IF EXISTS getGroupIDByName;$$
DROP PROCEDURE IF EXISTS deleteGroup;$$
DROP PROCEDURE IF EXISTS addContactRequest;$$
DROP PROCEDURE IF EXISTS setContactAccepted;$$
DROP PROCEDURE IF EXISTS removeContact;$$
DROP PROCEDURE IF EXISTS moveContactToGroup;$$
DROP PROCEDURE IF EXISTS createPlayer;$$

CREATE PROCEDURE loginNameExists(loginname VARCHAR(30))
BEGIN 
    SELECT COUNT(*)
    FROM players p
    WHERE p.loginname = loginname;
END;$$

CREATE PROCEDURE pidExists(pid BIGINT)
BEGIN 
    SELECT COUNT(*)
    FROM players p
    WHERE p.pid = pid;
END;$$

CREATE PROCEDURE getLoginName(pid BIGINT)
BEGIN
    SELECT p.loginname
    FROM players p
    WHERE p.pid = pid;
END;$$

CREATE PROCEDURE getPID(loginname VARCHAR(30))
BEGIN
    SELECT p.pid
    FROM players p
    WHERE p.loginname = loginname;
END;$$

CREATE PROCEDURE getPlayerDataByName(loginname VARCHAR(30))
BEGIN
    SELECT p.pid, p.loginname, p.ingamename, p.password, p.email, p.website, p.country
    FROM players p
    WHERE p.loginname = loginname;
END;$$

CREATE PROCEDURE getPlayerDataByID(pid BIGINT)
BEGIN
    SELECT p.pid, p.loginname, p.ingamename, p.password, p.email, p.website, p.country
    FROM players p
    WHERE p.pid = pid;
END;$$

CREATE PROCEDURE verifyLogin(loginname VARCHAR(30), password VARCHAR(40))
BEGIN
    SELECT COUNT(*)
    FROM players p
    WHERE p.loginname = loginname
    AND p.password = password;
END;$$

CREATE PROCEDURE updateEmail(pid BIGINT, email VARCHAR(320))
BEGIN
    UPDATE players p
    SET p.email = email
    WHERE p.pid = pid;
END;$$

CREATE PROCEDURE updateWebsite(pid BIGINT, website VARCHAR(320))
BEGIN
    UPDATE players p
    SET p.website = website
    WHERE p.pid = pid;
END;$$

CREATE PROCEDURE updateCountry(pid BIGINT, country VARCHAR(60))
BEGIN
    UPDATE players p
    SET p.country = country
    WHERE p.pid = pid;
END;$$

CREATE PROCEDURE updatePassword(pid BIGINT, password VARCHAR(40))
BEGIN
    UPDATE players p
    SET p.password = password
    WHERE p.pid = pid;
END;$$

CREATE PROCEDURE updateIngameName(pid BIGINT, ingamename VARCHAR(30))
BEGIN
    UPDATE players p
    SET p.ingamename = ingamename
    WHERE p.pid = pid;
END;$$

CREATE PROCEDURE updateLoginName(pid BIGINT, loginname VARCHAR(30))
BEGIN
    UPDATE players p
    SET p.loginname = loginname
    WHERE p.pid = pid;
END;$$

CREATE PROCEDURE updateLastLogin(pid BIGINT)
BEGIN
    UPDATE players p
    SET p.lastlogin = SYSDATE()
    WHERE p.pid = pid;
END;$$

CREATE PROCEDURE getMuteList(pid BIGINT)
BEGIN
    SELECT m.mutedid
    FROM mutelists m
    WHERE m.whoselistid = pid;
END;$$

CREATE PROCEDURE getBanList(pid BIGINT)
BEGIN
    SELECT b.bannedid
    FROM banlists b
    WHERE b.whoselistid = pid;
END;$$

CREATE PROCEDURE mutePlayer(pid BIGINT, mutedid BIGINT)
BEGIN
    INSERT INTO mutelists VALUES (pid, mutedid);
END;$$

CREATE PROCEDURE unMutePlayer(pid BIGINT, unmutedid BIGINT)
BEGIN
    DELETE FROM mutelists
    WHERE mutelists.whoselistid = pid
    AND mutelists.mutedid = unmutedid;
END;$$

CREATE PROCEDURE banPlayer(pid BIGINT, bannedid BIGINT)
BEGIN
    INSERT INTO banlists VALUES (pid, bannedid);
END;$$

CREATE PROCEDURE unBanPlayer(pid BIGINT, unbannedid BIGINT)
BEGIN
    DELETE FROM banlists
    WHERE banlists.whoselistid = pid
    AND banlists.bannedid = unbannedid;
END;$$

CREATE PROCEDURE whoMutedOrBannedMe(pid BIGINT)
BEGIN
    SELECT DISTINCT b.whoselistid
    FROM banlists b
    WHERE b.bannedid = pid
    UNION DISTINCT SELECT m.whoselistid
    FROM mutelists m
    WHERE m.mutedid = pid;
END;$$

CREATE PROCEDURE getContactList(pid BIGINT)
BEGIN
    SELECT g.gid,  g.groupname, c.contactid
    FROM grouplists g
    LEFT OUTER JOIN contactlists c
    ON (g.whoselistid = c.whoselistid AND g.gid = c.contactsgroup)
    WHERE g.whoselistid = pid
    ORDER BY g.gid;
END;$$

CREATE PROCEDURE getContactGroup(pid BIGINT, gid TINYINT)
BEGIN
    SELECT c.contactid, g.groupname, g.gid
    FROM contactlists c, grouplists g
    WHERE c.whoselistid = pid
    AND g.gid = gid
    AND c.whoselistid = g.whoselistid
    AND c.contactsgroup = g.gid
    ORDER BY g.gid;
END;$$


CREATE PROCEDURE getContactRequests(pid BIGINT)
BEGIN
    SELECT c.whoselistid
    FROM contactlists c
    WHERE c.contactid = pid
    AND c.contactstatus = 'Requested';
END;$$

CREATE PROCEDURE getPendingContacts(pid BIGINT)
BEGIN
    SELECT c.contactid
    FROM contactlists c
    WHERE c.whoselistid = pid
    AND c.contactstatus = 'Requested';
END;$$

CREATE PROCEDURE getContactsWhoKnowMe(pid BIGINT)
BEGIN
    SELECT c.whoselistid
    FROM contactlists c
    WHERE c.contactid = pid
    AND c.contactstatus = 'Accepted';
END;$$

CREATE PROCEDURE updateGroupName(pid BIGINT, gid TINYINT, groupname VARCHAR(30))
BEGIN
    UPDATE grouplists g
    SET g.groupname = groupname
    WHERE g.gid = gid
    AND g.whoselistid = pid;
END;$$

CREATE PROCEDURE createGroup(pid BIGINT, groupname VARCHAR(30))
BEGIN
    INSERT INTO grouplists VALUES (pid, 0, groupname);
END;$$

CREATE PROCEDURE getGroupIDByName(pid BIGINT, groupname VARCHAR(30))
BEGIN
    SELECT g.gid
    FROM grouplists g
    WHERE g.whoselistid = pid
    AND g.groupname = groupname;
END;$$

CREATE PROCEDURE deleteGroup(pid BIGINT, gid TINYINT)
BEGIN
    IF (gid = 0) THEN
        CALL default_group_SHOULD_NOT_BE_DELETED();
    END IF;

    UPDATE contactlists c
    SET c.contactsgroup = 0
    WHERE c.whoselistid = pid
    AND c.contactsgroup = gid;

    DELETE FROM grouplists
    WHERE grouplists.whoselistid = pid
    AND grouplists.gid = gid;
END;$$

CREATE PROCEDURE addContactRequest(pid BIGINT, contactid BIGINT, gid TINYINT)
BEGIN
    INSERT INTO contactlists VALUES (pid, contactid, gid, 'Requested');
END;$$

CREATE PROCEDURE setContactAccepted(pid BIGINT, contactid BIGINT)
BEGIN
    UPDATE contactlists c
    SET c.contactstatus = 'Accepted'
    WHERE c.whoselistid = pid
    AND c.contactid = contactid;
END;$$

CREATE PROCEDURE removeContact(pid BIGINT, contactid BIGINT)
BEGIN
    DELETE FROM contactlists
    WHERE contactlists.whoselistid = pid
    AND contactlists.contactid = contactid;
END;$$

CREATE PROCEDURE moveContactToGroup(pid BIGINT, contactid BIGINT, gid TINYINT)
BEGIN
    UPDATE contactlists c
    SET c.contactsgroup = gid
    WHERE c.whoselistid = pid
    AND c.contactid = contactid;
END;$$

CREATE PROCEDURE createPlayer(loginname VARCHAR(30), password VARCHAR(40),email VARCHAR(320),ingamename VARCHAR(30),website VARCHAR(320),country VARCHAR(60))
BEGIN
    DECLARE newpid BIGINT;
    
    INSERT INTO players VALUES (0, loginname, ingamename, password, email, website, country, SYSDATE());

    SELECT p.pid
    INTO newpid
    FROM players p
    WHERE p.loginname = loginname;
    
    INSERT INTO grouplists VALUES (newpid, 0, 'Default Group'); 
END;$$

DELIMITER ;

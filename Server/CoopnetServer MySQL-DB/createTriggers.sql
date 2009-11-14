DELIMITER $$

USE coopnet;$$

-----------------------TRIGGERS-----------------------
-- CHECK CONSTRAINTS aren't supported yet, so using trigger for all checks
-- raising exceptions is not supported yet
-- putting both events into same trigger via ... BEFORE INSERT OR UPDATE ON ... isn't supported yet
-- creating multiple triggers with the same event isn't supported yet

DROP TRIGGER IF EXISTS players_insert;$$
DROP TRIGGER IF EXISTS players_update;$$
DROP TRIGGER IF EXISTS banlists_insert;$$
DROP TRIGGER IF EXISTS banlists_update;$$
DROP TRIGGER IF EXISTS mutelists_insert;$$
DROP TRIGGER IF EXISTS mutelists_update;$$
DROP TRIGGER IF EXISTS grouplists_insert;$$
DROP TRIGGER IF EXISTS grouplists_update;$$
DROP TRIGGER IF EXISTS grouplists_delete;$$
DROP TRIGGER IF EXISTS contactlists_insert;$$
DROP TRIGGER IF EXISTS contactlists_update;$$

-- for players
CREATE TRIGGER players_insert
BEFORE INSERT ON players
FOR EACH ROW BEGIN
    -- autoincrement pid
    DECLARE newid BIGINT;
    SELECT MAX(pid)+1
    INTO newid
    FROM players;
    IF (newid IS NULL) THEN
        SET newid := 0;
    END IF;
    SET NEW.pid := newid;
    
    -- check integrity
    IF (LENGTH(NEW.loginname) < 3) THEN
        CALL loginname_IS_SHORTER_THAN_3_CHARS();
    END IF;
    IF (LENGTH(NEW.ingamename) < 1) THEN
        CALL ingamename_IS_SHORTER_THAN_1_CHAR();
    END IF;
    IF (LENGTH(NEW.password) < 40) THEN
        CALL password_IS_NOT_40_CHARS();
    END IF;
    IF (DATEDIFF(SYSDATE(), NEW.lastlogin) < 0) THEN
        CALL lastlogin_IS_IN_FUTURE();
    END IF;
END;$$
CREATE TRIGGER players_update
BEFORE UPDATE ON players
FOR EACH ROW BEGIN
    -- check integrity
    IF (LENGTH(NEW.loginname) < 3) THEN
        CALL loginname_IS_SHORTER_THAN_3_CHARS();
    END IF;
    IF (LENGTH(NEW.ingamename) < 1) THEN
        CALL ingamename_IS_SHORTER_THAN_1_CHAR();
    END IF;
    IF (LENGTH(NEW.password) < 40) THEN
        CALL password_IS_NOT_40_CHARS();
    END IF;
    IF (DATEDIFF(SYSDATE(), NEW.lastlogin) < 0) THEN
        CALL lastlogin_IS_IN_FUTURE();
    END IF;
END;$$

-- for banlists
CREATE TRIGGER banlists_insert
BEFORE INSERT ON banlists
FOR EACH ROW BEGIN
    -- check integrity
    IF (NEW.whoselistid = NEW.bannedid) THEN
        CALL whoselistid_AND_bannedid_ARE_THE_SAME();
    END IF;
END;$$
CREATE TRIGGER banlists_update
BEFORE UPDATE ON banlists
FOR EACH ROW BEGIN
    -- check integrity
    IF (NEW.whoselistid = NEW.bannedid) THEN
        CALL whoselistid_AND_bannedid_ARE_THE_SAME();
    END IF;
END;$$

-- for mutelists
CREATE TRIGGER mutelists_insert
BEFORE INSERT ON mutelists
FOR EACH ROW BEGIN
    -- check integrity
    IF (NEW.whoselistid = NEW.mutedid) THEN
        CALL whoselistid_AND_mutedid_ARE_THE_SAME();
    END IF;
END;$$
CREATE TRIGGER mutelists_update
BEFORE UPDATE ON mutelists
FOR EACH ROW BEGIN
    -- check integrity
    IF (NEW.whoselistid = NEW.mutedid) THEN
        CALL whoselistid_AND_mutedid_ARE_THE_SAME();
    END IF;
END;$$

-- for grouplists
CREATE TRIGGER grouplists_insert
BEFORE INSERT ON grouplists
FOR EACH ROW BEGIN
    -- autoincrement gid
    DECLARE newid TINYINT;
    SELECT MAX(gid)+1
    INTO newid
    FROM grouplists
    WHERE whoselistid = NEW.whoselistid;
    IF (newid IS NULL) THEN
        SET newid := 0;
    END IF;
    SET NEW.gid := newid;
    
    -- check integrity
    IF (LENGTH(NEW.groupname) < 1) THEN
        CALL groupname_IS_SHORTER_THAN_1_CHAR();
    END IF;
END;$$
CREATE TRIGGER grouplists_update
BEFORE UPDATE ON grouplists
FOR EACH ROW BEGIN
    -- check integrity
    IF (LENGTH(NEW.groupname) < 1) THEN
        CALL groupname_IS_SHORTER_THAN_1_CHAR();
    END IF;
    IF (NEW.gid = 0) THEN
        CALL default_group_SHOULD_NOT_BE_RENAMED();
    END IF;
END;$$

-- for contactlists
CREATE TRIGGER contactlists_insert
BEFORE INSERT ON contactlists
FOR EACH ROW BEGIN
    -- ensure that group exists for the player 
    DECLARE groupCount TINYINT;
    SELECT COUNT(gid)
    INTO groupCount
    FROM grouplists
    WHERE whoselistid = NEW.whoselistid
    AND gid = NEW.contactsgroup;
    IF (groupCount = 0) THEN
        CALL contactsgroup_DOES_NOT_EXIST_FOR_whoselistid();
    END IF;
    IF (groupCount > 1) THEN
        CALL contactsgroup_EXISTS_MORE_THAN_ONCE_FOR_whoselistid();
    END IF;

    -- check integrity
    IF (LENGTH(NEW.contactstatus) < 1) THEN
        CALL contactstatus_IS_SHORTER_THAN_1_CHAR();
    END IF;
    IF (NEW.whoselistid = NEW.contactid) THEN
        CALL whoselistid_AND_contactid_ARE_THE_SAME();
    END IF;
END;$$
CREATE TRIGGER contactlists_update
BEFORE UPDATE ON contactlists
FOR EACH ROW BEGIN
    -- ensure that group exists for the player 
    DECLARE groupCount TINYINT;
    SELECT COUNT(gid)
    INTO groupCount
    FROM grouplists
    WHERE whoselistid = NEW.whoselistid
    AND gid = NEW.contactsgroup;
    IF (groupCount = 0) THEN
        CALL contactsgroup_DOES_NOT_EXIST_FOR_whoselistid();
    END IF;
    IF (groupCount > 1) THEN
        CALL contactsgroup_EXISTS_MORE_THAN_ONCE_FOR_whoselistid();
    END IF;

    -- check integrity
    IF (LENGTH(NEW.contactstatus) < 1) THEN
        CALL contactstatus_IS_SHORTER_THAN_1_CHAR();
    END IF;
    IF (NEW.whoselistid = NEW.contactid) THEN
        CALL whoselistid_AND_contactid_ARE_THE_SAME();
    END IF;
END;$$

DELIMITER ;

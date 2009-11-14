DELIMITER $$

USE coopnet;$$

-------------------------BEGIN-----------------------

START TRANSACTION;$$

-------------------------DELETE-----------------------

DROP TABLE IF EXISTS tmp_inactiveusers;$$

-- get inactive users (not logged in since 1 year)
CREATE TEMPORARY TABLE tmp_inactiveusers (
	SELECT pid
	FROM players
	WHERE DATEDIFF(SYSDATE(), lastlogin) > 365
);$$

-- delete users contactlist
DELETE FROM contactlists
WHERE whoselistid IN (
	SELECT pid
	FROM tmp_inactiveusers
);$$

-- delete this user from other contactlists
DELETE FROM contactlists
WHERE contactid IN (
	SELECT pid
	FROM tmp_inactiveusers
);$$

-- delete this users grouplists
DELETE FROM grouplists 
WHERE whoselistid IN (
	SELECT pid 
	FROM tmp_inactiveusers
);$$

-- delete this users mutelist
DELETE FROM mutelists
WHERE whoselistid IN (
	SELECT pid
	FROM tmp_inactiveusers
);$$

-- delete this user from others mutelists
DELETE FROM mutelists
WHERE mutedid IN (
	SELECT pid
	FROM tmp_inactiveusers
);$$

-- delete this users banlist
DELETE FROM banlists
WHERE whoselistid IN (
	SELECT pid
	FROM tmp_inactiveusers
);$$

-- delete this user from others banlists
DELETE FROM banlists
WHERE bannedid IN (
	SELECT pid
	FROM tmp_inactiveusers
);$$

-- delete this user
DELETE FROM players
WHERE pid IN (
	SELECT pid
	FROM tmp_inactiveusers
);$$

DROP TABLE tmp_inactiveusers;$$

-------------------------COMMIT-----------------------

COMMIT;$$

DELIMITER ;

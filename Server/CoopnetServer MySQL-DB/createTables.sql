DELIMITER $$

USE coopnet;$$

-----------------------TABLES-----------------------
-- after recreating the tables, the triggers have to be recreated aswell

DROP TABLE IF EXISTS contactlists;$$
DROP TABLE IF EXISTS grouplists;$$
DROP TABLE IF EXISTS mutelists;$$
DROP TABLE IF EXISTS banlists;$$
DROP TABLE IF EXISTS players;$

CREATE TABLE players (
	pid BIGINT,
	loginname VARCHAR(30) NOT NULL,
	ingamename VARCHAR(30) NOT NULL, -- FOT only supports 30
	password VARCHAR(40) NOT NULL, -- SHA1 length = 40
	email VARCHAR(320) NOT NULL, -- RFC suggests 320 as max	
	website VARCHAR(320) NOT NULL, -- shouldn't be longer than 320, though 3000 is max
	country VARCHAR(60) NOT NULL,
	lastlogin DATE NOT NULL, -- after 1 year, user should be deleted
	CONSTRAINT pk_players PRIMARY KEY (pid),
	CONSTRAINT unique_loginname UNIQUE (loginname)
) ENGINE = InnoDB DEFAULT CHARSET=utf8;$$

CREATE TABLE banlists (
	whoselistid BIGINT,
	bannedid BIGINT,
	CONSTRAINT pk_banlists PRIMARY KEY (whoselistid, bannedid),
	CONSTRAINT fk_banlists_players_1 FOREIGN KEY (whoselistid) REFERENCES players (pid),
	CONSTRAINT fk_banlists_players_2 FOREIGN KEY (bannedid) REFERENCES players (pid)
) ENGINE = InnoDB DEFAULT CHARSET=utf8;$$

CREATE TABLE mutelists (
	whoselistid BIGINT,
	mutedid BIGINT,
	CONSTRAINT pk_mutelists PRIMARY KEY (whoselistid, mutedid),
	CONSTRAINT fk_mutelists_players_1 FOREIGN KEY (whoselistid) REFERENCES players (pid),
	CONSTRAINT fk_mutelists_players_2 FOREIGN KEY (mutedid) REFERENCES players (pid)
) ENGINE = InnoDB DEFAULT CHARSET=utf8;$$

CREATE TABLE grouplists (
	whoselistid BIGINT,
	gid TINYINT, -- user shouldn't have too many groups
	groupname VARCHAR(30) NOT NULL,
	CONSTRAINT pk_contactgroups PRIMARY KEY (gid, whoselistid),
	CONSTRAINT fk_contactgroups_players FOREIGN KEY (whoselistid) REFERENCES players (pid),
	CONSTRAINT unique_whoselistid_groupname UNIQUE (whoselistid, groupname)
) ENGINE = InnoDB DEFAULT CHARSET=utf8;$$

CREATE TABLE contactlists (
	whoselistid BIGINT,
	contactid BIGINT,
	contactsgroup TINYINT NOT NULL,
	contactstatus ENUM('Requested', 'Accepted') NOT NULL,
	CONSTRAINT pk_contactlists PRIMARY KEY (whoselistid, contactid),
	CONSTRAINT fk_contactlists_players_1 FOREIGN KEY (whoselistid) REFERENCES players (pid),
	CONSTRAINT fk_contactlists_players_2 FOREIGN KEY (contactid) REFERENCES players (pid)
) ENGINE = InnoDB DEFAULT CHARSET=utf8;$$

DELIMITER ;
 

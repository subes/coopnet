DELIMITER $$

USE mysql;$$

-------------------------USER-----------------------

CREATE USER 'coopnet'@'localhost' IDENTIFIED BY 'c0OpnetS3Rv3RpW';$$
GRANT ALL ON coopnet.* TO 'coopnet'@'localhost';$$
-- need this for triggers on MySQL 5.0, 5.1 supports TRIGGER privilege tho
GRANT SUPER ON *.* TO 'coopnet'@'localhost';$$

-------------------------DATABASE-----------------------

CREATE DATABASE coopnet;$$

DELIMITER ;


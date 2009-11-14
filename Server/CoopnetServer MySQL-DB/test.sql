DELIMITER $$

USE coopnet;$$

-------------------------TEST-----------------------

START TRANSACTION;$$

INSERT INTO players VALUES (0, '05555', 'someName', '1234567890123456789012345678901234567890', '', false, '', '', '2008.08.08');$$
INSERT INTO players VALUES (1, '15555', 'sameName', '1234567890123456789012345678901234567890', '', false, '', '', '2008.08.08');$$
INSERT INTO players VALUES (2, '25555', 'sameName', '1234567890123456789012345678901234567890', '', false, '', '', '2008.08.08');$$
INSERT INTO players VALUES (3, '35555', 'sameName', '1234567890123456789012345678901234567890', '', false, '', '', '2008.08.08');$$

INSERT INTO banlists VALUES (0, 1);$$
INSERT INTO banlists VALUES (1, 0);$$
INSERT INTO banlists VALUES (3, 0);$$

INSERT INTO mutelists VALUES (0, 1);$$
INSERT INTO mutelists VALUES (1, 0);$$
INSERT INTO mutelists VALUES (2, 0);$$

INSERT INTO grouplists VALUES (0, 0, 'zeroGroup');$$
INSERT INTO grouplists VALUES (0, 1, 'oneGroup');$$
INSERT INTO grouplists VALUES (1, 0, 'zeroGroup');$$
INSERT INTO grouplists VALUES (1, 1, 'oneGroup');$$
INSERT INTO grouplists VALUES (2, 0, 'zeroGroup');$$
INSERT INTO grouplists VALUES (3, 0, 'emptyGroup');$$

INSERT INTO contactlists VALUES (0, 1, 0, 'Accepted');$$
INSERT INTO contactlists VALUES (0, 2, 1, 'Accepted');$$
INSERT INTO contactlists VALUES (0, 3, 0, 'Accepted');$$
INSERT INTO contactlists VALUES (1, 0, 0, 'Requested');$$
INSERT INTO contactlists VALUES (2, 0, 0, 'Requested');$$

--COMMIT;$$
ROLLBACK;$$ -- tables have to be in InnoDB for this to work

DELIMITER ;

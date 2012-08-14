-- TestManager - test tracking and management system.
-- Copyright (C) 2012  Istvan Pamer
--
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
--
-- You should have received a copy of the GNU General Public License
-- along with this program.  If not, see <http://www.gnu.org/licenses/>.

-- ======================================
--   TESTMANAGER CREATE DATABASE SCRIPT
-- ======================================

-- CREATE DATABSE
CREATE DATABASE  `testmanager_reporting` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

-- CREATE TABLES
CREATE TABLE  `testmanager_reporting`.`TEST_RUN_DATA` (
`Id` INT NOT NULL AUTO_INCREMENT,
`SetName` VARCHAR( 255 ) NOT NULL ,
`SetDate` DATETIME NOT NULL ,
`TestName` VARCHAR( 255 ) NOT NULL ,
`ParamName` VARCHAR( 255 ) NOT NULL ,
`TestRunMessageId` INT NULL ,
`StartDate` DATETIME NOT NULL ,
`StopDate` DATETIME NOT NULL ,
`ResultState` VARCHAR( 20 ) NOT NULL ,
PRIMARY KEY (  `Id` ) ,
INDEX (  `SetDate` ) ,
INDEX (  `TestRunMessageId` ) ,
INDEX (  `StartDate` ) ,
INDEX (  `StopDate` ) ,
INDEX (  `ResultState` )
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_general_ci;

ALTER TABLE  `testmanager_reporting`.`TEST_RUN_DATA` ADD FULLTEXT (
`SetName` ,
`TestName` ,
`ParamName`
);

CREATE TABLE  `testmanager_reporting`.`TEST_RUN_PARAMS` (
`Id` INT NOT NULL AUTO_INCREMENT,
`TestRunId` INT NOT NULL,
`ParamKey` TEXT NULL ,
`ParamValue` TEXT NULL ,
PRIMARY KEY (  `Id` ) ,
INDEX (  `TestRunId` )
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_general_ci;

ALTER TABLE  `testmanager_reporting`.`TEST_RUN_PARAMS` ADD FULLTEXT (
`ParamKey` ,
`ParamValue`
);

CREATE TABLE  `testmanager_reporting`.`TEST_RUN_ENVIRONMENT` (
`Id` INT NOT NULL AUTO_INCREMENT,
`TestRunId` INT NOT NULL,
`EnvKey` TEXT NOT NULL ,
`EnvValue` TEXT NOT NULL ,
PRIMARY KEY (  `Id` ) ,
INDEX (  `TestRunId` )
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_general_ci;

ALTER TABLE  `testmanager_reporting`.`TEST_RUN_ENVIRONMENT` ADD FULLTEXT (
`EnvKey` ,
`EnvValue`
);

CREATE TABLE  `testmanager_reporting`.`TEST_RUN_CHECKPOINTS` (
`Id` INT NOT NULL AUTO_INCREMENT,
`TestRunId` INT NOT NULL,
`Message` TEXT NOT NULL ,
`MainType` TEXT NOT NULL ,
`SubType` TEXT NOT NULL ,
`ResultState` VARCHAR( 20 ) NOT NULL ,
PRIMARY KEY (  `Id` ) ,
INDEX (  `TestRunId` ) ,
INDEX (  `ResultState` )
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_general_ci;

ALTER TABLE  `testmanager_reporting`.`TEST_RUN_CHECKPOINTS` ADD FULLTEXT (
`Message` ,
`MainType` ,
`SubType`
);

CREATE TABLE  `testmanager_reporting`.`TEST_RUN_MESSAGE` (
`MessageId` INT NOT NULL AUTO_INCREMENT,
`ErrorMessage` TEXT NULL ,
`Comment` TEXT NULL ,
`Type` TEXT NULL ,
PRIMARY KEY (  `MessageId` )
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_general_ci;

ALTER TABLE  `testmanager_reporting`.`TEST_RUN_MESSAGE` ADD FULLTEXT (
`ErrorMessage` ,
`Comment` ,
`Type`
);

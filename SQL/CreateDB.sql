-- Uncomment to reset DB:
DROP SCHEMA IF EXISTS tiw;


-- Create main schema for project
CREATE SCHEMA IF NOT EXISTS tiw;


-- Create tables
CREATE TABLE IF NOT EXISTS tiw.`user` (
	username	VARCHAR(64) 	PRIMARY KEY,
    `password`	VARCHAR(64) 	NOT NULL
);

CREATE TABLE IF NOT EXISTS tiw.category (
	code		VARCHAR(64)		PRIMARY KEY,
	name		VARCHAR(64)		NOT NULL
);

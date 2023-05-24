-- Deleting all content of all tables before repopulating them
TRUNCATE tiw.`user`;
TRUNCATE tiw.category;


-- Primary root user
INSERT INTO tiw.`user` (username, password) VALUES ("admin", "abc123");


-- Inserting some categories
INSERT INTO tiw.category (code, name) VALUES
	("1", "Materiali solidi"),
	("11", "Materiali inerti"),
	("111", "Inerti da edilizia"),
	("1111", "Amianto"),
	("11111", "Amianto in lastre"),
	("11112", "Amianto in frammenti"),
	("1112", "Materiali cementizi"),
	("112", "Inerti ceramici"),
	("1121", "Piastrelle"),
	("1122", "Sanitari"),
	("12", "Materiali ferrosi"),
	("13", "Materiali ferrosi"),
	("14", "Materiali ferrosi"),
	("15", "Materiali ferrosi"),
	("16", "Materiali ferrosi"),
	("17", "Materiali ferrosi"),
	("18", "Materiali ferrosi"),
	("19", "Materiali ferrosi");

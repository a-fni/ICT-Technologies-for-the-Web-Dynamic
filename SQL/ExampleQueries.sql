-- Find all descendants of a particular category INCLUDING and EXLUDING parent
SET @parentCode = "111";

SELECT *
FROM tiw.category c 
WHERE c.code LIKE CONCAT(@parentCode, "%")
ORDER BY c.code ASC;

SELECT *
FROM tiw.category c 
WHERE c.code LIKE CONCAT(@parentCode, "_%")
ORDER BY c.code ASC;


-- Find direct parent of a particular category
-- ===> simply remove last digit from `code` of category


-- Find how many direct children a category has (limit must always be 9)
SET @parentCode = "111";

SELECT COUNT(*)
FROM tiw.category c
WHERE c.code LIKE CONCAT(@parentCode, "_");


-- Given a parent, compose next direct child full code
SET @parentCode = "1";

SELECT CONCAT(@parentCode, COUNT(*)+1)
FROM tiw.category c
WHERE c.code LIKE CONCAT(@parentCode, "_");

-- NOTICE: Works also when DB is empty (returns "1") or when parent
-- has no children (returns "[parentCode]1")


-- Clone a subtree under another category
SET @targetSubtreeRoot = "1111";
SET @destinationParentCode = "1";

-- Following value is retrieved from the previous example query (simply copy-pasted here)...
SET @newSubtreeRoot = (
	SELECT CONCAT(@destinationParentCode, COUNT(*)+1)
	FROM tiw.category c
	WHERE c.code LIKE CONCAT(@destinationParentCode, "_")
);

INSERT INTO tiw.category (code, name)
SELECT
	CONCAT (
		@newSubtreeRoot,
		SUBSTRING(c.code, INSTR(c.code, @targetSubtreeRoot) + LENGTH(@targetSubtreeRoot))
	), name
FROM tiw.category c
WHERE c.code LIKE CONCAT(@targetSubtreeRoot, "%"); 

-- Simple query to view result:
SELECT * FROM tiw.category c WHERE c.code LIKE CONCAT(@newSubtreeRoot, "%");

-- Explenation of lines 54 to 5:
-- We concatenate the new subtree root with a substring of c.code starting
-- from the index of @targetSubtreeRoot, shifted by LENGTH(@targetSubtreeRoot)
-- 
-- For reference: INSTR: INdex of STRing

-- NOTICE: Works also when @destinationParentCode is empty (= copy subtree to root of hierchy)

-- Get full category tree and, for each node, if it's parentable
SELECT code, name, (
	SELECT COUNT(*)
	FROM tiw.category AS inner_category
    WHERE inner_category.code LIKE CONCAT(outer_category.code, "_")
) < 9 AS parentable
FROM tiw.category AS outer_category ORDER BY code

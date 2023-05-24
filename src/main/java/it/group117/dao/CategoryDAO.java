package it.group117.dao;


import it.group117.beans.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;


/**
 * Class in charge of creating Category beans
 */
public class CategoryDAO {

    // Connection to DB
    private final Connection connection;


    /**
     * Class constructor
     *
     * @param connection connection to database to be user
     */
    public CategoryDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Method in charge of fetching the full categorisation tree
     *
     * @return an ordered linked-list of all the categories in the tree
     * @throws SQLException if the query fails
     */
    public LinkedList<Category> getFullCategoryTree() throws SQLException {
        // Selecting all categories, ordered by their code
        String query = """
                SELECT code, name, (
                   SELECT COUNT(*)
                   FROM tiw.category AS inner_category
                   WHERE inner_category.code LIKE CONCAT(outer_category.code, "_")
                ) < 9 AS parentable
                FROM tiw.category AS outer_category ORDER BY code
                """;

        // Setting up query and running it
        PreparedStatement pStatement = connection.prepareStatement(query);
        ResultSet result = pStatement.executeQuery();

        // Checking that we didn't get an empty result
        LinkedList<Category> tree = new LinkedList<>();

        // Manually adding the root to the linked-list
        Category root = new Category();
        root.setCode("");
        root.setName("Root");
        root.setParentable(getNumberOfDirectChildren("") < 9);
        tree.add(root);

        if (result.isBeforeFirst()) {
            // Constructing our result linked-list
            result.next();
            while (!result.isAfterLast()) {
                Category node = new Category();

                node.setCode(result.getString("code"));
                node.setName(result.getString("name"));
                node.setParentable(result.getBoolean("parentable"));

                tree.add(node);
                result.next();
            }
        }

        // Returning final result as ordered linked-list
        return tree;
    }

    /**
     * Method in charge of returning all nodes of the category tree who are
     * available as potential parents for new categories
     *
     * @return LinkedList of nodes who are potential valid parents
     * @throws SQLException if the query fails
     */
    public LinkedList<Category> getParentAble() throws SQLException {
        // Selecting all categories, ordered by their code
        String query = """
                SELECT code FROM tiw.category c
                WHERE (
                    SELECT COUNT(*) FROM tiw.category cc
                    WHERE cc.code LIKE CONCAT(c.code, '_')
                ) < 9 ORDER BY code""";

        // Setting up query and running it
        PreparedStatement pStatement = connection.prepareStatement(query);
        ResultSet result = pStatement.executeQuery();

        // Checking that we didn't get an empty result
        LinkedList<Category> parentAble = new LinkedList<>();
        if (result.isBeforeFirst()) {
            // Constructing our result linked-list
            result.next();
            while (!result.isAfterLast()) {
                Category node = new Category();

                node.setCode(result.getString("code"));

                parentAble.add(node);
                result.next();
            }
        }

        // Returning final result as ordered linked-list
        return parentAble;
    }

    /**
     * Method in charge of adding a new query to the category tree
     *
     * @param parentCode code of the parent node selected
     * @param name name of the new category to be added
     * @return code of the added category, or null otherwise
     * @throws SQLException if the insertion fails
     */
    public String createNewCategory(String parentCode, String name) throws SQLException {
        // Checking selected parent exists
        if (!doesCategoryExist(parentCode))
            return null;

        // Checking selected parent has space for new category
        int lastChildIndex = getNumberOfDirectChildren(parentCode);
        if (lastChildIndex >= 9 || lastChildIndex == -1)
            return null;

        // If there is space, we build the code for the new category
        String fullCode = parentCode + (lastChildIndex + 1);

        // At this point, we simply add the new category
        String insertion = "INSERT INTO tiw.category (code, NAME) VALUES (?, ?)";

        // Setting up statement and executing it
        PreparedStatement pStatement = connection.prepareStatement(insertion);
        pStatement.setString(1, fullCode);
        pStatement.setString(2, name);

        // We simply return the full-code of the new category
        pStatement.execute();
        return fullCode;
    }

    /**
     * Method in charge of copying a full sub-tree to another location of the categorisation tree
     *
     * @param targetSubtreeRoot root of the subtree to be copied
     * @param destinationParentRoot destination node, under which we will copy
     * @return boolean representing whether the operation is successful of not
     * @throws SQLException if the insertion fails
     */
    public boolean copySubTree(String targetSubtreeRoot, String destinationParentRoot) throws SQLException {
        // Checking selected nodes exists
        if (!doesCategoryExist(targetSubtreeRoot) || !doesCategoryExist(destinationParentRoot))
            return false;

        // Checking that the destination node isn't inside the target sub-tree
        if (destinationParentRoot.startsWith(targetSubtreeRoot))
            return false;

        // Checking selected node has space for new child
        int lastChildIndex = getNumberOfDirectChildren(destinationParentRoot);
        if (lastChildIndex >= 9 || lastChildIndex == -1)
            return false;

        // If there is space, we build the code for the new category
        String fullCode = destinationParentRoot + (lastChildIndex + 1);

        // We can actually perform the cloning if we reached this point
        String cloning = """
                INSERT INTO tiw.category (code, name)
                SELECT CONCAT (?, SUBSTRING(c.code, INSTR(c.code, ?) + LENGTH(?))), name
                FROM tiw.category c
                WHERE c.code LIKE CONCAT(?, '%')""";

        // Setting up statement and executing it
        PreparedStatement pStatement = connection.prepareStatement(cloning);
        pStatement.setString(1, fullCode);
        pStatement.setString(2, targetSubtreeRoot);
        pStatement.setString(3, targetSubtreeRoot);
        pStatement.setString(4, targetSubtreeRoot);

        // We simply return the outcome of the statement (returns true only in case of insertion)
        return pStatement.execute();
    }

    /**
     * Helper method in charge of counting number of direct children of a node in the categorisation tree
     *
     * @param code code of the node - empty string it tree root
     * @return number of direct children of a node
     * @throws SQLException if the query fails
     */
    public int getNumberOfDirectChildren(String code) throws SQLException {
        // Query string
        String childrenCount = "SELECT COUNT(*) FROM tiw.category c WHERE c.code LIKE CONCAT(?, '_')";

        // Preparing the query and running it
        PreparedStatement pStatement = connection.prepareStatement(childrenCount);
        pStatement.setString(1, code);
        ResultSet result = pStatement.executeQuery();

        // Checking if, for some reason, we obtained no results...
        if (!result.isBeforeFirst())
            return -1;
        result.next();

        // Returning obtained results
        return result.getInt(1);
    }

    /**
     * Method in charge of checking whether a given category code exists in the category hierarchy
     *
     * @param code code to look for
     * @return whether the code exists
     * @throws SQLException if the query fails
     */
    public boolean doesCategoryExist(String code) throws SQLException {
        // If we receive an empty code, we interpret it as the root node...
        // which always exists
        if (code == null || code.isEmpty())
            return true;

        // Query string
        String query = "SELECT code FROM tiw.category c WHERE c.code = ?";

        // Preparing query and running it
        PreparedStatement pStatement = connection.prepareStatement(query);
        pStatement.setString(1, code);
        ResultSet result = pStatement.executeQuery();

        // Returning whether we obtained an empty result or not
        return result.isBeforeFirst();
    }

}

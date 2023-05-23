package it.group117.dao;


import it.group117.beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Class in charge of creating User beans
 */
public class UserDAO {

    // Connection to DB
    private final Connection connection;


    /**
     * Class construction
     * @param connection connection to database to be user
     */
    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Method in charge of checking if credentials match any user in the DB
     *
     * @param username username to check
     * @param password password which has to associated to the username
     * @return a User bean corresponding to the found user, or null otherwise
     * @throws SQLException if query fails for some reason
     */
    public User checkCredentials(String username, String password) throws SQLException {
        // Selecting users with the provided credentials
        String query = "SELECT username FROM tiw.user WHERE username = ? AND password = ?";

        // Setting up query and running it
        PreparedStatement pStatement = connection.prepareStatement(query);
        pStatement.setString(1, username);
        pStatement.setString(2, password);
        ResultSet result = pStatement.executeQuery();

        // Checking if we obtained an empty result (= no user with given credentials)
        if (!result.isBeforeFirst())
            return null;

        // Otherwise, we return the found user
        result.next();

        User user = new User();
        user.setUsername(result.getString("username"));

        return user;
    }

}

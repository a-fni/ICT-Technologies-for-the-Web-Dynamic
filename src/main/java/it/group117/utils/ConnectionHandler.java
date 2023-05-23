package it.group117.utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;


/**
 * Class with static methods for DB connection
 */
public class ConnectionHandler {

	/**
	 * Class constructor
	 *
	 * @param context context of the web application
	 * @return a connection to the database instance
	 * @throws UnavailableException whenever connection is (for some reason) unavailable
	 */
	public static Connection getConnection(ServletContext context) throws UnavailableException {
		// Creating placeholder connection object
		Connection connection;

		// Fetching connection data from context and trying to establish connection to DB
		try {
			String driver 	= context.getInitParameter("dbDriver");
			String url 		= context.getInitParameter("dbUrl");
			String user 	= context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");

			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("ERROR: Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("ERROR: Couldn't establish db connection");
		}

		return connection;
	}

	/**
	 * Closing a database connection
	 *
	 * @param connection connection to be closed
	 * @throws SQLException if something goes wrong during closing
	 */
	public static void closeConnection(Connection connection) throws SQLException {
		if (connection != null)
			connection.close();
	}
	
}

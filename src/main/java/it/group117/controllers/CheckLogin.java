package it.group117.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.group117.beans.User;
import it.group117.dao.UserDAO;
import it.group117.utils.ConnectionHandler;

import java.io.IOException;
import java.io.Serial;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.group117.utils.JsonResponse;
import it.group117.utils.TemplateHandler;
import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

/**
 * Servlet implementation class Login
 */
@WebServlet(name = "CheckLogin", value = "/login")
public class CheckLogin extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    // Connection to DB attribute and templating engine
    private Connection connection;


    /** @see HttpServlet#HttpServlet() */
    public CheckLogin() {
        super();
    }


    /** @see HttpServlet#init() */
    @Override
    public void init() throws ServletException {
        // Creating a DB connection and templating engine
        this.connection     = ConnectionHandler.getConnection(getServletContext());
    }

    /** @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response) */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Marshalling toward post request
        doPost(request, response);
    }

    /** @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response) */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Fetching form parameters
        String username = StringEscapeUtils.escapeJava(request.getParameter("username"));
        String password = StringEscapeUtils.escapeJava(request.getParameter("password"));

        // Instantiating a JsonObject for responses
        JsonObject jsonResponse = new JsonObject();

        // Checking if parameters have actually arrived
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Username or password missing!");
            jsonResponse.addProperty("username", "");
            JsonResponse.sendJsonResponse(response, jsonResponse);
            return;
        }

        // Accessing user's database
        User user;
        try {
            UserDAO userDao = new UserDAO(this.connection);
            user = userDao.checkCredentials(username, password);
        } catch (SQLException ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while checking credentials.");
            return;
        }

        // Checking a the user has been found or not
        if (user == null) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Wrong username or password");
            jsonResponse.addProperty("username", "");
            JsonResponse.sendJsonResponse(response, jsonResponse);
            return;
        }

        // If we reached this point, creating a session and redirecting user to home page
        request.getSession().setAttribute("user", user);
        jsonResponse.addProperty("success", true);
        jsonResponse.addProperty("message", "");
        jsonResponse.addProperty("username", user.getUsername());
        JsonResponse.sendJsonResponse(response, jsonResponse);
    }

    /** @see HttpServlet#destroy() */
    @Override
    public void destroy() {
        // Trying to close connection
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

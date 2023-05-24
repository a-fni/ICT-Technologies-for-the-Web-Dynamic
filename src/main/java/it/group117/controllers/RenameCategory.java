package it.group117.controllers;


import com.google.gson.JsonObject;
import it.group117.dao.CategoryDAO;
import it.group117.utils.ConnectionHandler;
import it.group117.utils.JsonResponse;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * Servlet implementation class RenameCategory
 */
@WebServlet(name = "RenameCategory", value = "/renameCategory")
public class RenameCategory  extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    // Connection to DB attribute and templating engine
    private Connection connection;


    /** @see HttpServlet#HttpServlet() */
    public RenameCategory() {
        super();
    }


    /** @see HttpServlet#init() */
    @Override
    public void init() throws ServletException {
        // Creating a DB connection
        this.connection = ConnectionHandler.getConnection(getServletContext());
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
        String code = StringEscapeUtils.escapeJava(request.getParameter("code"));
        String newName = StringEscapeUtils.escapeJava(request.getParameter("newName"));

        // Instantiating a JsonObject for responses
        JsonObject jsonResponse = new JsonObject();

        // Checking if parameters have actually arrived correctly
        if (code == null || newName == null || code.isEmpty() || newName.isEmpty()) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Parameter missing");
            JsonResponse.sendJsonResponse(response, jsonResponse);
            return;
        }

        // Cleaning up parameter strings
        code = code.trim();
        newName = newName.trim();

        boolean success;
        try {
            CategoryDAO categoryDAO = new CategoryDAO(this.connection);
            success = categoryDAO.renameCategory(code, newName);
        } catch (SQLException ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while renaming category");
            return;
        }

        // Finally, wee send the outcome of the category creation
        jsonResponse.addProperty("success", success);
        jsonResponse.addProperty("message", success ? "" : "Chosen node doesn't exist");
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

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
 * Servlet implementation class CreateCategory
 */
@WebServlet(name = "CreateCategory", value = "/createCategory")
public class CreateCategory extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    // Connection to DB attribute and templating engine
    private Connection connection;


    /** @see HttpServlet#HttpServlet() */
    public CreateCategory() {
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
        String name = StringEscapeUtils.escapeJava(request.getParameter("name"));
        String parent = StringEscapeUtils.escapeJava(request.getParameter("parent"));

        // Instantiating a JsonObject for responses
        JsonObject jsonResponse = new JsonObject();

        // Checking if parameters have actually arrived correctly
        if (name == null || parent == null || name.isEmpty() || parent.isEmpty()) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Parameter missing");
            JsonResponse.sendJsonResponse(response, jsonResponse);
            return;
        }

        // Cleaning up parameter strings
        name = name.trim();
        parent = parent.trim();

        boolean success = false;
        String message = "";
        try {
            // Checking if root has been selected as a parent
            parent = parent.equals("/") ? "" : parent;
            CategoryDAO categoryDAO = new CategoryDAO(this.connection);
            String code = categoryDAO.createNewCategory(parent, name);
            if (code != null)
                success = categoryDAO.doesCategoryExist(code);
            if (!success)
                message = "Selected not is not parent-able!";
        } catch (SQLException ex) {
            message = "An internal server error occurred while creating new category. Retry later...";
        }

        // Finally, wee send the outcome of the category creation
        jsonResponse.addProperty("success", success);
        jsonResponse.addProperty("message", message);
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

package it.group117.controllers;


import java.io.IOException;
import java.io.Serial;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import it.group117.dao.CategoryDAO;
import it.group117.utils.ConnectionHandler;
import it.group117.utils.JsonResponse;
import org.apache.commons.text.StringEscapeUtils;


/**
 * Servlet implementation class DoCopy
 */
@WebServlet(name = "DoCopy", value = "/doCopy")
public class DoCopy extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    // Connection to DB attribute and templating engine
    private Connection connection;


    /** @see HttpServlet#HttpServlet() */
    public DoCopy() {
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
        // Marshalling toward get request
        doPost(request, response);
    }

    /** @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response) */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Fetching parameters
        String src = StringEscapeUtils.escapeJava(req.getParameter("src"));
        String dest = StringEscapeUtils.escapeJava(req.getParameter("dest"));

        // Instantiating a JsonObject for responses
        JsonObject jsonResponse = new JsonObject();

        // Checking if parameters have actually arrived correctly
        if (src == null || dest == null || src.isEmpty() || dest.isEmpty()) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Parameter missing");
            JsonResponse.sendJsonResponse(resp, jsonResponse);
            return;
        }

        if (src.equals("/")) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Can't clone root of tree");
            JsonResponse.sendJsonResponse(resp, jsonResponse);
            return;
        }

        boolean success = false;
        try {
            // Checking if we are copying under root
            dest = dest.equals("/") ? "" : dest;

            CategoryDAO categoryDAO = new CategoryDAO(this.connection);
            String fullCode = categoryDAO.copySubTree(src, dest);
            if (fullCode != null)
                success = categoryDAO.doesCategoryExist(fullCode);
        } catch (SQLException ex) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "An internal server error occurred while trying to copy sub-tree. Retry later...");
            JsonResponse.sendJsonResponse(resp, jsonResponse);
            return;
        }

        // Finally, wee send the outcome of the category duplication
        jsonResponse.addProperty("success", success);
        jsonResponse.addProperty("message", success ? "" : "Chosen node is not parent-able");
        JsonResponse.sendJsonResponse(resp, jsonResponse);
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

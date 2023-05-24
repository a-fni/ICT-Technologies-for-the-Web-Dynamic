package it.group117.controllers;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.group117.beans.Category;
import it.group117.beans.User;
import it.group117.dao.CategoryDAO;
import it.group117.utils.ConnectionHandler;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.io.Serial;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Servlet implementation class Home
 */
@WebServlet(name = "GetCategories", value = "/categories")
public class GetCategories extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    // Connection to DB attribute and templating engine
    private Connection connection;

    /** @see HttpServlet#HttpServlet() */
    public GetCategories() {
        super();
    }

    /** @see HttpServlet#init() */
    @Override
    public void init() throws ServletException {
        // Creating a DB connection and templating engine
        this.connection = ConnectionHandler.getConnection(getServletContext());
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Fetch categories for tree
        LinkedList<Category> tree, parentAble;
        boolean rootParentAble;
        try {
            CategoryDAO categoryDAO = new CategoryDAO(this.connection);
            tree = categoryDAO.getFullCategoryTree();
            parentAble = categoryDAO.getParentAble();
            rootParentAble = categoryDAO.getNumberOfDirectChildren("") < 9;
        } catch (SQLException ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error while fetching category tree: " + ex);
            return;
        }

        // Getting context from request and response
        // final WebContext ctx = new WebContext(request, response, getServletContext(),
        // request.getLocale());

        // // Setting template variables
        // ctx.setVariable("user", user);
        // ctx.setVariable("tree", tree);
        // ctx.setVariable("parentAble", parentAble);
        // ctx.setVariable("rootParentAble", rootParentAble);
        // ctx.setVariable("copySrc", src);
        // templateEngine.process("/WEB-INF/home.html", ctx, response.getWriter());
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Marshalling toward get request
        doGet(request, response);
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

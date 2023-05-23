package it.group117.controllers;


import java.io.IOException;
import java.io.Serial;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.group117.beans.Category;
import it.group117.beans.User;
import it.group117.dao.CategoryDAO;
import it.group117.utils.ConnectionHandler;
import it.group117.utils.TemplateHandler;


/**
 * Servlet implementation class CopySubtree
 */
@Deprecated
@WebServlet(name = "CopySubtree", value = "/copySubtree")
public class CopySubtree extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    // Connection to DB attribute and templating engine
    private Connection connection;
    private TemplateEngine templateEngine;


    /** @see HttpServlet#HttpServlet() */
    public CopySubtree() {
        super();
    }


    /** @see HttpServlet#init() */
    @Override
    public void init() throws ServletException {
        // Creating a DB connection and templating engine
        this.connection = ConnectionHandler.getConnection(getServletContext());
        this.templateEngine = TemplateHandler.getTemplateEngine(getServletContext());
    }

    /** @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse) */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Check if user is logged in
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(getServletContext().getContextPath());
            return;
        }

        // Fetch categories for tree
        LinkedList<Category> tree;
        try {
            CategoryDAO categoryDAO = new CategoryDAO(this.connection);
            tree = categoryDAO.getFullCategoryTree();
        } catch (SQLException ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error while fetching category tree: " + ex);
            return;
        }

        // get "target" parameter
        String src = null;
        try {
            src = StringEscapeUtils.escapeJava(request.getParameter("src"));
        } catch (NullPointerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing src parameter");
            return;
        }

        // Getting context from request and response
        final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());

        // Setting template variables
        ctx.setVariable("user", user);
        ctx.setVariable("tree", tree);
        ctx.setVariable("src", src);
        templateEngine.process("/WEB-INF/copySubtree.html", ctx, response.getWriter());
    }

    /** @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse) */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
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

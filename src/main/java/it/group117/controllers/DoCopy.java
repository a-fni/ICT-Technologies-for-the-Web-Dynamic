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

import it.group117.beans.User;
import it.group117.dao.CategoryDAO;
import it.group117.utils.ConnectionHandler;
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
        // Check if user is logged in
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect(getServletContext().getContextPath());
            return;
        }

        // Fetching form parameters
        String src = StringEscapeUtils.escapeJava(req.getParameter("src"));
        String dest = StringEscapeUtils.escapeJava(req.getParameter("dest"));

        // Checking if parameters have actually arrived correctly
        if (src != null && dest != null && !src.isEmpty() && !dest.isEmpty()) {
            try {
                // Checking if we are copying under root
                dest = dest.equals("/") ? "" : dest;

                CategoryDAO categoryDAO = new CategoryDAO(this.connection);
                categoryDAO.copySubTree(src, dest);
            } catch (SQLException ex) {
                resp.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Error while copying tree: " + ex
                );
                return;
            }
        }

        resp.sendRedirect(getServletContext().getContextPath() + "/home");
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

package it.group117.controllers;

import java.io.IOException;
import java.io.Serial;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Servlet implementation class Logout
 */
@WebServlet(name = "Logout", value = "/logout")
public class Logout extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;


    /** @see HttpServlet#HttpServlet() */
    public Logout() {
        super();
    }


    /** @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse) */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // If a valid session is active, invalidate it
        HttpSession session = request.getSession(false);
        if (session != null)
            session.invalidate();

        // send the user back to root
        response.sendRedirect(getServletContext().getContextPath());
    }

    /** @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response) */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Marshalling toward get request
        doGet(request, response);
    }

}

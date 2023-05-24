package it.group117.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Servlet Filter implementation class LoginChecker
 */
public class LoginChecker implements Filter {

    /** Class constructor */
    public LoginChecker() {
        super();
    }


    /** @see Filter#destroy() */
    public void destroy() {}

    /** @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain) */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.print("Login checker filter executing... ");

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession();
        if (session.isNew() || session.getAttribute("user") == null) {
            res.setStatus(403);
            res.setHeader("Location", req.getServletContext().getContextPath() + "/index.html");
            System.out.println("login checker FAILED!");
            return;
        }

        // If login check is successful, we handle the request
        chain.doFilter(request, response);
    }

    /** @see Filter#init(FilterConfig) */
    public void init(FilterConfig fConfig) {}

}

package it.group117.filters;


import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Servlet Filter implementation class NoCacher
 */
public class NoCacher implements Filter {

    /** Class constructor */
    public NoCacher() {
        super();
    }


    /** @see Filter#destroy() */
    public void destroy() {}

    /** @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain) */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Logging
        System.out.println("=== No cache-r running ===");

        // Purpose of this filter is to prevent clients from caching certain static files
        HttpServletResponse res = (HttpServletResponse) response;
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");  // HTTP 1.1.
        res.setHeader("Pragma", "no-cache");                                    // HTTP 1.0.
        res.setHeader("Expires", "0");                                          // Proxies.

        // Pass the request along the filter chain
        chain.doFilter(request, response);
    }

    /** @see Filter#init(FilterConfig) */
    public void init(FilterConfig fConfig) {}

}

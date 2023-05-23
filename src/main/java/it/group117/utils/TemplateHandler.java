package it.group117.utils;


import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;


/**
 * Class in charge of setting up and configuring the thymeleaf template engine
 */
public class TemplateHandler {

    /**
     * Function in charge of actually creating a thymeleaf template engine
     *
     * @param ctx servlet context
     * @return a TemplateEngine instance
     */
    public static TemplateEngine getTemplateEngine(ServletContext ctx) {
        // Creating a thymeleaf template resolver and configuring it
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(ctx);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setSuffix(".html");

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine;
    }

}

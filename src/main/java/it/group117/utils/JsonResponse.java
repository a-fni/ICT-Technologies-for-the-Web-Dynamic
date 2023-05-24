package it.group117.utils;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Utilities class for JSON responses
 */
public class JsonResponse {

    /**
     * Static method in charge of preparing and sending a JSON response to a client
     *
     * @param res HttpServletResponse object
     * @param jsonObject JSON object will should be serialized
     * @throws IOException if the response fails
     */
    public static void sendJsonResponse(HttpServletResponse res, JsonObject jsonObject) throws IOException {
        // Redirect to the Home page and add missions to the parameters
        String json = new Gson().toJson(jsonObject);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(json);
    }

    /**
     * Static method in charge of preparing and sending a JSON response to a client
     *
     * @param res HttpServletResponse object
     * @param jsonString already JSON serialized object
     * @throws IOException if the response fails
     */
    public static void sendJsonResponse(HttpServletResponse res, String jsonString) throws IOException {
        // Redirect to the Home page and add missions to the parameters
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(jsonString);
    }

}

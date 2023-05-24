package it.group117.utils;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 *
 */
public class JsonResponse {

    /**
     *
     * @param res
     * @param jsonObject
     * @throws IOException
     */
    public static void sendJsonResponse(HttpServletResponse res, JsonObject jsonObject) throws IOException {
        // Redirect to the Home page and add missions to the parameters
        String json = new Gson().toJson(jsonObject);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(json);
    }

}

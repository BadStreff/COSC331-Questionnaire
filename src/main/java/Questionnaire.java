/**
 * Created by Adam Furbee on 9/19/2015.
 * This will be the main request handler for the project.
 */

import static spark.Spark.*;

/**
 *  Velocity imports
 */
import java.util.HashMap;
import java.util.Map;

import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;

public class Questionnaire {
    public static void main(String[] args) {
        port(80);
        int maxThreads = 8;
        threadPool(maxThreads);

        staticFileLocation("/public"); // Static files

        get("/", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("message", "This is where the survey index page will be generated");

            // The wm files are located under the resources directory
            return new spark.ModelAndView(model, "/public/index.vm");
        }, new VelocityTemplateEngine());
    }
}

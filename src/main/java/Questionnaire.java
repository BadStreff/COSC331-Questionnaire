/*
 * This file is part of COSC3318-Questionnaire.
 *
 *     COSC3318-Questionnaire is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     COSC3318-Questionnaire is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

import static spark.Spark.*;

// Used for velocity templates.
import java.util.HashMap;
import java.util.Map;
import spark.template.velocity.VelocityTemplateEngine;


import db.Database;
import db.Survey;
import db.Question;

public class Questionnaire {
    public static void main(String[] args) {
        Database db = new Database();

        // Initialize Web Server
        port(4567);
        int maxThreads = 8;
        threadPool(maxThreads);

        //Initialize static file directory.
        staticFileLocation("/public");

        get("/", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("message", "Please take the time to complete any surveys you have in your queue.");

            System.out.println("Serving / to " + request.ip());

            // The wm files are located under the resources directory
            return new spark.ModelAndView(model, "/private/index.html");
        }, new VelocityTemplateEngine());

        get("/login", (request, response) -> {
            Map<String, Object> model = new HashMap<>();

            System.out.println("Serving /login to " + request.ip());

            // The wm files are located under the resources directory
            return new spark.ModelAndView(model, "/private/login.html");
        }, new VelocityTemplateEngine());

        get("/sign_up", (request, response) -> {
            Map<String, Object> model = new HashMap<>();

            System.out.println("Serving /sign_up to " + request.ip());

            // The wm files are located under the resources directory
            return new spark.ModelAndView(model, "/private/signup.html");
        }, new VelocityTemplateEngine());

        get("/survey/:id", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("message", "You are taking survey ");
            model.put("id", request.params(":id"));

            System.out.println("Serving /survey/" + request.params(":id") + " to " + request.ip());

            // The wm files are located under the resources directory
            return new spark.ModelAndView(model, "/private/survey.html");
        }, new VelocityTemplateEngine());
    }
}

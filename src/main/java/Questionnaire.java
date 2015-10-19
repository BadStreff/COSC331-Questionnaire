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
import db.User;

public class Questionnaire {
    public static void main(String[] args) {
        Database db = new Database();

        // Initialize Web Server
        port(4567);
        int maxThreads = 8;
        threadPool(maxThreads);

        //Initialize static file directory.
        staticFileLocation("/public");

        //Before filter to handle authentications.
        before((request, response) -> {
            String path = request.pathInfo();
            System.out.println("Serving " + path + " to " + request.ip());

            //if(request.cookie("uid") != null)
            //    System.out.println("Got uid cookie: " + request.cookie("uid"));

            //Redirect users that are not logged in to the login page
            if(request.cookie("uid") == null && !(path.contains("bootstrap/") ||
                    path.contains("/login")  ||
                    path.contains("/sign_up")  ||
                    path.contains("js/")     ||
                    path.contains("css/"))) {
                response.redirect("/login");
            }
        });

        get("/", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("message", "Please take the time to complete any surveys you have in your queue.");

            // The wm files are located under the resources directory
            return new spark.ModelAndView(model, "/private/index.html");
        }, new VelocityTemplateEngine());


        get("/login", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("message", "Please login before accessing the site.");

            // The wm files are located under the resources directory
            return new spark.ModelAndView(model, "/private/login.html");
        }, new VelocityTemplateEngine());
        post("/login", (request, response) -> {
            System.out.println("Username posted: " + request.queryParams("username"));
            System.out.println("Password posted: " + request.queryParams("password"));

            //response.cookie("uid", "uid", 3600); //set a cookie for authenticated user

            //try createusersession
            //  issue a cookie with uid
            //catch
            //  redirect to login page with err message

            return 0;
        });


        get("/sign_up", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            // The wm files are located under the resources directory
            return new spark.ModelAndView(model, "/private/signup.html");
        }, new VelocityTemplateEngine());
        post("/sign_up", (request, response) -> {
            System.out.println("Username posted: " + request.queryParams("username"));
            System.out.println("Email posted: " + request.queryParams("email"));
            System.out.println("Password posted: " + request.queryParams("password"));
            User u = new User(request.queryParams("username"),
                    request.queryParams("email"),
                    request.queryParams("password"),
                    User.Type.REGULAR);
            db.insertUser(u);
            return 0;
        });
    }
}

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

            //Redirect users that are not logged in to the login page
            if(request.session().attribute("uid") == null && //request.cookie("uid") == null &&
                    !(path.contains("bootstrap/") ||
                    path.contains("/login")  ||
                    path.contains("/sign_up")  ||
                    path.contains("js/")     ||
                    path.contains("css/") ||
                    path.contains("/userexist"))) {
                response.redirect("/login");
            }
            if(request.session().attribute("uid") != null && //request.cookie("uid") != null &&
                    (path.contains("/login") ||
                     path.contains("/sign_up"))) {
                response.redirect("/");
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
            String username = request.queryParams("username");
            String password = request.queryParams("password");
            try {
                int uid = db.createUserSession(username, password);
                request.session(true);
                request.session().attribute("uid", String.valueOf(uid));
            }
            catch(Database.BadCredentialsException e) {
                return "failure";
            }
            return "success";
        });


        get("/sign_up", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            // The wm files are located under the resources directory
            return new spark.ModelAndView(model, "/private/signup.html");
        }, new VelocityTemplateEngine());
        post("/sign_up", (request, response) -> {
            String username = request.queryParams("username");
            String email = request.queryParams("email");
            String password = request.queryParams("password");

            System.out.println("Username posted: " + username);
            System.out.println("Email posted: " + email);
            System.out.println("Password posted: " + password);

            User u = new User(username,
                    email,
                    password,
                    User.Type.REGULAR);
            try {
                db.insertUser(u);
                request.session(true);
                int uid = db.createUserSession(username, password);
                request.session().attribute("uid", String.valueOf(uid));
            }
            catch(db.Database.UserAlreadyExistException e) {
                return "failure";
            }
            return "success";
        });

        post("/userexist", (request, response) -> {
            String username = request.queryParams("username");
            System.out.println("Checking username: " + username);
            return db.userExist(username);
        });
    }
}

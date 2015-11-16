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
import java.util.List;
import java.util.ArrayList;
import spark.template.velocity.VelocityTemplateEngine;


import db.Database;
import db.Survey;
import db.Question;
import db.User;

public class Questionnaire {
    private static List<String> adminSession = new ArrayList<String>();
    public static void main(String[] args) {
        Database db = new Database("Questionnaire.db");

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
            if(request.session().attribute("username") == null && !publicPath(path)) {
                response.redirect("/login");
            }
            //Redirect users logged in to the home page, if trying to access non-auth pages
            if(request.session().attribute("username") != null && forbiddenPath(path)) {
                response.redirect("/");
            }
            //Halt if user attempts to login to an admin page
            if(adminPath(path)) {
                if(!adminSession.contains(request.session().id()))
                    halt(403, "<h1>403 Forbidden<h6>");
            }

        });

        get("/", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("message", "Please take the time to complete any surveys you have in your queue.");
            model.put("question", db.getRandomQuestion(request.session().attribute("username")));
            return new spark.ModelAndView(model, "/private/index.html");
        }, new VelocityTemplateEngine());
        post("/submit_answer", (request, response) -> {
            return db.submitAnswer(Integer.parseInt(request.queryParams("choice_id")), request.session().attribute("username"));
        });

        get("/admin", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            return new spark.ModelAndView(model, "/private/admin/index.html");
        }, new VelocityTemplateEngine());
        get("/admin/users", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("users", db.getUsers());
            return new spark.ModelAndView(model, "/private/admin/users.html");
        }, new VelocityTemplateEngine());
        get("/admin/questions", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            return new spark.ModelAndView(model, "/private/admin/questions.html");
        }, new VelocityTemplateEngine());
        post("/admin/delete_user", (request, response) -> {
            return db.deleteUser(request.queryParams("username"));
        });
        post("/change_password", (request, response) -> {
            //A user can only change their own password, unless they have an admin session
            if(request.queryParams("username") == request.session().attribute("username") ||
                    adminSession.contains(request.session().id()))
                return db.changePassword(request.queryParams("username"), request.queryParams("password"));
            return false;
        });

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
            if (db.verifyUserCredentials(username, password)) {
                request.session(true);
                request.session().attribute("username", username);
                if(db.isAdmin(username))
                    adminSession.add(request.session().id());
                return true;
            }
            else {
                return false;
            }
        });

        get("/sign_up", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
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
                request.session().attribute("username", username);
            }
            catch(db.Database.UserAlreadyExistException e) {
                return false;
            }
            return true;
        });
        post("/userexist", (request, response) -> {
            String username = request.queryParams("username");
            System.out.println("Checking username: " + username);
            return db.userExist(username);
        });
    }

    public static boolean publicPath(String pathInfo) {
        String[] paths = {"/css","/bootstrap", "/js", "/images",
                "/vendors", "/login", "/sign_up", "/userexist"};
        boolean r = false;
        for(String i : paths)
            if(pathInfo.contains(i))
                r = true;
        return r;
    }
    public static boolean forbiddenPath(String pathInfo) {
        String[] paths = {"/login", "/sign_up", "/userexist"};
        boolean r = false;
        for(String i : paths)
            if(pathInfo.contains(i))
                r = true;
        return r;
    }
    public static boolean adminPath(String pathInfo) {
        String[] paths = {"/admin"};
        boolean r = false;
        for(String i : paths)
            if(pathInfo.contains(i))
                r = true;
        return r;
    }
}

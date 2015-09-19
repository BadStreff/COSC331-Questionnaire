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

/**
 * Created by Adam Furbee on 9/19/2015.
 * This will be the main request handler for the project.
 */

import static spark.Spark.*;

public class Questionnaire {
    public static void main(String[] args) {
        port(80);
        int maxThreads = 8;
        threadPool(maxThreads);

        staticFileLocation("/public"); // Static files

        // Each request spawns a thread to handle it.
        get("/hello", (req, res) -> "Hello World");
    }
}

package db;

/**
 * Created by Adam Furbee on 11/28/2015.
 */

import db.Database;

public class Statistics {
    db.Database database;
    public Statistics(db.Database database) {
        this.database = database;
    }

    public int getTotalUsers() {
        try {
            return Integer.parseInt(database.executeQuery("SELECT COUNT(*) FROM Users").get(0).get("COUNT(*)"));
        }
        catch(Exception e) {
            System.err.println("Statistics.getTotalUsers(): " + e.getMessage());
            return 0;
        }
    }
    public int getTotalQuestions() {
        try {
            return Integer.parseInt(database.executeQuery("SELECT COUNT(*) FROM Questions").get(0).get("COUNT(*)"));
        }
        catch(Exception e) {
            System.err.println("Statistics.getTotalQuestions(): " + e.getMessage());
            return 0;
        }
    }
    public int getTotalAnswers() {
        try {
            return Integer.parseInt(database.executeQuery("SELECT COUNT(*) FROM Answers").get(0).get("COUNT(*)"));
        }
        catch(Exception e) {
            System.err.println("Statistics.getTotalAnswers(): " + e.getMessage());
            return 0;
        }
    }
    public int getTotalUnansweredQuestions() {
        return 0;
    }
}

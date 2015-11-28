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
        return 0;
    }
    public int getTotalQuestions() {
        return 0;
    }
    public int getTotalAnswers() {
        return 0;
    }
    public int getTotalUnansweredQuestions() {
        return 0;
    }
}

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
    public int percentage(int choiceId, int questionId) {
        int numAnswers = 0;
        int totalAnswers = 0;
        try {
            numAnswers = Integer.parseInt(database.executeQuery("SELECT COUNT(*) FROM Answers WHERE cid=" + choiceId).get(0).get("COUNT(*)"));
            totalAnswers = Integer.parseInt(database.executeQuery("SELECT COUNT(*) FROM (Answers NATURAL JOIN Choices) WHERE qid=" + questionId).get(0).get("COUNT(*)"));
            //            List <HashMap< String,String >> qrs = this.executeQuery("SELECT * FROM Questions WHERE qid NOT IN (SELECT qid FROM Answers NATURAL JOIN Choices WHERE username=\"" + username + "\") ORDER BY RANDOM() LIMIT 1;");
            if(totalAnswers == 0)
                return 0;
            double r = numAnswers/(double)totalAnswers;
            return (int)(r*100);
        }
        catch(Exception e){return 0;}
    }
    public int getTotalUnansweredQuestions() {
        return 0;
    }
}

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


/**
 * This package will be used to interface with the Questionnaire database.
 */

/*
 * The purpose of this class is for initialization of the database.
 * As well as interfacing with the database.
 */

package db;

import java.io.File;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.*;

public class Database {
    private final String DB_NAME;
    public Statistics StatisticsHandler;

    /**===============================================
     *  Constructor / Destructor
     *==============================================*/
    public Database(String db){
        DB_NAME = db;
        StatisticsHandler = new Statistics(this);
        if(!this.exists()) {
            System.err.println("No database detected, creating a new one");
            try {
                createDatabaseFile(DB_NAME);
                System.out.println("Creating tables");
                createTables();
                System.out.println("Adding admin user");
                createAdminAccount();
                System.out.println("Adding standard questions");
                createStandardQuestions();
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        else {
            System.out.println("Database Detected. Verifying Database Integrity...");
            try {
                //Temp Testing Goes Here
                List<Question> q = this.getAllQuestions();
                for(Question i : q) {
                    System.out.println(i.getId() + ":" + i.question);
                }
            }
            catch(Exception e) {System.err.println(e.getMessage());}
        }
    }

    /**===============================================
     *  Public Members
     *==============================================*/
    //TODO: Wrap in a question service
    public boolean submitAnswer(int choiceID, String username) {
        return this.executeUpdate("INSERT INTO Answers VALUES ("+ choiceID + ",\"" + username + "\");");
    }
    public boolean insertQuestion(Question question) {
        this.serializeQuestion(question);
        this.executeUpdate("INSERT INTO Questions VALUES ("+question.id+",\"" + question.question + "\"," + question.type.getValue() +");");
        Iterator it = question.choice.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            it.remove(); // avoids a ConcurrentModificationException
            this.executeUpdate("INSERT INTO Choices VALUES ("+ pair.getKey()+ "," + question.id + ",\"" + pair.getValue()+ "\");");
        }
        return false;
    }
    public Question getRandomQuestion(String username) {
        //Here lies Q, unused and forgotten
        String QuestionText = "";
        int QuestionID = 0;
        HashMap <Integer,String > Choices = new HashMap<>(); //more like Hashbrown
        Question.Type QuestionType = Question.Type.MULTIPLE_CHOICE;

        try {
            List <HashMap< String,String >> qrs = this.executeQuery("SELECT * FROM Questions WHERE qid NOT IN (SELECT qid FROM Answers NATURAL JOIN Choices WHERE username=\"" + username + "\") ORDER BY RANDOM() LIMIT 1;");
            QuestionID = Integer.parseInt(qrs.get(0).get("qid"));
            QuestionText = qrs.get(0).get("question");
            QuestionType =  Question.Type.values()[Integer.parseInt(qrs.get(0).get("type"))];
            List <HashMap< String,String >> crs = this.executeQuery("SELECT * FROM Choices WHERE qid = \"" + QuestionID +"\";");
            for(HashMap<String,String> i: crs)
                Choices.put(Integer.parseInt(i.get("cid")), i.get("choice"));
        }

        catch (Exception E){
            System.err.println(E.toString());
        }
        return new Question(QuestionID, QuestionText, Choices, QuestionType);
    }
    List<Question> getAllQuestions(){
        List<Question> r = new LinkedList<Question>();
        try {
            List<HashMap<String, String>> qrs = this.executeQuery("SELECT * FROM Questions;");

            for(HashMap<String,String> i : qrs) {
                int QuestionID = Integer.parseInt(i.get("qid"));
                String QuestionText = i.get("question");
                Question.Type QuestionType =  Question.Type.values()[Integer.parseInt(i.get("type"))];
                HashMap<Integer, String> choices = new HashMap<Integer,String>();

                List <HashMap< String,String >> crs = this.executeQuery("SELECT * FROM Choices WHERE qid = \"" + QuestionID +"\";");
                for(HashMap<String,String> j : crs) {
                    choices.put(Integer.parseInt(j.get("cid")), j.get("choice"));
                }

                r.add(new Question(QuestionID, QuestionText, choices, QuestionType));
            }
        }
        catch(Exception e){}
        return r;
    }

    //TODO: Wrap in a user service
    public boolean deleteUser(String username) {
        //TODO: Make this also delete any answers
        return this.executeUpdate("DELETE FROM Users WHERE username=\""+ username +"\";");
    }
    public boolean changePassword(String username, String password){
        //TODO: Test
        return this.executeUpdate("UPDATE Users SET password=\"" + User.hashPassword(password, username) + "\" WHERE username=\""+ username +"\";");
    }
    public boolean verifyUserCredentials(String username, String password) {
        try {
            List<HashMap<String,String>> rs = this.executeQuery("select * from Users where username = '" + username + "'");
            if(rs.get(0).get("password").equals(User.hashPassword(password, username)))
                return true;
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
    public boolean isAdmin(String username) {
        List<HashMap<String,String>> result;
        try {
            result = this.executeQuery("select * from Users where username = '" + username + "'");
            if(!result.isEmpty() && result.get(0).get("type").equals("0"))
                return true;
        }
        catch(SQLException e) {
            System.err.println("Error in isAdmin()");
            System.err.println(e.toString());
        }
        return false;
    }
    public boolean userExist(String username) {
        try {
            return !this.executeQuery("select * from Users where username = '" + username + "'").isEmpty();
        }
        catch (Exception e) {
            return false;
        }
    }
    public void insertUser(User user) throws UserAlreadyExistException {
        if (!this.executeUpdate("insert into Users values(\"" + user.userName + "\",\""
                + user.email + "\",\"" + user.password + "\"," + user.type.getValue() + ")"))
            throw new UserAlreadyExistException();
    }

    public List<User> getUsers() {
        List<User> r = new LinkedList<>();
        List<HashMap<String, String>> rs;
        try {
            rs = this.executeQuery("SELECT * FROM Users");
        }
        catch(Exception e) {
            System.err.println("Error retrieving users");
            return r;
        }
        for(HashMap<String,String> item : rs) {
            User u = new User(item.get("username"),
                        item.get("email"),
                        item.get("password"),
                        User.Type.values()[(Integer.parseInt(item.get("type")))]);
            r.add(u);
        }
        return r;
    }


    /**===============================================
     *  Private Members
     *==============================================*/
    private void createDatabaseFile(String dbName) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        Connection connection = null;
        try {
            // create a database connection, this will also create the database
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbName);
        }
        catch(SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            // or you do not have write access to create one
            System.err.println(e.getMessage());
        }
        finally {
            try {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e) {
                // connection close failed.
                System.err.println(e);
            }
        }
    }
    private void createTables() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
            Statement stmt = connection.createStatement();
            stmt.execute("PRAGMA foreign_keys = ON;");
            //User Table
            stmt.execute("CREATE TABLE Users(username STRING PRIMARY KEY, " +
                    "email STRING UNIQUE, " +
                    "password STRING, " +
                    "type INTEGER);");
            //Question Table
            stmt.execute("CREATE TABLE Questions(qid INTEGER PRIMARY KEY, " +
                    "question STRING," +
                    "type INTEGER);");
            //Choice Table
            stmt.execute("CREATE TABLE Choices(cid INTEGER PRIMARY KEY ASC, " +
                    "qid INTEGER," +
                    "choice STRING," +
                    "FOREIGN KEY(qid) REFERENCES Questions(qid));");
            //Answer Table
            stmt.execute("CREATE TABLE Answers("+
                    "cid INTEGER," +
                    "username STRING," +
                    "FOREIGN KEY(username) REFERENCES Users," +
                    "FOREIGN KEY(cid) REFERENCES Choices);");
        }
        catch (SQLException e) {
            System.err.println(e);
        }
        finally {
            try {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e) {
                System.err.println(e);
            }
        }
    }
    private void createAdminAccount() {
        User u = new User("admin", "admin", "admin", User.Type.ADMIN);
        try {insertUser(u);}
        catch(Exception e) {System.err.println("Could not create local admin account");}
    }

    private void createStandardQuestions() {
        this.insertQuestion(new Question("How many fingers am I holding up?", new String[] {"12", "0", "3", "2"}));
        this.insertQuestion(new Question("Which answer is correct?", new String[] {"0", "1", "2", "3"}));
        this.insertQuestion(new Question("Today is Friday", new String[] {"True", "False"}));
        this.insertQuestion(new Question("What day of the week is it?", new String[] {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"}));
    }

    //Sanitizes question id's so that they are inserted into the db without collision
    //note that this should only be called on questions that use the lazy constructor
    void serializeQuestion(Question q) {
        int maxQuestionId = 0;
        int maxAnswerId = 0;

        try {maxAnswerId = Integer.parseInt(this.executeQuery("SELECT MAX(cid) FROM Choices").get(0).get("MAX(cid)"));}
        catch(Exception e) {System.err.println("Statistics.serializeQuestion(): " + e.getMessage());}

        try {maxQuestionId = Integer.parseInt(this.executeQuery("SELECT MAX(qid) FROM Questions").get(0).get("MAX(qid)"));}
        catch(Exception e) {System.err.println("Statistics.serializeQuestion(): " + e.getMessage());}

        q.id = maxQuestionId+1;

        int num_choices = q.choice.size();
        Map<Integer,String> choice = new HashMap<>();
        for(int i = 0; i < num_choices; i++)
            choice.put(maxAnswerId+i+1,  q.choice.get(i));
        q.choice = choice;
    }

    protected boolean executeUpdate(String update) {
        try {
            Class.forName("org.sqlite.JDBC");
        }
        catch (ClassNotFoundException e) {
            System.err.println("Unrecoverable Error: SQLite JDBC Driver not found. Exitting");
            System.err.println(e.toString());
            System.exit(1);
        }
        Connection connection = null;
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(10);  // set timeout to 10 sec.
            statement.execute("PRAGMA FOREIGN_KEYS=1"); //Always follow foreign key restraints

            statement.executeUpdate(update);
            return true;
        }
        catch(SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
            return false;
        }
        finally {
            try {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e) {
                // connection close failed.
                System.err.println(e);
            }
        }
    }
    protected List<HashMap<String,String>> executeQuery(String query) throws SQLException {
        Connection connection = null;
        ResultSet rs;
        List<HashMap<String,String>> list = new LinkedList<>();

        try {
            Class.forName("org.sqlite.JDBC");
        }
        catch (ClassNotFoundException e) {
            System.err.println("Unrecoverable Error: SQLite JDBC Driver not found. Exitting");
            System.err.println(e.toString());
            System.exit(1);
        }

        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(10);  // set timeout to 10 sec.

            rs = statement.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();

            //Take the result and transform it into a list of dictionaries

            while(rs.next()) {
                HashMap<String,String> map = new HashMap<>();
                for(int i = 1; i <= rsmd.getColumnCount(); i++)
                    map.put(rsmd.getColumnLabel(i), rs.getString(i));
                list.add(map);
            }

            return list;
        }
        catch(SQLException e) {
            throw e;
        }
        finally {
            try {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e) {
                // connection close failed.
                System.err.println(e);
            }
        }
    }

    //Check if the database already exist or not
    private boolean exists() {
        return new File(DB_NAME).isFile();
    }

    /**===============================================
     *  Exceptions
     *==============================================*/
    public static class BadCredentialsException extends Exception {
        BadCredentialsException() {
            super();
        }
    }
    public static class UserAlreadyExistException extends Exception {
        UserAlreadyExistException() {
            super();
        }
    }
}
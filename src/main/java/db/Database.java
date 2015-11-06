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

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Database {
    private final String DB_NAME = "Questionnaire.db";

    /**===============================================
     *  Constructor / Destructor
     *==============================================*/
    public Database(){
        if(!this.exists()) {
            System.err.println("No database detected, creating a new one");
            try {
                createDatabaseFile(DB_NAME);
                createTables();
                createAdminAccount();
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        else {
            System.out.println("Database Detected. Verifying Database Integrity...");
            try {
                //Temp Testing Goes Here
            }
            catch(Exception e) {System.err.println(e.getMessage());}
        }
    }

    /**===============================================
     *  Public Members
     *==============================================*/
    //TODO: Wrap in a question service
    public boolean submitAnswer(int questionID, int choiceID) {
        //TODO
        return false;
    }
    public boolean insertQuestion(Question question) {
        //TODO
        return false;
    }
    public Question getRandomQuestion(String username) {
        //TODO: Returns a random unanswered question for the user
        return new Question();
    }

    //TODO: Wrap in a user service
    public boolean deleteUser(String username) {
        return this.executeUpdate("DELETE FROM Users WHERE username=\""+ username +"\";");
    }
    public boolean changePassword(String username, String password){
        //TODO
        return false;
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
                    "FOREIGN KEY(qid) REFERENCES Questions);");
            //Answer Table
            stmt.execute("CREATE TABLE Answers(aid INTEGER PRIMARY KEY, " +
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

    private boolean executeUpdate(String update) {
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
    private List<HashMap<String,String>> executeQuery(String query) throws SQLException {
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
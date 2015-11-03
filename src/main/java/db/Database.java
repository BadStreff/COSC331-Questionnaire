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

import java.security.MessageDigest;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import db.Survey;
import db.Question;
import db.User;

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
            //TODO
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
        //TODO: Returns a random unanswere question for the user
        return new Question();
    }

    //TODO: Wrap in a user service
    public boolean deleteUser(String username) {
        //TODO
        return false;
    }
    public boolean verifyUserCredentials(String username, String password) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        Connection connection = null;

        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(10);  // set timeout to 10 sec.
            ResultSet rs = statement.executeQuery("select * from Users where username = '" + username + "'");

            //Check if the username and password match, if not throw an exception
            if (rs.isBeforeFirst() && rs.getString("password").equals(User.hashPassword(password)))
                return true;
            else
                return false;
        }
        catch(SQLException e) {
            System.err.println(e.getMessage());
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
        return false;
    }
    public boolean isAdmin(String username) {
        //TODO
        return false;
    }
    public boolean userExist(String username) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        Connection connection = null;
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(10);  // set timeout to 10 sec.
            ResultSet rs = statement.executeQuery("select * from Users where username = '" + username + "'");

            //If no result, then the user does not exist
            if (!rs.isBeforeFirst())
                return false;
        }
        catch(SQLException e) {
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
        return true;
    }
    public void insertUser(User user) throws ClassNotFoundException,UserAlreadyExistException  {
        Class.forName("org.sqlite.JDBC");
        Connection connection = null;
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(10);  // set timeout to 10 sec.

            statement.executeUpdate("insert into Users values(\"" + user.userName + "\",\""
                    + user.email + "\",\"" + user.password + "\"," + user.type.getValue() + ")");
        }
        catch(SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
            throw new UserAlreadyExistException();
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
                            /*/TODO
                            "creation_timestamp TIMESTAMP," +
                            "publish_timestamp TIMESTAMP," +
                            /**/
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
            /*/Survey Table (Deprecated)
            stmt.execute("CREATE TABLE Surveys(sid INTEGER PRIMARY KEY, " +
                            "name STRING, " +
                            "about STRING, " +
                            "creation_timestamp TIMESTAMP, " +
                            "publish_timestamp TIMESTAMP);");
            /**/
            /*/Completed Question Table (Deprecated)
            stmt.execute("CREATE TABLE Completed(sid INTEGER, " +
                            "date_complete TIMESTAMP);");
            /**/
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
        try {
            insertUser(u);
        }
        catch(Exception e) {System.err.println("Could not create local admin account");}
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
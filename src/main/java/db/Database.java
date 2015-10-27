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
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        else {
            try {createUserSession("blah", "blah");}
            catch (Exception e){}
            System.out.println("Database Detected. Verifying Database Integrity...");
            //TODO
        }
    }

    /**===============================================
     *  Public Members
     *==============================================*/
    public void addSurvey(Survey s) {
        //TODO
    }
    public void deleteSurvey(int sid) {
        //TODO
    }
    public Survey[] getSurveys(){
        //TODO
        return new Survey[0];
    }
    public Survey getSurvey(int sid){
        //TODO
        return new Survey();
    }

    public void submitSurveyAnswers() {
        //TODO
    }

    public int createUserSession(String name, String password) throws BadCredentialsException,ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        Connection connection = null;
        int uId = 0;
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(10);  // set timeout to 10 sec.
            ResultSet rs = statement.executeQuery("select * from Users where username = '" + name + "'");

            //Check if the username and password match, if not throw an exception
            if (rs.isBeforeFirst() && rs.getString("password").equals(User.hashPassword(password)))
                uId = rs.getInt("uid");
            else
                throw new BadCredentialsException();
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
        return uId;//returns the uid of the user or throws an exception if no user found
    }
    public boolean isAdmin(int uId) {
        //TODO
        return false;
    }
    public void insertUser(User user) throws ClassNotFoundException,UserAlreadyExistException  {
        Class.forName("org.sqlite.JDBC");
        Connection connection = null;
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(10);  // set timeout to 10 sec.

            statement.executeUpdate("insert into Users values(" + user.uId + ",\"" + user.userName + "\",\""
                    + user.email + "\",\"" + user.password + "\"," + user.type.getValue() + ")");
        }
        catch(SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
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
            stmt.execute("CREATE TABLE Users(uid INTEGER PRIMARY KEY, " +
                            "username STRING UNIQUE, " +
                            "email STRING, " +
                            "password STRING, " +
                            "type INTEGER);");
            stmt.execute("CREATE TABLE Surveys(sid INTEGER PRIMARY KEY, " +
                            "name STRING, " +
                            "about STRING, " +
                            "creation_timestamp TIMESTAMP, " +
                            "publish_timestamp TIMESTAMP);");
            stmt.execute("CREATE TABLE Questions(qid INTEGER PRIMARY KEY, " +
                            "sid INTEGER," +
                            "type INTEGER," +
                            "choices STRING," +
                            "FOREIGN KEY(sid) REFERENCES Surveys(sid));");
            stmt.execute("CREATE TABLE Answers(aid INTEGER PRIMARY KEY ASC, " +
                            "uid INTEGER," +
                            "qid INTEGER," +
                            "answer STRING," +
                            "FOREIGN KEY(uid) REFERENCES Users," +
                            "FOREIGN KEY(qid) REFERENCES Questions);");
            stmt.execute("CREATE TABLE Completed(sid INTEGER, " +
                            "uid INTEGER," +
                            "date_complete TIMESTAMP);");
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
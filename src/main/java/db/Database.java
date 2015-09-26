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

import db.Survey;
import db.Question;

public class Database {
    private final String DB_NAME = "";

    /**===============================================
     *  Constructor / Destructor
     ===============================================*/
    public Database() {
        //TODO
        if(!this.exists()) {
            createDatabaseFile(DB_NAME);
            createTables();
        }
    }

    /**===============================================
     *  Public Members
     ===============================================*/
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

    /**===============================================
     *  Private Members
     ===============================================*/
    private void createDatabaseFile(String dbName) {
        //TODO
    }
    private void createTables() {
        //TODO
    }
    //Check if the database already exist or not
    private boolean exists() {
        //TODO
        return false;
    }
}
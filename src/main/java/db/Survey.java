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

package db;

import db.Question;

/**
 * This is our survey objects for handling surveys.
 * Note that this object by itself will not interface
 * with the Database class, but will be passed to it
 * for processing.
 *
 * You can think of surveys as having a name, and a collection of questions.
 */
public class Survey {
    final String name;
    final String about;
    final Question[] question;

    public Survey() {
        this.name = "";
        this.about = "";
        this.question = new Question[0];
    }

    public Survey(String name, String about, Question[] question) {
        this.name = name;
        this.about = about;
        this.question = question;
    }

    //Public getters are needed for the velocity engine
    public String getName() { return this.name; }
    public String getAbout() { return this.name; }
    public Question[] getQuestion() { return this.question; }
}
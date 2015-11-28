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

import java.util.Dictionary;
import java.util.Map;
import java.util.HashMap;

/**
 * Note that this class is meant to be handled by the survey class.
 * You can think of a question object as the question along with its choices.
 * Type will be an enum most likely in the format TRUEFALSE,MULTIPLECHOICE,MULTIPLECHECK,OPEN
 * or something of that nature for velocity to parse it correctly.
 */

//TODO: Add support for timestamps

public class Question {
    int id;
    final String question;
    Map<Integer, String> choice;
    final Type type;

    public enum Type {
        MULTIPLE_CHOICE(0), TRUE_FALSE(1), MULTIPLE_SELECT(2), OPEN(3);
        private final int value;
        Type(int value){this.value=value;}
        public int getValue(){return this.value;}
    }

    public Question() {
        this.id = 0;
        this.question = "";
        this.choice = new HashMap<>();
        this.type = Type.OPEN;
    }
    //this should only be used when returning a question, not creating one
    public Question(int id, String question, HashMap<Integer,String> choice, Type type) {
        this.id = id;
        this.question = question;
        this.choice = choice;
        this.type = type;
    }
    //lazy constructor to be used with serializeQuestion()
    public Question(String question, String[] choice) {
        this.id = 0;
        this.question = question;
        this.choice = new HashMap<>();
        for(int i = 0; i < choice.length; i++)
            this.choice.put(i,  choice[i]);
        this.type = Type.MULTIPLE_CHOICE;
    }
    //TODO: Public getters are needed for the velocity engine
    public String getQuestion() { return this.question; }
    public Map<Integer, String> getChoice() { return this.choice; }
    public Type getType() { return this.type; }
    public int getId() { return this.id; }
}
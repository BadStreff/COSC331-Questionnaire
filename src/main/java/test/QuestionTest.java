package test;

import junit.framework.TestCase;

import db.Question;

/**
 * Created by ajf023 on 11/9/2015.
 */
public class QuestionTest extends TestCase {

    public void testGetQuestion() throws Exception {
        String[] c = {};
        Question q = new Question("test", c);
        assertEquals("question must equal 'test'", "test", q.getQuestion());
    }

    public void testGetChoice() throws Exception {
        String[] c = {"test1", "test2"};
        Question q = new Question("test", c);
        assertEquals("test1", q.getChoice().get(0));
        assertEquals("test2", q.getChoice().get(1));
    }

    public void testGetType() throws Exception {
        Question q = new Question();
        assertEquals(Question.Type.OPEN, q.getType());
    }

    public void testGetId() throws Exception {
        Question q = new Question();
        assertEquals(0, q.getId());
    }
}
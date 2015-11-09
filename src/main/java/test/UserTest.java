package test;

import db.User;
import junit.framework.TestCase;

/**
 * Created by ajf023 on 11/9/2015.
 */
public class UserTest extends TestCase {

    public void testHashPassword() throws Exception {
        assertEquals("testing admin:admin","d82494f05d6917ba02f7aaa29689ccb444bb73f20380876cb05d1f37537b7892", User.hashPassword("admin","admin"));
    }
}


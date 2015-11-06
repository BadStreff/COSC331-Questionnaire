package db;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


/**
 * Created by ajf023 on 10/12/2015.
 */
public class User {

    public enum Type {
        ADMIN(0), REGULAR(1);
        private final int value;
        Type(int value) {this.value = value;}
        public int getValue() {return value;}
    }


    final public String userName;
    final public String email;
    final public String password;
    final public Type type;


    public User(){
        this.userName = "";
        this.email = "";
        this.password = "";
        this.type = Type.REGULAR;
    }

    public User(String userName,String email,String password,Type type){
        Random r = new Random();
        this.userName = userName;
        this.email = email;
        this.password = hashPassword(password, userName);
        this.type = type;
    }

    public static String hashPassword(String password, String userName) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.err.print("Unable to hash passwords, storing in plain text");
            return password;
        }

        password = userName + password;

        md.update(password.getBytes());

        byte byteData[] = md.digest();

        //convert the byte to hex format
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++)
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));

        return sb.toString();
    }
}
//Check if the database already exist or not


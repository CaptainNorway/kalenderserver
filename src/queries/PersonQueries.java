package queries;

import database.DBConnect;
import models.Calendar;
import models.Person;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by sondrehj on 26.02.2015.
 */
public class PersonQueries {

    public static Person getPerson(String username) {
        
        ArrayList<Person> person = new ArrayList<>();
        String pass;
        Connection con = DBConnect.getConnection();
        //Execute query
        try {
            String sql = "SELECT * FROM Person " +
                    "WHERE Username = ?;";

            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, username);
            
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            int personID = rs.getInt("PersonID");
            String name = rs.getString("Name");
            
            rs.close();
            pstmt.close();
            con.close();

            return new Person(username, name, personID);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String getPassword(String username) {

        String pass;
        Connection con = DBConnect.getConnection();
        //Execute query
        try {
            String sql = "SELECT Password FROM Person " +
                    "WHERE Username = ?;";

            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            rs.next();
            pass = rs.getString("Password");

            rs.close();
            pstmt.close();
            con.close();
            return pass;
            
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static boolean authenticate(String username, String pass) {

        try {
            Person user = new Person(username, pass);
            if (user.getPassword().equals(getPassword(username))){
                System.out.println("Login complete");
                return true;
            } else {
                System.out.println("Invalid Password");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Username doesn't exist");
            return false;
        }
    }
    
    public static void main(String[] agrs){
        Scanner user_input = new Scanner( System.in );
        System.out.println("Username: ");
        String username = user_input.next();
        System.out.println("Password: ");
        String pass = user_input.next();
        System.out.println(authenticate(username, pass));
    }
}

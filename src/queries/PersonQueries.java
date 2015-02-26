package queries;

import database.DBConnect;
import models.Person;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Created by sondrehj on 26.02.2015.
 */
public class PersonQueries {

    /**
     * Retrieves information about a user, provided the username
     *
     * @param username - username of a user
     * @return Person
     */
    public static Person getPerson(String username) {

        String pass;
        Connection con = DBConnect.getConnection();
        //Execute query
        try {
            String sql = "SELECT * FROM Person WHERE Username = ?;";

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

    /**
     * Creates a person given a Person-object.
     *
     * @param person - Format: username, password, name
     */
    public static void createPerson(Person person) {

        String pass;
        Connection con;
        PreparedStatement prep;
        //Execute query
        try {

            con = DBConnect.getConnection();
            con.setAutoCommit(false);

            String query = "INSERT INTO Person (Username, Password, Name) VALUES (?, ?, ?);";
            prep = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            prep.setString(1, person.getUsername());
            prep.setString(2, person.getPassword());
            prep.setString(3, person.getName());
            prep.executeUpdate();
            ResultSet keys = prep.getGeneratedKeys();
            keys.next();
            int key = keys.getInt(1);

            query = "INSERT INTO UserGroup(GroupName, Private) VALUES (?, ?);";
            prep = con.prepareStatement(query);
            prep.setString(1, person.getName());
            prep.setInt(2, 1);
            prep.executeUpdate();

            query = "INSERT INTO PersonUserGroup (PersonID, UserGroupID) VALUES(?, LAST_INSERT_ID())";
            prep = con.prepareStatement(query);
            prep.setInt(1, key);
            prep.executeUpdate();

            con.commit();
            prep.close();
            con.close();
            System.out.println(person + " was created");
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void deletePerson(Person person) {

        String pass;
        Connection con;
        PreparedStatement prep;
        //Execute query
        try {

            con = DBConnect.getConnection();
            con.setAutoCommit(false);

            String query = "DELETE FROM Person WHERE Username = ?;";
            prep = con.prepareStatement(query);
            prep.setString(1, person.getUsername());
            prep.executeUpdate();

            query = "DELETE FROM UserGroup WHERE GroupName = ? AND private = ?;";
            prep = con.prepareStatement(query);
            prep.setString(1, person.getName());
            prep.setInt(2, 1);
            prep.executeUpdate();
            
            con.commit();
            prep.close();
            con.close();
            System.out.println(person + " was deleted");
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * Retrieves the password of a given user.
     *
     * @param username - Username of a user.
     * @return String
     */
    public static String getPassword(String username) {

        String pass;
        Connection con = DBConnect.getConnection();
        //Execute query
        try {
            String sql = "SELECT Password FROM Person WHERE Username = ?;";
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

    /**
     * Authenticate a user provided the username & password.
     * Uses {@link #getPassword(String) getPassword} method
     *
     * @param username
     * @param pass
     * @return boolean - authenticate True/False
     */
    public static boolean authenticate(String username, String pass) {

        try {
            Person user = new Person(username, pass);
            if (user.getPassword().equals(getPassword(username))) {
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

    public static void main(String[] agrs) {
        //createPerson(new Person("Sondrehh", "1234", "Sondre Hjetland"));
        deletePerson(new Person("Sondrehh", "1234", "Sondre Hjetland"));
        Scanner user_input = new Scanner(System.in);
        System.out.println("Username: ");
        String username = user_input.next();
        System.out.println("Password: ");
        String pass = user_input.next();
        System.out.println(authenticate(username, pass));
    }
}

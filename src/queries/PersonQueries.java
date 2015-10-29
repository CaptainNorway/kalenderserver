package queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import models.Person;
import database.DBConnect;

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
            String flag = rs.getString("Flag");

            rs.close();
            pstmt.close();
            con.close();

            return new Person(personID, username, name, flag);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public static ArrayList<Person> getAllPersons() {

        String pass;
        Connection con = DBConnect.getConnection();
        ArrayList<Person> persons = new ArrayList<Person>();
        //Execute query
        try {
            String sql = "SELECT * FROM Person;";

            PreparedStatement pstmt = con.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();
	        while(rs.next()){
	            int personID = rs.getInt("PersonID");
	            String username = rs.getString("Username");
	            String name = rs.getString("Name");
	            String flag = rs.getString("Flag");
	            persons.add(new Person(personID, username,name,flag));
	        }
            
            rs.close();
            pstmt.close();
            con.close();

            return persons;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Creates a person given a Person-object.
     *
     * @param person - Format: username, password (hash), salt, name, flag
     */
    public static void createPerson(Person person) {

        String pass;
        Connection con;
        PreparedStatement prep;

        try {
            con = DBConnect.getConnection();
            con.setAutoCommit(false);

            String query = "INSERT INTO Person (Username, Password, Salt, Name, Flag) VALUES (?, ?, ?, ?, ?);";
            prep = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            prep.setString(1, person.getUsername());
            prep.setString(2, person.getPassword());
            prep.setString(3, person.getSalt());
            prep.setString(4, person.getName());
            prep.setString(5, person.getFlag());

            prep.executeUpdate();
            ResultSet keys = prep.getGeneratedKeys();
            keys.next();
            int key = keys.getInt(1);

            query = "INSERT INTO UserGroup(GroupName, Private) VALUES (?, ?);";
            prep = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            prep.setString(1, person.getName());
            prep.setInt(2, 1);
            prep.executeUpdate();
            ResultSet keys3 = prep.getGeneratedKeys();
            keys3.next();
            int userGroupID = keys3.getInt(1);

            query = "INSERT INTO PersonUserGroup (PersonID, UserGroupID) VALUES(?, LAST_INSERT_ID())";
            prep = con.prepareStatement(query);
            prep.setInt(1, key);
            prep.executeUpdate();

            query = "INSERT INTO Calendar (CalendarName) VALUES(?)";
            prep = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            prep.setString(1, person.getName());
            prep.executeUpdate();
            ResultSet keysInt = prep.getGeneratedKeys();
            keysInt.next();
            int calendarID = keysInt.getInt(1);

            query = "INSERT INTO UserCalendar (UserGroupID, CalendarID) VALUES(?, ?)";
            prep = con.prepareStatement(query);
            prep.setInt(1, userGroupID);
            prep.setInt(2, calendarID);
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
            
            String query = "SELECT COUNT(*) AS total FROM Person WHERE Name = ? AND Flag = ? ;";
            prep = con.prepareStatement(query);
            prep.setString(1, person.getUsername());
            prep.setString(2, "a");
            ResultSet rs = prep.executeQuery();
            rs.next();
            int count = rs.getInt("total");
            
            if (count == 0){
	            query = "DELETE FROM Person WHERE Name = ? AND Flag = ?;";
	            prep = con.prepareStatement(query);
	            prep.setString(1, person.getUsername()); //OBJEKTET DEN MOTTAR HAR NAME SOM USERNAME
	            prep.setString(2, "u");
	            prep.executeUpdate();
	
	            query = "DELETE FROM UserGroup WHERE GroupName = ? AND private = ?;";
	            prep = con.prepareStatement(query);
	            prep.setString(1, person.getUsername()); //SE KOMMENTAR OVER
	            prep.setInt(2, 1);
	            prep.executeUpdate();
	
	            query = "DELETE FROM Calendar WHERE CalendarName = ?;";
	            prep = con.prepareStatement(query);
	            prep.setString(1, person.getUsername()); //SE KOMMENTAR OVER
	            prep.executeUpdate();
	            System.out.println(person + " was deleted");
            }else{
            	System.out.println(person + " is admin and can not be deleted.");
            }
            
            con.commit();
            prep.close();
            con.close();
            
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

    public static Person getSalt(Person p) {

        Person saltWrapper;
        Connection con = DBConnect.getConnection();
        //Execute query
        try {
            String sql = "SELECT Salt FROM Person WHERE Username = ?;";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, p.getUsername());
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            String salt = rs.getString("Salt");

            saltWrapper = new Person(p.getUsername(), null, salt);

            rs.close();
            pstmt.close();
            con.close();
            return saltWrapper;

        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * Authenticate a user provided the username & password.
     * Uses {@link #getPassword(String) getPassword} method
     *
     *
     * @return boolean - authenticate True/False
     */
    public static Person authenticate(Person user) {
        try {
            if (user.getPassword().equals(getPassword(user.getUsername()))) {
                System.out.println("Login complete");
                return getPerson(user.getUsername());
            } else {
                System.out.println("Invalid Password");
                return null;
            }
        } catch (Exception e) {
            System.out.println("Username doesn't exist");
            return null;
        }
    }
}

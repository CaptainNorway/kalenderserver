package queries;

import database.DBConnect;
import models.Calendar;
import models.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by sondrehj on 26.02.2015.
 */
public class PersonQueries {

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
    
    public Person authenticate(){

      return new Person(1,"s","s","s");
    }
    
    
    
    public static void main(String[] agrs){
        System.out.println(PersonQueries.getPassword("eirik"));
        
    }
    
    
    
}

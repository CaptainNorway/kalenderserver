package queries;

import database.DBConnect;
import models.Calendar;
import models.Person;
import models.UserGroup;

import java.sql.*;
import java.util.ArrayList;

public class CalendarQueries {

    public static ArrayList<UserGroup> getCalendars(UserGroup userGroup) throws SQLException {
        ArrayList<Calendar> calendars = new ArrayList<>();
        Connection con = DBConnect.getConnection();
        //Execute query
        
        String sql = "SELECT * FROM UserGroup";
        PreparedStatement pstmt = con.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery(sql);

        while (rs.next()) {
            int UserGroupID = rs.getInt("UserGroupID");
            System.out.println(UserGroupID);
        }
        rs.close();
        pstmt.close();
        con.close();
        return users;
    }
    
    public static void main (String[] args) throws SQLException {
        ArrayList<Person> person =  new ArrayList<>();
        getCalendars(new UserGroup(3,"Sondre", person));
        
    }
    
    
}

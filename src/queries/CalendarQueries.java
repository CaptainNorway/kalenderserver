package queries;

import database.DBConnect;
import models.UserGroup;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CalendarQueries {

    public static ArrayList<UserGroup> getUserGroup() throws SQLException {
        ArrayList<UserGroup> users = new ArrayList<>();
        Connection con = DBConnect.getConnection();
        //Execute query
        Statement stmt = con.createStatement();
        String sql = "SELECT * FROM UserGroup";
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            int UserGroupID = rs.getInt("UserGroupID");
            System.out.println(UserGroupID);
        }
        rs.close();
        stmt.close();
        con.close();
        return users;
    }
    
    public static void main (String[] args) throws SQLException {
        getUserGroup();
        
    }
    
    
}

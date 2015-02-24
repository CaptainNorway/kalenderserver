package queries;

import database.DBConnect;
import models.Calendar;
import models.Person;
import models.UserGroup;
import java.sql.*;
import java.util.ArrayList;

public class CalendarQueries {

    public static ArrayList<Calendar> getCalendars(UserGroup userGroup) throws SQLException {
        ArrayList<Calendar> calendars = new ArrayList<>();
        ArrayList<UserGroup> userGroups = new ArrayList<>();
        Connection con = DBConnect.getConnection();
        //Execute query
        String sql = "SELECT c.CalendarID, c.Name\n"
                + "FROM Calendar AS c\n"
                + "JOIN UserCalendar AS uc ON c.CalendarID = uc.CalendarID\n"
                + "WHERE uc.UserGroupID = ?";
        
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, userGroup.getUserGroupID());
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            int CalendarID = rs.getInt("CalendarID");
            String CalendarName = rs.getString("Name");
            calendars.add(new Calendar(CalendarID, CalendarName, getCalendarUserGroups(CalendarID, con)));
        }
        rs.close();
        pstmt.close();
        con.close();
        return calendars;
    }
    
    public static ArrayList<UserGroup> getCalendarUserGroups (int CalendarID, Connection con) throws SQLException {
        ArrayList<UserGroup> userGroups = new ArrayList<>();
        //Execute query
        String sql = "SELECT uc.UserGroupID, u.name\n"
                + "FROM UserCalendar AS uc JOIN UserGroup AS u\n"
                + "ON uc.UserGroupID = u.UserGroupID\n"
                + "WHERE uc.CalendarID = ?";

        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, CalendarID);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            int UserGroupID = rs.getInt("UserGroupID");
            String UserGroupName = rs.getString("Name");
            userGroups.add(new UserGroup(UserGroupID, UserGroupName, null));
        }
        rs.close();
        pstmt.close();
        return userGroups;
    }

    public static void main (String[] args) throws SQLException {
        ArrayList<Person> u = new ArrayList<>();
        System.out.println(getCalendars(new UserGroup(1, "Sondre", u)));
    }
}

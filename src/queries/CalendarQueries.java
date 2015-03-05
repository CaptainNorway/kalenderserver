package queries;

import database.DBConnect;
import models.Calendar;
import models.UserGroup;
import java.sql.*;
import java.util.ArrayList;

public class CalendarQueries {

    /**
     * Adds a given UserGroup to a given Calendar.
     * @param calendar
     * @param usergroup 
     */
    public static void addUserGroup(Calendar calendar, UserGroup usergroup) {

        Connection con = DBConnect.getConnection();
        //Execute query
        try {
            String sql =
                    "INSERT INTO UserCalendar"
                            + "(UserGroupID, CalendarID) VALUES"
                            + "(?, ?)";

            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, usergroup.getUserGroupID());
            pstmt.setInt(2, calendar.getCalendarID());
            pstmt.executeUpdate();
            System.out.println("INSERTION was successful");
            pstmt.close();
            con.close();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Removes a given UserGroup from a given Calendar.
     * @param calendar
     * @param usergroup
     */
    public static void removeUserGroup(Calendar calendar, UserGroup usergroup) {

        Connection con = DBConnect.getConnection();
        //Execute query
        try {
            String sql = "DELETE FROM UserCalendar " +
                    "WHERE UserGroupID = ? AND CalendarID = ?;";

            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, usergroup.getUserGroupID());
            pstmt.setInt(2, calendar.getCalendarID());
            pstmt.executeUpdate();
            System.out.println("DELETION was successful");
            pstmt.close();
            con.close();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    /**
     * Creates an empty Calendar from a Calendar-object.
     * @param calendar
     */
    public static Calendar createCalendar(Calendar calendar) {
        Connection con = DBConnect.getConnection();
        //Execute query
        try {
            String sql =
                    "INSERT INTO Calendar"
                            + "(CalendarID, CalendarName) VALUES"
                            + "(null, ?)";

            PreparedStatement pstmt = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, calendar.getName());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            System.out.println("Calendar: " + calendar.toString() + "was created");
            pstmt.close();
            con.close();
            rs.next();
            calendar.setCalendarID(rs.getInt(1));
            return calendar;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    /**
     * Deletes a given Calendar.
     * @param calendar
     */
    public static void deleteCalendar(Calendar calendar) {
        Connection con = DBConnect.getConnection();
        //Execute query
        try {
            String sql = "DELETE FROM Calendar " +
                    "WHERE CalendarID = ?;";

            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, calendar.getCalendarID());
            pstmt.executeUpdate();
            System.out.println("Calendar: " + calendar.toString() + "was deleted");
            pstmt.close();
            con.close();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Get all Calendars of a given UserGroup. userGroupID must exist and be valid.
     * @param userGroup
     * @return ArrayList<Calendar> 
     */
    public static ArrayList<Calendar> getCalendars(UserGroup userGroup) {
        ArrayList<Calendar> calendars = new ArrayList<>();
        ArrayList<UserGroup> userGroups = new ArrayList<>();
        Connection con = DBConnect.getConnection();
        //Execute query
        try {
            String sql = "SELECT c.CalendarID, c.CalendarName\n"
                    + "FROM Calendar AS c\n"
                    + "JOIN UserCalendar AS uc ON c.CalendarID = uc.CalendarID\n"
                    + "WHERE uc.UserGroupID = ?";

            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, userGroup.getUserGroupID());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int CalendarID = rs.getInt("CalendarID");
                String CalendarName = rs.getString("CalendarName");
                calendars.add(new Calendar(CalendarID, CalendarName, getCalendarUserGroups(CalendarID, con)));
            }
            rs.close();
            pstmt.close();
            con.close();
            return calendars;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Get all UserGroups of a given calendar. Used by getCalendars(UserGroup)
     * @param CalendarID - The ID of the Calendar
     * @param con - A database connection.
     * @return ArrayList<UserGroup>
     */
    public static ArrayList<UserGroup> getCalendarUserGroups(int CalendarID, Connection con) {
        ArrayList<UserGroup> userGroups = new ArrayList<>();
        //Execute query
        try {
            String sql = "SELECT uc.UserGroupID, u.GroupName\n"
                    + "FROM UserCalendar AS uc JOIN UserGroup AS u\n"
                    + "ON uc.UserGroupID = u.UserGroupID\n"
                    + "WHERE uc.CalendarID = ?";

            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, CalendarID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int UserGroupID = rs.getInt("UserGroupID");
                String UserGroupName = rs.getString("GroupName");
                userGroups.add(new UserGroup(UserGroupID, UserGroupName, null));
            }
            rs.close();
            pstmt.close();
            return userGroups;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void main(String[] args) throws SQLException {
        Calendar c = new Calendar(4, "Sondre2", null);
        UserGroup u = new UserGroup(4, "Pelle", null);
        //removeUserGroup(c, u);
        //createCalendar(c);
        //addUserGroup(c, u);
        //deleteCalendar(c);
    }
}
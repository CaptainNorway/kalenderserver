package queries;

//yolo
import database.DBConnect;
import models.Calendar;
import models.Person;
import models.UserGroup;

import java.sql.*;
import java.util.ArrayList;

public class CalendarQueries {

	
	public static void checkUpdateCounts(int[] updateCounts) {
		for (int i = 0; i < updateCounts.length; i++) {
			if (updateCounts[i] >= 0) {
				System.out.println("Successfully executed; updateCount=" + updateCounts[i]);
			} else if (updateCounts[i] == Statement.SUCCESS_NO_INFO) {
				System.out.println("Successfully executed; updateCount=Statement.SUCCESS_NO_INFO");
			} else if (updateCounts[i] == Statement.EXECUTE_FAILED) {
				System.out.println("Failed to execute; updateCount=Statement.EXECUTE_FAILED");
			}
		}
	}
	
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
        	con.setAutoCommit(false);
            String sql =
                    "INSERT INTO Calendar"
                            + "(CalendarName) VALUES"
                            + " (?);";

            PreparedStatement pstmt = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, calendar.getName());
            pstmt.executeUpdate();
            ResultSet keys = pstmt.getGeneratedKeys();
            keys.next();
            calendar.setCalendarID(keys.getInt(1));
            keys.close();
            
    		sql = "INSERT INTO UserCalendar(UserGroupID, CalendarID) VALUES (?,?); ";
    		pstmt = con.prepareStatement(sql);
    		for (UserGroup ug : calendar.getUserGroups()){
    			pstmt.setInt(1, ug.getUserGroupID());
    			pstmt.setInt(2, calendar.getCalendarID());
    			pstmt.addBatch();
    		}
    		int[] updateCounts = pstmt.executeBatch();
    		checkUpdateCounts(updateCounts);
    		con.commit();
            pstmt.close();
            con.close();
            
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
     * Get all Calendars that a Person can see, both personal usergroup and other subscribed usergroups.
     * @param userGroup
     * @return ArrayList<Calendar> 
     */
    public static ArrayList<Calendar> getCalendars(Person person) {
        ArrayList<Calendar> calendars = new ArrayList<>();
        ArrayList<UserGroup> userGroups = new ArrayList<>();
        Connection con = DBConnect.getConnection();
        //Execute query
        try {
        	String sql = "SELECT * FROM PersonUserGroup WHERE PersonID = ? ";
        	PreparedStatement pstmt = con.prepareStatement(sql);
        	pstmt.setInt(1, person.getPersonID());
        	ResultSet rs = pstmt.executeQuery();
        	while (rs.next()){
        		UserGroup ug = new UserGroup(rs.getInt("UserGroupID"), null, null, 1);
        		userGroups.add(ug);
        	}
        	rs.close();
        	
            sql = "SELECT c.CalendarID, c.CalendarName FROM Calendar AS c JOIN UserCalendar AS uc ON c.CalendarID = uc.CalendarID WHERE uc.UserGroupID = ? ";
            for (int i = 1; i < userGroups.size(); i++){
            	sql += "OR uc.UserGroupID = ? ";
            }
            pstmt = con.prepareStatement(sql);
            for (int i = 0; i < userGroups.size(); i++){
            	pstmt.setInt(i+1, userGroups.get(i).getUserGroupID());
            }
            System.out.println(pstmt);
            rs = pstmt.executeQuery();
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

    public static ArrayList<Calendar> getAllCalendars() {
        ArrayList<Calendar> calendars = new ArrayList<>();
        Connection con = DBConnect.getConnection();
        //Execute query
        try {
            String sql = "SELECT * FROM Calendar";
            PreparedStatement pstmt = con.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int CalendarID = rs.getInt("CalendarID");
                String CalendarName = rs.getString("CalendarName");
                calendars.add(new Calendar(CalendarID, CalendarName, null));
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
            String sql = "SELECT uc.UserGroupID, u.GroupName, u.Private\n"
                    + "FROM UserCalendar AS uc JOIN UserGroup AS u\n"
                    + "ON uc.UserGroupID = u.UserGroupID\n"
                    + "WHERE uc.CalendarID = ?";

            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, CalendarID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int UserGroupID = rs.getInt("UserGroupID");
                String UserGroupName = rs.getString("GroupName");
                userGroups.add(new UserGroup(UserGroupID, UserGroupName, null, rs.getInt("Private")));
            }
            rs.close();
            pstmt.close();
            return userGroups;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

	public static ArrayList<UserGroup> getUserGroupsInCalendar(Calendar calendar) {
		ArrayList<UserGroup> userGroups = new ArrayList<>();
		ArrayList<Person> persons = new ArrayList<>();
		Connection con = DBConnect.getConnection();
        //Execute query
        try {
            String sql = "SELECT * FROM PersonUserGroup WHERE UserGroupID = ? ";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, calendar.getUserGroups().get(0).getUserGroupID());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                persons.add(new Person(rs.getInt("PersonID"), null, null, null));
            }
            rs.close();
            pstmt.close();
            
            sql = "SELECT * FROM UserGroup NATURAL JOIN PersonUserGroup WHERE (PersonID = ? AND Private = ? ) ";
            for (int i = 1; i < persons.size(); i++){
            	sql += "OR (PersonID = ? AND Private = ? ) ";
            }
            pstmt = con.prepareStatement(sql);
            for (int i = 0; i/2 < persons.size(); i+=2){
            	pstmt.setInt(i+1, persons.get(i/2).getPersonID());
            	pstmt.setInt(i+2, 1);
            }
            System.out.println(pstmt);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int userGroupID = rs.getInt("UserGroupID");
                String userGroupName = rs.getString("GroupName");
                userGroups.add(new UserGroup(userGroupID, userGroupName, null, 1));
            }            		
            return userGroups;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
	}
}
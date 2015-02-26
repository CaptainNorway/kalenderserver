package queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import database.DBConnect;
import models.Event;
import models.Calendar;
import models.UserGroup;
import queries.CalendarQueries;

public class EventQueries {

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
     * Get all the evens from an ArrayList of calendars.
     * @param cal
     */
	public static ArrayList<Event> getEvents(ArrayList<Calendar> cal){
		Connection conn = null;
	    PreparedStatement pstmt = null;
	    ArrayList<Event> events = new ArrayList<>();
	    if (cal.size() > 0){
		    try {
	            conn = DBConnect.getConnection();
		        conn.setAutoCommit(false);
		        String query = "SELECT * FROM Calendar NATURAL JOIN CalendarEvent NATURAL JOIN Event WHERE ";
	            for (int i = 0; i< cal.size(); i++) {
	            	System.out.println("In loop");
	                if (i != 0){
	                    query += "OR ";
	                }
	                query = query + "CalendarID = ? ";
	            }
	            pstmt = conn.prepareStatement(query);
	            for (int i = 0; i < cal.size(); i++) {
	                pstmt.setInt(i+1, cal.get(i).getCalendarID());
	            }
	
	            ResultSet result = pstmt.executeQuery();
	            System.out.println("We found something");
	
	            while (result.next()) {
	                int eventID = result.getInt("EventID");
	                int calendarID = result.getInt("CalendarID");
	                String calendarName = result.getString("CalendarName");
	                String eventName = result.getString("EventName");
	                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
	                System.out.println(result.getTimestamp("From").toString());
	                LocalDateTime from = LocalDateTime.parse(result.getTimestamp("From").toString(), formatter);
	                LocalDateTime to = LocalDateTime.parse(result.getTimestamp("To").toString(), formatter);
	
	                ArrayList<Calendar> templist = new ArrayList<>();
	                templist.add(new Calendar(calendarID, null, null));
	
	                Calendar calendar = new Calendar (calendarID, calendarName, null);
	                events.add(new Event(eventID, eventName, null, from, to, calendar));
	            }
	            result.close();
	            pstmt.close();
			    conn.close();
	        } catch (Exception e){
	            System.out.println(e);
	        }
	        return events;
		}else{
			System.out.println("The parameter contains no calendars");
			return null;
		}
	}
}
	
//	public static void main(String[] args) {
//		UserGroup ug = new UserGroup(5, "Fellesprosjekt", null);
//		ArrayList<Calendar> cal = CalendarQueries.getCalendars(ug);
//		ArrayList<Event> events = getEvents(cal);
//		for (Event event : events){
//			System.out.println(event.toString());
//		}
//       createEmptyUserGroup(ug);

//		UserGroup us1 = new UserGroup(1, null, null);
//		UserGroup us2 = new UserGroup(3, null, null);
//		UserGroup us3 = new UserGroup(4, null, null);
//		UserGroup us4 = new UserGroup(5, null, null);
//		ArrayList<UserGroup> groups = new ArrayList<>();
//		groups.add(us1);groups.add(us2);groups.add(us3);groups.add(us4);
//		getPersons(groups);

//		ArrayList<Calendar> cals = new ArrayList<>();
//		Calendar cal1 = new Calendar(1, null, null);
//		Calendar cal2 = new Calendar(2, null, null);
//		Calendar cal3 = new Calendar(3, null, null);
//		Calendar cal4 = new Calendar(4, null, null);
//		cals.add(cal1);cals.add(cal2);cals.add(cal3);cals.add(cal4);
//		ArrayList<UserGroup> users = getUserGroups(cals);
//		for(UserGroup g: users){
//			System.out.println(g.getName());
//		}

//		UserGroup ug = new UserGroup(6, null, null);
//		ArrayList<UserGroup> cals = new ArrayList<UserGroup>();
//		cals.add(ug);
//		deleteUserGroups(cals);

//      createEmptyUserGroup("SuperUserGroup");
//    }
//}


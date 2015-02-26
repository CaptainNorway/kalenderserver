package queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

	/**
	 * Creates an Event with the given name. 
	 * This method could be improved to do everything in one query.
	 * @param users
	 */
	
	/*Følgende kall fungerer i mysqladmin hos NTNU, men ikke som et kall gjennom Java.
	 *START TRANSACTION;
	 *INSERT INTO `Event`(`EventName`, `From`, `To`) VALUES ('Vaske bort kaffi', '2015-05-03T15:39', '2015-05-03T19:41');
	 *INSERT INTO CalendarEvent(CalendarID, EventID) SELECT 1, LAST_INSERT_ID();
	 *COMMIT;
	 */
	public static void createEvent(Event event){
		Connection con = null;
		PreparedStatement prep;
		try{
			con = DBConnect.getConnection();
			String query = "INSERT INTO `Event`(`EventName`, `From`, `To`) VALUES (?, ?, ?);";
			prep = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			prep.setString(1, event.getName());
			prep.setString(2, event.getFrom().toString());
			prep.setString(3, event.getTo().toString());
			System.out.println(prep.toString());
			prep.executeUpdate();
			System.out.println("Executed");
			ResultSet keys = prep.getGeneratedKeys();
			keys.next();
			int key = keys.getInt(1);
			query = "INSERT INTO CalendarEvent(CalendarID, EventID) VALUES (?, ?);";
			prep = con.prepareStatement(query);
			prep.setInt(1, event.getCal().getCalendarID());
			prep.setInt(2, key);
			System.out.println(prep.toString());
			prep.execute();
			prep.close();
		    con.close();
		} catch(SQLException e){
			System.out.println(e);
		}
	}
	
	/**
	 * Edit an event that already exists.
	 * @param event
	 */
	public static void editEvent(Event event){
		Connection con = null;
		PreparedStatement prep;
		try{
			con = DBConnect.getConnection();
			String query = "UPDATE `Event` "
					+ "SET `EventName` = ?, "
					+ "`From` = ?, "
					+ "`To` = ? "
					+ "WHERE `EventID` = ?";				
			prep = con.prepareStatement(query);
			prep.setString(1, event.getName());
			prep.setString(2, event.getFrom().toString());
			prep.setString(3, event.getTo().toString());
			prep.setInt(4, event.getEventID());
			System.out.println(prep.toString());
			prep.execute();
			System.out.println("Executed");
			prep.close();
		    con.close();
		} catch(SQLException e){
			System.out.println(e);
		}
	}
	
	/**
	 * Delete an event.
	 * @param event
	 */
	public static void deleteEvent(Event event){
		Connection con = null;
		PreparedStatement prep;
		try{
			con = DBConnect.getConnection();
			String query = "DELETE FROM Event WHERE EventID = ?";			
			prep = con.prepareStatement(query);
			prep.setInt(1, event.getEventID());
			System.out.println(prep.toString());
			prep.execute();
			System.out.println("Executed");
			prep.close();
		    con.close();
		} catch(SQLException e){
			System.out.println(e);
		}
	}
/*	
	public static void main(String[] args) {
		
		Calendar cal = new Calendar(2, "Yolo", null);
		Event ev = new Event(22, "Vaske bort kaffi", null, LocalDateTime.parse("2015-05-03T15:39:00"), LocalDateTime.parse("2015-05-03T19:41:00"), cal);

		createEvent(ev);
		deleteEvent(ev);
		editEvent(ev);

		UserGroup ug = new UserGroup(5, "Fellesprosjekt", null);
		ArrayList<Calendar> cal3 = CalendarQueries.getCalendars(ug);
		ArrayList<Event> events = getEvents(cal3);
		for (Event event : events){
			System.out.println(event.toString());
		}
	}
	*/
}


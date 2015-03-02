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
	 * @param users
	 */
	
	public static void createEvent(Event event){
		Connection con = null;
		PreparedStatement prep;
		try{
			con = DBConnect.getConnection();
			con.setAutoCommit(false);
			
			String query = "INSERT INTO `Event`(`EventName`, `From`, `To`) VALUES (?, ?, ?);";
			prep = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			prep.setString(1, event.getName());
			prep.setString(2, event.getFrom().toString());
			prep.setString(3, event.getTo().toString());
			prep.executeUpdate();
			ResultSet keys = prep.getGeneratedKeys();
			keys.next();
			int key = keys.getInt(1);

			query = "INSERT INTO CalendarEvent(CalendarID, EventID) SELECT ?, LAST_INSERT_ID();";
			prep = con.prepareStatement(query);
			prep.setInt(1, event.getCal().getCalendarID());
			prep.executeUpdate();
			con.commit();

			event.setEventID(key);
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
		
		Calendar cal = new Calendar(3, "Yolo", null);
		Event ev = new Event(22, "Slå ned Sigurd", null, LocalDateTime.parse("2015-03-03T05:39:00"), LocalDateTime.parse("2015-03-03T05:41:00"), cal);

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


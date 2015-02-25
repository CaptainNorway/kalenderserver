package queries;

import java.sql.BatchUpdateException;
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

	public static ArrayList<Event> getEvents(ArrayList<Calendar> cal){
		Connection conn = null;
	    PreparedStatement pstmt = null;
	    ArrayList<Event> events = new ArrayList<>();
	    try {
	      conn = DBConnect.getConnection();
	      conn.setAutoCommit(false);
	      String query = ""
	      		+ "SELECT * "
	      		+ "FROM Calendar NATURAL JOIN CalendarEvent NATURAL JOIN Event "
	      		+ "WHERE ";
	      for (int i = 0; i< cal.size(); i++){
	    	  if (i != 0){
	    		  query += "OR ";
	    	  }
	    	  query = query + "CalendarID = '?' ";
	      }
	      pstmt = conn.prepareStatement(query);
	      for (int i = 0; i < cal.size(); i++){
	    	  pstmt.setInt(i+1, cal.get(i).getCalendarID());
	      }
	      
	      ResultSet result = pstmt.executeQuery(query);
	      
	      System.out.println("We found something");
	      
	      while (result.next()) {
	    	  int eventID = result.getInt("EventID");
	    	  int calendarID = result.getInt("CalendarID");
	    	  String calendarName = result.getString("CalendarName");
	    	  String eventName = result.getString("Name");
	    	  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-mm-dd hh:mm:ss.fffffffff");
	    	  LocalDateTime from = LocalDateTime.parse(result.getTimestamp("From").toString(), formatter);
	    	  LocalDateTime to = LocalDateTime.parse(result.getTimestamp("To").toString(), formatter);
	    	  
	    	  ArrayList<Calendar> templist = new ArrayList<>();
	    	  templist.add(new Calendar(calendarID, null, null));
	    	  
	    	  // getUserGroup må lages!!!!!!!!!!
	    	  Calendar calendar = new Calendar (calendarID, calendarName, getUserGroup(templist));
	    	  
	    	  events.add(new Event(eventID, eventName, null, from, to, calendar));
	      }
	      
		return events;
	  }catch (Exception e){
		System.out.println(e);
	  }
	}
}


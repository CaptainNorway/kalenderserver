package queries;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import database.DBConnect;
import models.*;
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
	public static ArrayList<Event> getEvents(ArrayList<Calendar> cal, UserGroup ug){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ArrayList<Event> events = new ArrayList<>();
		if (cal.size() > 0){
			try {
				conn = DBConnect.getConnection();
				conn.setAutoCommit(false);
				String query = "SELECT DISTINCT * FROM Calendar NATURAL JOIN CalendarEvent NATURAL JOIN Event NATURAL JOIN Attends WHERE ";
				for (int i = 0; i< cal.size(); i++) {
					if (i != 0){
						query += "OR ";
					}
					query = query + "(CalendarID = ? AND UserGroupID = ? )";
				}
				pstmt = conn.prepareStatement(query);
				for (int i = 0; i/2 < cal.size(); i+=2) {
					pstmt.setInt(i+1, cal.get(i/2).getCalendarID());
					pstmt.setInt(i+2, ug.getUserGroupID());
				}

				ResultSet result = pstmt.executeQuery();
				while (result.next()) {
					int eventID = result.getInt("EventID");
					int calendarID = result.getInt("CalendarID");
					String calendarName = result.getString("CalendarName");
					String eventName = result.getString("EventName");
					String eventNote = result.getString("EventNote");
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
					System.out.println(result.getTimestamp("From").toString());
					LocalDateTime from = LocalDateTime.parse(result.getTimestamp("From").toString(), formatter);
					LocalDateTime to = LocalDateTime.parse(result.getTimestamp("To").toString(), formatter);
					int attends = result.getInt("Attends");

					ArrayList<Calendar> templist = new ArrayList<>();
					templist.add(new Calendar(calendarID, null, null));

					Calendar calendar = new Calendar (calendarID, calendarName, null);
					events.add(new Event(eventID, eventName, eventNote, null, from, to, calendar, attends));
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
	 * Get all the events for one calendar.
	 * @param cal
	 */
	public static ArrayList<Event> getEvents(Calendar cal){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ArrayList<Event> events = new ArrayList<>();
		try {
			conn = DBConnect.getConnection();
			conn.setAutoCommit(false);
			String query = "SELECT DISTINCT * FROM Event NATURAL JOIN CalendarEvent WHERE CalendarID = ? ";
			pstmt = conn.prepareStatement(query);
				pstmt.setInt(1, cal.getCalendarID());
			ResultSet result = pstmt.executeQuery();
			while (result.next()) {
				int eventID = result.getInt("EventID");
				int calendarID = result.getInt("CalendarID");
				String eventName = result.getString("EventName");
				String eventNote = result.getString("EventNote");
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
				LocalDateTime from = LocalDateTime.parse(result.getTimestamp("From").toString(), formatter);
				LocalDateTime to = LocalDateTime.parse(result.getTimestamp("To").toString(), formatter);
				Calendar calendar = new Calendar (calendarID, null, null);
				events.add(new Event(eventID, eventName, eventNote, null, from, to, calendar,-1));
			}
			result.close();
			pstmt.close();
			conn.close();
		} catch (Exception e){
			System.out.println(e);
		}
		return events;

	}

	/**
	 * Creates an Event with the given name.
	 * @param users
	 */

	public static Event createEvent(Event event){
		Connection con = null;
		PreparedStatement prep;
		try{
			con = DBConnect.getConnection();
			con.setAutoCommit(false);

			String query = "INSERT INTO `Event`(`EventName`, `EventNote`, `From`, `To`) VALUES (?, ?, ?, ?);";
			prep = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			prep.setString(1, event.getName());
			prep.setString(2, event.getNote());
			prep.setString(3, event.getFrom().toString());
			prep.setString(4, event.getTo().toString());
			prep.executeUpdate();
			ResultSet keys = prep.getGeneratedKeys();
			keys.next();
			int key = keys.getInt(1);
			event.setEventID(key);

			query = "INSERT INTO CalendarEvent(CalendarID, EventID) SELECT ?, LAST_INSERT_ID();";
			prep = con.prepareStatement(query);
			prep.setInt(1, event.getCal().getCalendarID());
			prep.executeUpdate();
			
			query = "INSERT INTO Attends(UserGroupID, EventID, Attends) VALUES (?,?,?);";
			prep = con.prepareStatement(query);
			for (UserGroup ug : event.getParticipants()){
				prep.setInt(1, ug.getUserGroupID());
				prep.setInt(2, event.getEventID());
				prep.setInt(3, 0);
				prep.addBatch();
			}
			int[] updateCounts = prep.executeBatch();
			checkUpdateCounts(updateCounts);
			
			con.commit();

			prep.close();
			con.close();

			return event;
		} catch(SQLException e){
			System.out.println(e);
			return null;
		}
	}
	
	public static Event createGroupEvent(Event event){
		Connection con = null;
		PreparedStatement prep;
		try{
			con = DBConnect.getConnection();
			con.setAutoCommit(false);

			String query = "INSERT INTO `Event`(`EventName`, `EventNote`, `From`, `To`) VALUES (?, ?, ?, ?);";
			prep = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			prep.setString(1, event.getName());
			prep.setString(2, event.getNote());
			prep.setString(3, event.getFrom().toString());
			prep.setString(4, event.getTo().toString());
			prep.executeUpdate();
			ResultSet keys = prep.getGeneratedKeys();
			keys.next();
			int key = keys.getInt(1);
			event.setEventID(key);

			query = "INSERT INTO CalendarEvent(CalendarID, EventID) SELECT ?, LAST_INSERT_ID();";
			prep = con.prepareStatement(query);
			prep.setInt(1, event.getCal().getCalendarID());
			prep.executeUpdate();
			
			query = "INSERT INTO Attends(UserGroupID, EventID, Attends) VALUES (?,?,?);";
			prep = con.prepareStatement(query);
			for (UserGroup ug : event.getParticipants()){
				prep.setInt(1, ug.getUserGroupID());
				prep.setInt(2, event.getEventID());
				prep.setInt(3, 1);
				prep.addBatch();
			}
			int[] updateCounts = prep.executeBatch();
			checkUpdateCounts(updateCounts);
			
			Notification notification = new Notification(0, event.getName() + " created in " + event.getCal().getName(), new UserGroup(1, "System", null, 0), event.getParticipants(), event, 0);
			query = "INSERT INTO Notification(EventID, Note, UserGroupID, IsInvite) VALUES (?,?,?,?);";
			prep = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			prep.setInt(1, notification.getEvent().getEventID());
			prep.setString(2, notification.getNote());
			prep.setInt(3, notification.getSender().getUserGroupID());
			prep.setInt(4, notification.isInvite());
			prep.executeUpdate();
			ResultSet keys1 = prep.getGeneratedKeys();
			keys1.next();
			int noteKey = keys1.getInt(1);
			notification.setNoteID(noteKey);
	        
			query = "INSERT INTO HasRead(UserGroupID, NoteID, HasRead) VALUES (?,?,?);";
			prep = con.prepareStatement(query);
			for (UserGroup user : event.getParticipants()){
				prep.setInt(1, user.getUserGroupID());
				prep.setInt(2, noteKey);
				prep.setInt(3, 0);
				prep.addBatch();
			}
			int[] updateCounts2 = prep.executeBatch();
			checkUpdateCounts(updateCounts2);
			con.commit();
			prep.close();
			con.close();

			return event;
		} catch(SQLException e){
			System.out.println(e);
			return null;
		}
	}
	
	
	/**
	 * Delete an event.
	 * @param event
	 */
	public static void deleteEvent(Event event){
		Connection con = null;
		PreparedStatement prep;
		ArrayList<UserGroup> ugs = new ArrayList<UserGroup>();
		try{
			con = DBConnect.getConnection();
			con.setAutoCommit(false);
			String query = "SELECT * FROM Attends WHERE EventID = ? AND (Attends = ? OR Attends = ?)";
			prep = con.prepareStatement(query);
			prep.setInt(1, event.getEventID());
			prep.setInt(2, 1);
			prep.setInt(3, 0);
			ResultSet result = prep.executeQuery();
			while (result.next()) {
				int userGroupID = result.getInt("UserGroupID");
				ugs.add(new UserGroup(userGroupID, null, null, 1));
			}
			result.close();
			
			System.out.println("Deleting event...");
			System.out.println("Deleting attendants...");
			query = "DELETE FROM Attends WHERE EventID = ?";
			prep = con.prepareStatement(query);
			prep.setInt(1, event.getEventID());
			System.out.println(prep.toString());
			prep.execute();
			
			System.out.println("Deleting the event from calendar...");
			query = "DELETE FROM Event WHERE EventID = ?";
			prep = con.prepareStatement(query);
			prep.setInt(1, event.getEventID());
			System.out.println(prep.toString());
			prep.execute();
			
			System.out.println("Deleting Notifications...");
			query = "DELETE FROM Notification WHERE EventID = ? ;";
			prep = con.prepareStatement(query);
			prep.setInt(1, event.getEventID());
			System.out.println(prep);
			prep.execute();
			
			System.out.println("Inserting into Notification...");
			query = "INSERT INTO Notification(EventID, Note, UserGroupID, IsInvite) VALUES (?,?,?,?);";
			prep = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			prep.setInt(1, 1);
			prep.setString(2, "Event deleted: " + event.getName());
			prep.setInt(3, 1);
			prep.setInt(4, 0);
			prep.execute();
			ResultSet keys = prep.getGeneratedKeys();
			keys.next();
			int generated_note_id = keys.getInt(1);

			System.out.println("NoteID: " + generated_note_id);

			System.out.println("Inserting into HasRead....");
			query = "INSERT INTO HasRead(UserGroupID, NoteID, HasRead) VALUES (?,?,?);";
			prep = con.prepareStatement(query);
			for (UserGroup ug : ugs) {
				prep.setInt(1, ug.getUserGroupID());
				prep.setInt(2, generated_note_id);
				prep.setInt(3, 0);
				prep.addBatch();
			}
			prep.executeBatch();
			System.out.println("Complete");
			

			con.commit();
			prep.close();
			con.close();
		} catch(SQLException e){
			System.out.println(e);
		}
	}

	/**
	 * Update attendants.
	 * @param event
	 */
	public static void updateAttends(Event event, Attendant attendant){

		/* status: 0 = no response, 1 = Attends, 2 = Not attending*/
		Connection con = null;
		PreparedStatement prep;
		try{
			con = DBConnect.getConnection();
			String query = "UPDATE `Attends` "
					+ "SET `Attends` = ? "
					+ "WHERE `EventID` = ? AND `UserGroupID` = ?";
			prep = con.prepareStatement(query);
			prep.setInt(1, attendant.getStatus());
			prep.setInt(2, event.getEventID());
			prep.setInt(3, attendant.getUserGroupID());
			System.out.println(prep.toString());
			prep.execute();
			UserGroup ug = new UserGroup(attendant.getUserGroupID(), attendant.getName(), null, 1);
			ArrayList<Calendar> cals = CalendarQueries.getCalendars(ug);
			int calID = -1;
			for (Calendar cal : cals){
				if (cal.getName().equals(attendant.getName())){
					calID = cal.getCalendarID();
					break;
				}
			}
			if (calID != -1){
				query = "INSERT INTO `CalendarEvent`(`CalendarID`, `EventID`) SELECT DISTINCT ?,? FROM Calendar "
						+ "WHERE NOT EXISTS (SELECT * FROM UserCalendar JOIN CalendarEvent ON UserCalendar.CalendarID = CalendarEvent.CalendarID WHERE CalendarEvent.EventID = ? AND UserCalendar.UserGroupID = ?) ;";
				prep = con.prepareStatement(query);
				prep.setInt(1, calID);
				prep.setInt(2, event.getEventID());
				prep.setInt(3, event.getEventID());
				prep.setInt(4, attendant.getUserGroupID());
				prep.execute();
			}
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
	public static void setAttends(Event event, ArrayList<UserGroup> attendant){
		/* status: 0 = no response, 1 = Attends, 2 = Not attending*/
		Connection con = null;
		PreparedStatement prep;
		try{
			con = DBConnect.getConnection();
			con.setAutoCommit(false);
			String query = "INSERT INTO Attends(UserGroupID, EventID, Attends) VALUES (?,?,?);";
			prep = con.prepareStatement(query);
			for (UserGroup ug : attendant){
				prep.setInt(1, ug.getUserGroupID());
				prep.setInt(2, event.getEventID());
				prep.setInt(3, 0);
				prep.addBatch();
			}
			int[] updateCounts = prep.executeBatch();
			checkUpdateCounts(updateCounts);
			con.commit();
			prep.close();
			con.close();
		} catch(SQLException e){
			System.out.println(e);
		}
	}

	public static ArrayList<Attendant> getAttendants (Event event){

		ArrayList<Attendant> attends = new ArrayList<Attendant>();
		Connection con = null;
		PreparedStatement prep;
		try{
			con = DBConnect.getConnection();
			String query = "SELECT * FROM Attends NATURAL JOIN UserGroup WHERE EventID = ? ;";
			prep = con.prepareStatement(query);
			prep.setInt(1, event.getEventID());
			ResultSet rs = prep.executeQuery();
			while (rs.next()) {
				int userGroupID = rs.getInt("UserGroupID");
				String name = rs.getString("GroupName");
				int attends_status = rs.getInt("Attends");
				attends.add(new Attendant(userGroupID, name, attends_status));
			}
			rs.close();
			prep.close();
			con.close();
		} catch(SQLException e){
			System.out.println(e);
		}
		return attends;
	}

	/**
	 * Edit an event that already exists. New participants will get an invite.
	 * Exsisting participants will get a notification
	 *
	 * @param event
	 * @param sender
	 */
	public static void editEvent(Event event, UserGroup sender) {
		Connection con = null;
		PreparedStatement prep;
		try {
			con = DBConnect.getConnection();
			con.setAutoCommit(false);
			System.out.println("------ Edit Event ------");
			ArrayList<UserGroup> participants = event.getParticipants();
			ArrayList<UserGroup> participants2 = new ArrayList<>();
			for (UserGroup u : participants) {
				participants2.add(u);
			}
			System.out.println("Participants: " + participants2);

			//Query for å oppdatere eventet
			String query = "UPDATE `Event` "
					+ "SET `EventName` = ?, "
					+ "`EventNote` = ?, "
					+ "`From` = ?, "
					+ "`To` = ? "
					+ "WHERE `EventID` = ?";
			prep = con.prepareStatement(query);
			prep.setString(1, event.getName());
			prep.setString(2, event.getNote());
			prep.setString(3, event.getFrom().toString());
			prep.setString(4, event.getTo().toString());
			prep.setInt(5, event.getEventID());
			prep.executeUpdate();

			System.out.println("Event was updated successfully");

			//Finner nye deltakere
			ArrayList<UserGroup> new_participants = new ArrayList<>();
			ArrayList<UserGroup> temp_new_participants = event.getParticipants();
			ArrayList<Attendant> attendants = getAttendants(event);
			for (UserGroup u : temp_new_participants) {
				for (int x = 0; x < attendants.size(); x++) {
					if (u.getUserGroupID() == attendants.get(x).getUserGroupID()) {
						new_participants.add(u);
					}
				}
			}
			for (int i = 0; i < new_participants.size(); i++) {
				temp_new_participants.remove(new_participants.get(i));
			}
			new_participants = temp_new_participants;

			if (new_participants.size() > 0) {
				//Queries for å legge til nye deltakere
				System.out.println("---- Adding new participants ----");
				System.out.println("New Participants: " + new_participants);
				System.out.println("Inserting into Attends...");
				query = "INSERT INTO Attends(UserGroupID, EventID, Attends) VALUES (?,?,?);";
				prep = con.prepareStatement(query);
				for (UserGroup ug : new_participants) {
					prep.setInt(1, ug.getUserGroupID());
					prep.setInt(2, event.getEventID());
					prep.setInt(3, 0);
					prep.addBatch();
				}
				prep.executeBatch();
				System.out.println("Complete");

				System.out.println("Inserting into Notification...");
				query = "INSERT INTO Notification(EventID, Note, UserGroupID, IsInvite) VALUES (?,?,?,?);";
				prep = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				prep.setInt(1, event.getEventID());
				prep.setString(2, "Invite to: " + event.getName());
				prep.setInt(3, sender.getUserGroupID());
				prep.setInt(4, 1);
				prep.addBatch();
				prep.executeBatch();
				ResultSet keys = prep.getGeneratedKeys();
				keys.next();
				int generated_note_id = keys.getInt(1);

				System.out.println("NoteID: " + generated_note_id);
				System.out.println("Complete");

				System.out.println("Inserting into HasRead");
				query = "INSERT INTO HasRead(UserGroupID, NoteID, HasRead) VALUES (?,?,?);";
				prep = con.prepareStatement(query);
				for (UserGroup ug : new_participants) {
					prep.setInt(1, ug.getUserGroupID());
					prep.setInt(2, generated_note_id);
					prep.setInt(3, 0);
					prep.addBatch();
				}
				prep.executeBatch();
				System.out.println("Complete");
			}

			System.out.println("---- Removing participants ----");
			//Finner deltakere som er fjernet
			ArrayList<Integer> deleted_participants_id = new ArrayList<>();
			ArrayList<Integer> old_participants_id = new ArrayList<>();
			ArrayList<Integer> new_participants_id = new ArrayList<>();

			for (Attendant a : attendants) old_participants_id.add(a.getUserGroupID());
			System.out.println("Participants before edit: " + old_participants_id);
			for (UserGroup u : participants2) new_participants_id.add(u.getUserGroupID());
			System.out.println("Participants after edit: " + new_participants_id);
			for (int i = 0; i < old_participants_id.size(); i++) {
				if (!new_participants_id.contains(old_participants_id.get(i))) {
					deleted_participants_id.add(old_participants_id.get(i));
				}
			}
			if (deleted_participants_id.size() > 0) {
				System.out.println("Participants to be deleted: " + deleted_participants_id);

				//Queries for å fjerne deltakere
				System.out.println("Deleting from Attends....");
				query = "DELETE FROM Attends WHERE UserGroupID = ? AND EventID = ?";
				prep = con.prepareStatement(query);
				for (Integer i : deleted_participants_id) {
					prep.setInt(1, i);
					prep.setInt(2, event.getEventID());
					prep.addBatch();
				}
				prep.executeBatch();
				System.out.println("Deleted ids: " + deleted_participants_id);
				System.out.println("Complete");

				System.out.println("Finding NoteIDs....");
				ArrayList<Integer> deleted_noteID = new ArrayList<>();
				query = "SELECT NoteID FROM Notification WHERE EventID = ?";
				prep = con.prepareStatement(query);
				prep.setInt(1, event.getEventID());
				ResultSet r = prep.executeQuery();
				while (r.next()) {
					int v = r.getInt("NoteID");
					System.out.println("ID: " + v);
					deleted_noteID.add(v);
				}
				System.out.println(deleted_noteID);
				System.out.println("Complete");

				System.out.println("Deleting from HasRead....");
				query = "DELETE FROM HasRead WHERE NoteID = ? AND UserGroupID = ?";
				prep = con.prepareStatement(query);
				for (int i = 0; i < deleted_noteID.size(); i++) {
					for (Integer v : deleted_participants_id) {
						prep.setInt(1, deleted_noteID.get(i));
						prep.setInt(2, v);
						prep.addBatch();
					}
				}
				prep.executeBatch();
				
				System.out.println("Inserting into Notification...");
				query = "INSERT INTO Notification(EventID, Note, UserGroupID, IsInvite) VALUES (?,?,?,?);";
				prep = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				prep.setInt(1, event.getEventID());
				prep.setString(2, "You have been removed from the event: " + event.getName());
				prep.setInt(3, sender.getUserGroupID());
				prep.setInt(4, 0);
				prep.addBatch();
				prep.executeBatch();
				ResultSet keys = prep.getGeneratedKeys();
				keys.next();
				int generated_note_id = keys.getInt(1);

				System.out.println("NoteID: " + generated_note_id);

				System.out.println("Inserting into HasRead....");
				query = "INSERT INTO HasRead(UserGroupID, NoteID, HasRead) VALUES (?,?,?);";
				prep = con.prepareStatement(query);
				for (Integer i : deleted_participants_id) {
					prep.setInt(1, i);
					prep.setInt(2, generated_note_id);
					prep.setInt(3, 0);
					prep.addBatch();
				}
				prep.executeBatch();
				System.out.println("Complete");
			}
			System.out.println("--- Notification to exsisting users ---");
			System.out.println("Finding exsisting users....");
			ArrayList<UserGroup> exsisting_participants = participants2;
			for (int i = 0; i < new_participants.size(); i++) {
				if (exsisting_participants.contains(new_participants.get(i))) {
					exsisting_participants.remove(new_participants.get(i));
				}
			}
			System.out.println(exsisting_participants);
			if (exsisting_participants.size() > 0) {
				System.out.println("Inserting into Notification...");
				query = "INSERT INTO Notification(EventID, Note, UserGroupID, IsInvite) VALUES (?,?,?,?);";
				prep = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				prep.setInt(1, event.getEventID());
				prep.setString(2, event.getName() + " was updated");
				prep.setInt(3, sender.getUserGroupID());
				prep.setInt(4, 0);
				prep.addBatch();
				prep.executeBatch();
				ResultSet keys = prep.getGeneratedKeys();
				keys.next();
				int generated_note_id = keys.getInt(1);

				System.out.println("NoteID: " + generated_note_id);
				System.out.println("Complete");

				System.out.println("Inserting into HasRead....");
				query = "INSERT INTO HasRead(UserGroupID, NoteID, HasRead) VALUES (?,?,?);";
				prep = con.prepareStatement(query);
				for (UserGroup ug : exsisting_participants) {
					prep.setInt(1, ug.getUserGroupID());
					prep.setInt(2, generated_note_id);
					prep.setInt(3, 0);
					prep.addBatch();
				}
				prep.executeBatch();
				System.out.println("Complete");
			}
			con.commit();
			prep.close();
			con.close();
		} catch (SQLException e1) {
			System.out.println(e1.toString());
		}

	}
}

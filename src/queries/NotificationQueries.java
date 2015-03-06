package queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import models.Calendar;
import models.Event;
import models.Notification;
import models.Person;
import models.UserGroup;
import database.DBConnect;

public class NotificationQueries {
	
	
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
* Returns a list of Notification that belongs to the given param person.
* @param person
* @return ArrayList<Notification> - liste over alle notifikasjoner for person-parameteren.
* @throws SQLException
*/
public static ArrayList<Notification> getNotifications(Person person){
	ArrayList<Notification> notifications = new ArrayList<Notification>();
	int personID = person.getPersonID();
	Connection con = null;
	PreparedStatement prep = null;
	ResultSet rs;
	try{
		con = DBConnect.getConnection();
		String query = "SELECT *\n" 
		+ "FROM Notification\n"
		+"NATURAL JOIN UserGroup\n"
		+"NATURAL JOIN Person\n"
		+"NATURAL JOIN HasRead\n"
		+"NATURAL JOIN Event\n"
		+"WHERE PersonID = ? AND HasRead = 0\n";
		prep = con.prepareStatement(query);
		prep.setInt(1, personID);
		rs = prep.executeQuery();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
		while(rs.next()){
			System.out.println(rs.getTimestamp("To").toString());
			System.out.println(rs.getTimestamp("From").toString());
			notifications.add(new Notification(rs.getInt("NoteID"), rs.getString("Note"), 
			new UserGroup(rs.getInt("UserGroupID"), rs.getString("GroupName"), null), 
			null, new Event(rs.getInt("EventID"), rs.getString("EventName"), rs.getString("EventNote"), null, 
			LocalDateTime.parse(rs.getTimestamp("From").toString(), formatter), LocalDateTime.parse(rs.getTimestamp("To").toString(), formatter), null), rs.getInt("IsInvite")));
		}
		rs.close();
		prep.close();
		con.close();
		return notifications;

	}
	
	catch(SQLException e){
			System.out.println(e);
			return null;
	}
	}

/**
 *Sets notification in database. The 'ug' parameter specifies which user is the sender of the notification.
 * @param notification
 * @param event
 * @param ug
 */
public static void setNotification(Notification notification){
	Connection con = null;
	PreparedStatement prep;
	try{
		con = DBConnect.getConnection();
		con.setAutoCommit(false);
		String query = "INSERT INTO Notification(EventID, Note, UserGroupID) VALUES (?,?,?);";
		prep = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		prep.setInt(1, notification.getEvent().getEventID());
		prep.setString(2, notification.getNote());
		prep.setInt(3, notification.getSender().getUserGroupID());
		prep.executeUpdate();
		ResultSet keys = prep.getGeneratedKeys();
		keys.next();
		int noteKey = keys.getInt(1);
		notification.setNoteID(noteKey);
		
		query = "SELECT PersonID FROM Attends WHERE EventID = ?;";
		prep = con.prepareStatement(query);
		prep.setInt(1, notification.getEvent().getEventID());
		ResultSet result = prep.executeQuery();
		ArrayList<Integer> personKeys = new ArrayList<Integer>();
        while (result.next()) {
            personKeys.add(result.getInt("PersonID"));
        }
        result.close();
        
		query = "INSERT INTO HasRead(PersonID, NoteID, HasRead) VALUES (?,?,?);";
		prep = con.prepareStatement(query);
		for (int personID : personKeys){
			prep.setInt(1, personID);
			prep.setInt(2, noteKey);
			prep.setInt(3, 0);
			prep.addBatch();
		}
		int[] updateCounts = prep.executeBatch();
		checkUpdateCounts(updateCounts);
		con.commit();
	} catch(SQLException e){
		System.out.println(e);
	}
}

/**
 * Set HasRead column i HasRead table to 1, where PersonID matches the given person.PersonID and notification.NoteID
 * @param notification
 * @param person
 */


public static void setRead(Notification notification, Person person){
	int noteID = notification.getNoteID();
	int personID = person.getPersonID();
	Connection con = null;
	PreparedStatement prep;
	try{
		con = DBConnect.getConnection();
		String query = "UPDATE HasRead \n SET HasRead = 1\n WHERE PersonID = ? AND NoteID = ?";
		prep = con.prepareStatement(query);
		prep.setInt(1, personID);
		prep.setInt(2, noteID);
		prep.executeUpdate();
		prep.close();
		con.close();
	} catch(SQLException e){
		System.out.println(e);
		System.out.println("Failed to set HasRead=1");
	}
	}
	
//	public static void main(String[] args){
//	Person p = new Person(1, "Sondre", "D", "Sondre");
//	Notification n = new Notification(2, null, null, null, null);
////	NotificationQueries.setRead(n, p);
//	System.out.println("+" + NotificationQueries.getNotifications(p));
//	}
/*
	public static void main(String[] args) {
		Event e = new Event(2, "Spise kake", null, null, null, new Calendar(2, "Yolo", null));
		UserGroup ug = new UserGroup(1,"Sondre Hjetland", null);
		Notification n = new Notification(0, "Husk å slå ned noen.", ug, null, e);
		setNotification(n);
	}*/
}
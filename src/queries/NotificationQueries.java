package queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import models.Event;
import models.Notification;
import models.Person;
import models.UserGroup;
import database.DBConnect;

public class NotificationQueries {
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
			null, new Event(rs.getInt("EventID"), rs.getString("EventName"), null, 
			LocalDateTime.parse(rs.getTimestamp("From").toString(), formatter), LocalDateTime.parse(rs.getTimestamp("To").toString(), formatter), null)));
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
	
}
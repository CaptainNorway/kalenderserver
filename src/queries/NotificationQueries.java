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
* 
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
		String query = "SELECT *" 
		+ "FROM Notification"
		+"NATURAL JOIN UserGroup"
		+"NATURAL JOIN Person"
		+"NATURAL JOIN HasRead"
		+"NATURAL JOIN Event"
		+"WHERE PersonID = ? AND HasRead = 0";
		prep.setInt(1, personID);
		prep = con.prepareStatement(query);
		rs = prep.executeQuery();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-mm-dd hh:mm:ss.fffffffff");
		while(rs.next()){
		notifications.add(new Notification(rs.getInt("NoteID"), rs.getString("Note"), 
		new UserGroup(rs.getInt("UserGroupID"), rs.getString("GroupName"), null), 
		null, new Event(rs.getInt("EventID"), rs.getString("EventName"), null, 
		LocalDateTime.parse(rs.getTimestamp("From").toString(), formatter), LocalDateTime.parse(rs.getTimestamp("To").toString(), formatter), null)));
		return notifications;
	}
	
		catch(SQLException e){
			System.out.println(e);
			return null;
	}
	}
	
public static void setRead(Notification notification, Person person){
	int noteID = notification.getNoteID();
	int personID = person.getPersonID();
	Connection con = null;
	PreparedStatement prep;
	try{
	con = DBConnect.getConnection();
	String query = "UPDATE HasRead SET HasRead = 1 WHERE PersonID = ? AND NoteID = ?";
	prep = con.prepareStatement(query);
	prep.setInt(1, noteID);
	prep.setInt(2, personID);
	} catch(SQLException e){
	System.out.println(e);
	}
	}
	
	public static void main(String[] args){
	Person p = new Person("Sondre", null);
	Notification n = new Notification(2, null, null, null, null);
	NotificationQueries.setRead(n, p);
	}
	
}
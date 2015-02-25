package queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import models.Notification;
import models.Person;
import database.DBConnect;

public class NotificationQueries {
	
	public static ArrayList<Notification> getNotifications(Person person) throws SQLException {
		ArrayList<Notification> notifications = new ArrayList<Notification>();
		int peronID = person.getPersonID();
		Connection con = null;
		PreparedStatement prep = null;
		
		try{
			con = DBConnect.getConnection();
			String query = "SELECT * N"
			
			
			
		}
		
	}
	
	public static void setRead(Notification notification, Person person){
		int noteID = notification.getNotID()
	}
	

}

package queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import database.DBConnect;
import models.Calendar;
import models.Person;
import models.UserGroup;

public class UserGroupQueries {

	// getUserGroup(Calendar)
	private static void checkUpdateCounts(int[] updateCounts) {
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

	public static void createEmptyUserGroup(UserGroup users){
		Connection con = null;
		PreparedStatement prep;
		try{
			con = DBConnect.getConnection();
			String query = "INSERT INTO UserGroup(Name) VALUES(?)";
			prep = con.prepareStatement(query);
			prep.setString(1, users.getName());
			prep.execute();
		} catch(SQLException e){
			System.out.println(e);
		}
	}

	public static void addUsers(UserGroup users){
		Connection con = null;
		PreparedStatement prep;
		try{
			con = DBConnect.getConnection();
			con.setAutoCommit(false);
			String query = "INSERT INTO PersonUserGroup(PersonID,UserGroupID) VALUES(?,?)";
			prep = con.prepareStatement(query);
			for(Person person : users.getUsers()){
				prep.setInt(0, person.getPersonID());
				prep.setInt(1, users.getUserGroupID());
				prep.addBatch();
			}
			int[] updateCounts = prep.executeBatch();
			checkUpdateCounts(updateCounts);
			con.commit();
		} catch(SQLException e){
			System.out.println(e);
		}
	}

	public static ArrayList<UserGroup> getUserGroups(Calendar cal){
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs;
		ArrayList<UserGroup> userGroups = new ArrayList<UserGroup>();
		try{
			con = DBConnect.getConnection();
			String query = "SELECT UserGroup.UserGroupID , UserGroup.Name "
					+ "FROM UserGroup NATURAL JOIN UserCalendar NATURAL JOIN Calendar "
					+ "WHERE Calendar.Name = ?";
			prep = con.prepareStatement(query);
			prep.setString(1, cal.getName());
			rs = prep.executeQuery();
			while(rs.next()){
				userGroups.add(new UserGroup(rs.getInt("UserGroupID"),rs.getString("Name"),null));
			}
			return userGroups;
		}
		catch( SQLException e){
			System.out.println(e);
			return null;
		}

	}

	public static ArrayList<Person> getPersons(ArrayList<UserGroup> usersList){
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs;
		ArrayList<Person> persons = new ArrayList<Person>();
		try{
			con = DBConnect.getConnection();
			String query = "SELECT UNIQUE Person.PersonID, Person.Name, Persons.UserName "
					+ "FROM UserGroup NATURAL JOIN PersonUserGroup NATURAL JOIN Person "
					+ "WHERE ";
			for(int i=0; i<usersList.size();i++){
				if(i!=0 && i!=usersList.size()-1){
					query += " OR ";
				}
				query += "UserGroup.UserGroupID = ?";
			}
			prep = con.prepareStatement(query);
			for(int i=0;i<usersList.size();i++){
				prep.setInt(i+1, usersList.get(i).getUserGroupID());
			}
			rs = prep.executeQuery();
			while(rs.next()){
				persons.add(new Person(username, password))
			}
			return persons;
		}
		catch( SQLException e){
			System.out.println(e);
			return null;
		}
	}

	public static void main(String[] args) {
		UserGroup ug = new UserGroup(0, "SuperKalender", null);
		createEmptyUserGroup(ug);
	}
}

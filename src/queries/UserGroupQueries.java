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
	/**
	 * Creates an empty UserGroup with the given name
	 * @param users
	 */
	public static void createEmptyUserGroup(String groupName){
		Connection con = null;
		PreparedStatement prep;
		try{
			con = DBConnect.getConnection();
			String query = "INSERT INTO UserGroup(GroupName) VALUES(?)";				
			prep = con.prepareStatement(query);
			prep.setString(1, groupName);
			prep.execute();
		} catch(SQLException e){
			System.out.println(e);
		}
	}
	/**
	 * Set all persons in given UserGroup object to the UserGroup spesified in the objet
	 * @param users
	 */
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
	
	/**
	 * Get all UserGroups that spesified calendars, checks calendarID (Given calendars must have a valid ID)
	 * @param cal
	 * @return
	 */
	public static ArrayList<UserGroup> getUserGroups(ArrayList<Calendar> cals){
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs;
		ArrayList<UserGroup> userGroups = new ArrayList<UserGroup>();
		try{
			con = DBConnect.getConnection();
			String query = "SELECT DISTINCT UserGroup.UserGroupID , UserGroup.GroupName "
					+ "FROM UserGroup NATURAL JOIN UserCalendar NATURAL JOIN Calendar "
					+ "WHERE ";
			for(int i = 0; i<cals.size();i++){
				if(i!=0){
					query += " OR ";
				}
				query += " Calendar.CalendarID = ? ";
			}
			prep = con.prepareStatement(query);
			for(int i = 0; i<cals.size(); i++){
				prep.setInt(i+1, cals.get(i).getCalendarID());
			}
			rs = prep.executeQuery();
			while(rs.next()){
				userGroups.add(new UserGroup(rs.getInt("UserGroupID"),rs.getString("GroupName"),null));
			}
			return userGroups;
		}
		catch( SQLException e){
			System.out.println(e);
			return null;
		}
			
	}
	/**
	 * Get all distinct persons in given UserGroup, checks spesified UserGroupID
	 * @param usersList
	 * @return
	 */
	public static ArrayList<Person> getPersons(ArrayList<UserGroup> usersList){
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs;
		ArrayList<Person> persons = new ArrayList<Person>();
		if(usersList == null || usersList.size() == 0){
			throw new IllegalArgumentException("Cannot use empty UserGroup-list");
		}
		try{
			con = DBConnect.getConnection();
			String query = "SELECT DISTINCT Person.PersonID, Person.Name, Person.Username "
					+ "FROM UserGroup NATURAL JOIN PersonUserGroup NATURAL JOIN Person "
					+ "WHERE ";
			for(int i=0; i<usersList.size();i++){
				if(i!=0){
					query += " OR ";
				}
				query += "UserGroup.UserGroupID = ?";
			}
			prep = con.prepareStatement(query);
			for(int i=0;i<usersList.size();i++){
				prep.setInt(i+1, usersList.get(i).getUserGroupID());
			}
			System.out.println(prep.toString());
			rs = prep.executeQuery();
			while(rs.next()){
				System.out.println("PersonID: " + rs.getInt(1) + " Name : " + rs.getString(2) 
						+ " Username : " + rs.getString(3));
				//persons.add(new Person(rs.getString(2), null));
			}
			return persons;
		}
		catch( SQLException e){
			System.out.println(e);
			return null;
		}
	}
	
	/**
	 * Deletes all UserGroups in an array , objects must have valid UserGroupID
	 * @param userGroupList
	 */
	public static void deleteUserGroups(ArrayList<UserGroup> userGroupList){
		Connection con = DBConnect.getConnection();
		PreparedStatement prep;
		if(userGroupList == null || userGroupList.size() ==0){
			throw new IllegalArgumentException("Cannot delete using empty list");
		}
		try{
			String query = "DELETE FROM UserGroup "
					+ "WHERE ";
			for(int i = 0; i<userGroupList.size();i++ ){
				if(i!=0){
					query += " OR ";
				}
				query += " UserGroupID = ? ";
			}
			prep = con.prepareStatement(query);
			for(int i = 0; i<userGroupList.size();i++){
				prep.setInt(i+1, userGroupList.get(i).getUserGroupID());
			}
			System.out.println(prep.toString());
			prep.execute();
		}
		catch(SQLException e){
			System.out.println(e);
		}
	}
    /**
    * Get all UserGroup(s) spesified person is in, including persons which are in the groups
    * Person objects are if they appear in different groups, but will be return true on equal 
    * because they have the same PersonID
    * @param person
    * @return
    */
	public static ArrayList<UserGroup> getUserGroups(Person person){
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs;
		ArrayList<UserGroup> userGroups = new ArrayList<UserGroup>();
		try{
			con = DBConnect.getConnection();
			String query = "SELECT UserGroupID, PersonID, Username, Name, GroupName "
					+ "FROM UserGroup NATURAL JOIN PersonUserGroup NATURAL JOIN Person "
					+ "WHERE EXISTS ( "
					+ "SELECT UserGroupID "
					+ "FROM PersonUserGroup AS G "
					+ "WHERE PersonID = ? AND G.UserGroupID = UserGroup.UserGroupID )";
			prep = con.prepareStatement(query);
			prep.setInt(1, person.getPersonID());
			rs = prep.executeQuery();
			// Denne klassen brukes slik at contains og indexOf skal returnere positivt 
			// om id-er lik selvom det faktisk ikke er samme objekt
			class Wrapper{
				public int number;
				Wrapper(Integer input){
					number=input;
				}
				@Override
				public boolean equals(Object o){
					return number == ((Wrapper) o).number;
				}
			}
			ArrayList<Wrapper> userGroupIDs = new ArrayList<>(); 
			while(rs.next()){
				int userGroupID = rs.getInt("UserGroupID");
				Wrapper userGroupIndex = new Wrapper(-1);
				
				if(!userGroupIDs.contains(new Wrapper(userGroupID))){
					//Gruppe finnes ikke, legg til i liste
					userGroupIDs.add(new Wrapper(userGroupID));
					ArrayList<Person> persons = new ArrayList<Person>();
					UserGroup userGroup = new UserGroup(userGroupID, rs.getString("GroupName"), persons);
					userGroups.add(userGroup);
					userGroupIndex.number = userGroups.size()-1;
				}
				else{
					//Gruppe finnes fra f�r , hent referanse
					userGroupIndex.number = userGroupIDs.indexOf(new Wrapper(userGroupID));
				}
				
				Person dbPerson = new Person(rs.getString("Username"), rs.getString("Name"), rs.getInt("PersonID"));
				userGroups.get(userGroupIndex.number).addUser(dbPerson);
				
			}
			return userGroups;
		}
		catch( SQLException e){
			System.out.println(e);
			return null;
		}
	}

    /**
     HER NEDENFOR ER TO FUNKSJONER SOM FUNKER MED HVERANDRE, de er svært like to funksjoner som er over disse.
     Fint om alle som leser dette følger syntaksen nedenfor, altså, utfyllende navn a la: PreparedStatement preparedStatement
     ResultSet resultSet, osv ... husk å close alle ting, statements, resultsets og connections osv. 
     */

//    /**
//     * Get all distinct persons in given UserGroup, checks specified UserGroupID
//     * @param UserGroupID
//     * @return ArrayList<Person>
//     */
//    public static ArrayList<Person> getPersonsInUserGroup(int UserGroupID) {
//        ArrayList<Person> users = new ArrayList<>(); // users/persons/members
//        try{
//            Connection con = DBConnect.getConnection();
//            String sqlQuery = "SELECT DISTINCT Person.PersonID, Person.Username, Person.Password, Person.Name "
//                    + "FROM UserGroup NATURAL JOIN PersonUserGroup NATURAL JOIN Person "
//                    + "WHERE UserGroupID = " + UserGroupID + "";
//
//            PreparedStatement preparedStatement = con.prepareStatement(sqlQuery);
//            ResultSet resultSet = preparedStatement.executeQuery();
//
//            while(resultSet.next()) {
//                int PersonID = resultSet.getInt("PersonID");
//                String Username = resultSet.getString("Username");
//                String Password = resultSet.getString("Password");
//                String Name = resultSet.getString("Name");
//                users.add(new Person(PersonID, Username, null, Name));
//            }
//            resultSet.close();
//            preparedStatement.close();
//            con.close();
//            return users;
//        }
//        catch(SQLException e){
//            throw new IllegalArgumentException(e);
//        }
//    }
//
//    /**
//     * Returns all UserGroup(s) a specified person belongs to.
//     * @param person
//     * @return ArrayList<UserGroup>
//     */
//    public static ArrayList<UserGroup> getUserGroups(Person person){
//        ArrayList<UserGroup> userGroups = new ArrayList<>();
//        try{
//            Connection con = DBConnect.getConnection();
//            String sqlQuery = "SELECT UserGroup.UserGroupID , UserGroup.GroupName "
//                    + "FROM UserGroup NATURAL JOIN PersonUserGroup NATURAL JOIN Person "
//                    + "WHERE PersonID = " + person.getPersonID() + "";
//
//            PreparedStatement preparedStatement = con.prepareStatement(sqlQuery);
//            ResultSet resultSet = preparedStatement.executeQuery();
//
//            while(resultSet.next()){
//                int UserGroupID = resultSet.getInt("UserGroupID");
//                String GroupName = resultSet.getString("GroupName");
//                userGroups.add(new UserGroup(UserGroupID, GroupName, getPersonsInUserGroup(UserGroupID)));
//            }
//            resultSet.close();
//            preparedStatement.close();
//            con.close();
//            return userGroups;
//        }
//        catch( SQLException e){
//            throw new IllegalArgumentException(e);
//        }
//    }
	
	public static void main(String[] args) {
		//UserGroup ug = new UserGroup(0, "SuperKalender", null);
		//createEmptyUserGroup(ug);
		
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
		
//		Person person = new Person("martin", "bla", 5);
//		ArrayList<UserGroup> userGroups = getUserGroups(person);
//		for(UserGroup ug : userGroups){
//			System.out.println("Gruppe: " + ug.getName() +"----------");
//			for(Person p : ug.getUsers()){
//				System.out.println("\t"+p.getName());
//			}
//		}
    }
}

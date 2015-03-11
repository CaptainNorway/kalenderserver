
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
import models.Calendar;
import models.Event;
import models.Person;
import models.UserGroup;

public class UserGroupQueries {

    /**
     * Checks update counts.
     * @param updateCounts
     */
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
	 * Creates an empty UserGroup with the given name.
	 * @param name
	 */
	public static UserGroup createEmptyUserGroup(String name){
		Connection con = null;
		PreparedStatement prep;
		ResultSet rs;
		try{
			con = DBConnect.getConnection();
			String query = "INSERT INTO UserGroup(GroupName) VALUES(?)";
			prep = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			prep.setString(1, name);
			prep.execute();
			rs = prep.getGeneratedKeys();
			rs.next();
			return new UserGroup(rs.getInt(1), name, null);
		} catch(SQLException e){
			e.printStackTrace();
			return null;
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
			String query = "SELECT DISTINCT Person.PersonID, Person.Name, Person.Username, Person.Flag"
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
			rs = prep.executeQuery();
			while(rs.next()){
				persons.add(new Person(rs.getInt("PersonID"), rs.getString("Username"), rs.getString("Name"), rs.getString("Flag")));
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
			String query = "SELECT UserGroupID, PersonID, Username, Name, GroupName, Flag "
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
				
				Person dbPerson = new Person(rs.getInt("PersonID"), rs.getString("Username"), rs.getString("Name"), rs.getString("Flag"));
				userGroups.get(userGroupIndex.number).addUser(dbPerson);
				
			}
			return userGroups;
		}
		catch( SQLException e){
			System.out.println(e);
			return null;
		}
	}
	
	public static void removeUsers(UserGroup userGroup){
		Connection con = null;
		PreparedStatement prep;
		try{
			
			con = DBConnect.getConnection();
			String query = "DELETE FROM `PersonUserGroup` "
					+ "WHERE 'UserGroupID` = ? AND ( ";
			for(int i=0;i<userGroup.getUsers().size(); i++){
				if(i==0){
					query += " OR ";
				}
				query += "PersonID = ? ";
			}
			prep = con.prepareStatement(query);
			prep.setInt(1, userGroup.getUserGroupID());
			
			for(int i=0; i<userGroup.getUsers().size(); i++){
				prep.setInt(i+2, userGroup.getUsers().get(i).getPersonID());
			}
			
			System.out.println(prep.toString());
			prep.execute();
			System.out.println("Executed");
			prep.close();
		    con.close();
		} catch(SQLException e){
			System.out.println(e);
		}
	}
	
	public static void editUserGroup(UserGroup userGroup){
		Connection con = null;
		PreparedStatement prep;
		try{
			con = DBConnect.getConnection();
			con.setAutoCommit(false);
			String query = "DELETE FROM PersonUserGroup "
					+ "WHERE UserGroupID = ? AND ( ";
			for(int i=0;i<userGroup.getUsers().size(); i++){
				if(i!=0){
					query += " OR ";
				}
				query += "PersonID = ? ";
			}
			query += ");";
			prep = con.prepareStatement(query);
			prep.setInt(1, userGroup.getUserGroupID());
			
			for(int i=0; i<userGroup.getUsers().size(); i++){
				prep.setInt(i+2, userGroup.getUsers().get(i).getPersonID());
			}
			
			prep.execute();
			String query2 = "INSERT INTO PersonUserGroup(PersonID,UserGroupID) VALUES(?,?) ";
			prep = con.prepareStatement(query2);
			for (Person person : userGroup.getUsers()){
				prep.setInt(1, person.getPersonID());
				prep.setInt(2, userGroup.getUserGroupID());
				prep.addBatch();
			}
			
			prep.executeBatch();
			
			String query3 = "UPDATE UserGroup "
					+ "SET GroupName = ? "
					+ "WHERE UserGroupID = ? ;";
			
			prep = con.prepareStatement(query3);
			prep.setString(1, userGroup.getName());
			prep.setInt(2, userGroup.getUserGroupID());
			prep.executeUpdate();
			con.commit();
			prep.close();
		    con.close();
		} catch(SQLException e){
			System.out.println(e);
		}
	}
	
	/**
	 * Henter alle private usergroups fra databasen. Altså userGroups som er individuelle for personer.
	 * @return
	 */
	public static ArrayList<UserGroup> getPrivateUserGroups(){
		Connection con = null;
		PreparedStatement prep;
		ArrayList<UserGroup> ug = new ArrayList<>();
		try{
			con = DBConnect.getConnection();
			String query ="SELECT * FROM UserGroup WHERE Private = ?;";
			prep = con.prepareStatement(query);
			prep.setInt(1, 1);
			prep.execute();
			ResultSet result = prep.executeQuery();
            while (result.next()) {
                int userGroupID = result.getInt("UserGroupID");
                String groupName= result.getString("GroupName");
                ug.add(new UserGroup(userGroupID, groupName, null));
            }
            result.close();
            prep.close();
		    con.close();
		} catch(SQLException e){
			System.out.println(e);
		}
		return ug;
	}
	
	public static UserGroup getPersonalUserGroup(Person p){
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs;
		UserGroup userGroup = null;
		try{
			con = DBConnect.getConnection();
			String query = "SELECT * FROM UserGroup NATURAL JOIN PersonUserGroup WHERE Private = ? AND PersonID = ? ;";
			prep = con.prepareStatement(query);
			prep.setInt(1, 1);
            prep.setInt(2, p.getPersonID());

			rs = prep.executeQuery();
			System.out.println("PERSON ID : " + p.getPersonID());
			while(rs.next()){
				int ugID = rs.getInt("UserGroupID");
				String groupName = rs.getString("GroupName");
				userGroup = new UserGroup(ugID, groupName, null);
			}
			rs.close();
			prep.close();
			con.close();
		}
		catch( SQLException e){
			System.out.println(e);
			return null;
		}
		return userGroup;
	}
	
	/**
	 * Lite brukt men nødvendig 1 plass. let it be.
	 * @param ug
	 * @return
	 */
	public static UserGroup getUserGroup(UserGroup ug){
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs;
		UserGroup userGroup = null;
		try{
			con = DBConnect.getConnection();
			String query = "SELECT * FROM UserGroup WHERE UserGroupID = ? ;";
			prep = con.prepareStatement(query);
			prep.setInt(1, ug.getUserGroupID());
			rs = prep.executeQuery();
			while(rs.next()){
				int ugID = rs.getInt("UserGroupID");
				String groupName = rs.getString("GroupName");
				userGroup = new UserGroup(ugID, groupName, null);
			}
			rs.close();
			prep.close();
			con.close();
		}
		catch( SQLException e){
			System.out.println(e);
			return null;
		}
		return userGroup;
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
		/*Edit usergroup testing*/ // gruppe 8 , legg til person 4
//		ArrayList<Person> persons = new ArrayList<>();
//		Person p1 = new Person(null, null, 4);
//		Person p2 = new Person(null, null, 5);
//		persons.add(p1);persons.add(p2);
//		UserGroup ug = new UserGroup(8, "Sjefer", persons);
//		System.out.println(ug.getUsers().size());
//		editUserGroup(ug);
		
    }
}

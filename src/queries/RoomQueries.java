package queries;

import database.DBConnect;
import models.Event;
import models.Person;
import models.Room;
import models.UserGroup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import queries.EventQueries;

public class RoomQueries {

    public static ArrayList<Room> getRooms() {
        ArrayList<Room> rooms = new ArrayList<>();
        try {
            Connection con = DBConnect.getConnection();
            String sqlQuery = "SELECT * FROM Room";

            PreparedStatement preparedStatement = con.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String RoomName = resultSet.getString("RoomName");
                int Capacity = resultSet.getInt("Capacity");
                rooms.add(new Room(RoomName, Capacity));
                }
            resultSet.close();
            preparedStatement.close();
            con.close();
            return rooms;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    public static ArrayList<Room> getAvailableRooms(Event event){
    	ArrayList<Room> availableRooms = new ArrayList();
    	int eventID = event.getEventID();
    	try{
    		Connection con = DBConnect.getConnection();
    		String sqlQuery = "SELECT * "
    				+ "FROM Room AS AvailableRoom "
    				+"WHERE CAPACITY>= ? AND NOT EXISTS ("
    				+"SELECT* "
    				+"FROM Room NATURAL JOIN Meeting NATURAL JOIN Event "
    				+"WHERE Room.RoomName = AvailableRoom.RoomName AND "
    				+ "Event.EventID != ? AND NOT "
    				+"((`From` < (?) AND `To` < ?) "
    				+ "OR (`From` >= ?)) )";
    		PreparedStatement preparedStatement = con.prepareStatement(sqlQuery);
    		preparedStatement.setInt(1, event.getParticipants().size());
    		if(event.getEventID()==0){
    			preparedStatement.setInt(2,-1);
    		}
    		else{
    			preparedStatement.setInt(2,event.getEventID());
    		}
    		preparedStatement.setString(3, event.getFrom().toString());
    		preparedStatement.setString(4, event.getFrom().toString());
    		preparedStatement.setString(5, event.getTo().toString());
    		ResultSet resultSet = preparedStatement.executeQuery();
    		while(resultSet.next()){
    			String roomName = resultSet.getString("RoomName");
    			int roomCapacity = resultSet.getInt("Capacity");
    			availableRooms.add(new Room(roomName, roomCapacity));
    		}
    		resultSet.close();
    		preparedStatement.close();
    		con.close();
    		return availableRooms;
    	}
    	catch(SQLException e){
    		System.out.println(e);
    		return null;
    	}
    }
    public static void main(String[] args) {
    	LocalDateTime from = LocalDateTime.of(2015,3, 11, 20, 15);
    	LocalDateTime to= LocalDateTime.of(2015, 3, 11, 20, 15);
    	UserGroup ug = new UserGroup(1, null, null);
    	ArrayList<UserGroup> participants = new ArrayList<>();
    	participants.add(ug);
    	Event event = new Event(99, "kaffe", "Drikke 10 liter kaffe", participants, from, to, null);
    	
    	
        ArrayList<Room> rooms = getAvailableRooms(event);
        System.out.println("Printing a list of the room entries in the database table ROOM:");
    	for (Room room : rooms) {
    		System.out.println(room);
    	}
        
    	
  }
}


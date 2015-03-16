package queries;

import database.DBConnect;
import models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    
	/**
	 * Books a room in the database and delete previous bookings for the meeting if it exists.
	 * @param ev
	 * @param room
	 */
    public static void bookRoom(Event ev, Room room){
    	Connection con = null;
    	PreparedStatement prep;
    	try{
    		con = DBConnect.getConnection();
    		con.setAutoCommit(false);
    		String query = "DELETE FROM Meeting WHERE EventID = ? ;";
    		prep = con.prepareStatement(query);
    		prep.setInt(1, ev.getEventID());
    		prep.executeUpdate();
    		
    		query = "INSERT INTO Meeting(EventID, RoomName) VALUES (?,?)";
    		prep = con.prepareStatement(query);
    		prep.setInt(1, ev.getEventID());
    		prep.setString(2, room.getRoomName());
    		prep.executeUpdate();
    		con.commit();
    		prep.close();
    		con.close();
    	} catch(SQLException e){
    		System.out.println(e);
    	}
    }
    
    public static Room getEventRoom(Event ev){
    	Room room = null;
    	try{
    		Connection con = DBConnect.getConnection();
    		String sqlQuery = "SELECT * FROM Meeting NATURAL JOIN Room WHERE EventID = ? ;";
    		PreparedStatement prep = con.prepareStatement(sqlQuery);
    		prep.setInt(1, ev.getEventID());
    		ResultSet resultSet = prep.executeQuery();
    		while(resultSet.next()){
    			String roomName = resultSet.getString("RoomName");
    			int capacity = resultSet.getInt("Capacity");
    			room = new Room(roomName, capacity);
    		}
    		resultSet.close();
    		prep.close();
    		con.close();
    		return room;
    	}
    	catch(SQLException e){
    		System.out.println(e);
    		return null;
    	}
    }

	public static void updateLocation(Event event, Room room){

		/* status: 0 = no response, 1 = Attends, 2 = Not attending*/
		Connection con = null;
		PreparedStatement prep;
		try{
			con = DBConnect.getConnection();
			String query = "UPDATE `Meeting` "
					+ "SET `RoomName` = ? "
					+ "WHERE `EventID` = ?";
			prep = con.prepareStatement(query);
			prep.setString(1, room.getRoomName());
			prep.setInt(2, event.getEventID());
			System.out.println(prep.toString());
			prep.execute();
			System.out.println("Executed");
			prep.close();
			con.close();
		} catch(SQLException e){
			System.out.println(e);
		}
	}
}


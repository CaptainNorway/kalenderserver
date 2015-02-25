package queries;

import database.DBConnect;
import models.Room;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RoomQueries {

    public static ArrayList<Room> getRooms() {
        ArrayList<Room> rooms = new ArrayList<>();
        try {
            Connection con = DBConnect.getConnection();
            String sqlQuery = "SELECT * FROM Room";

            PreparedStatement preparedStatement = con.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int RoomID = resultSet.getInt("RoomID");
                String RoomName = resultSet.getString("RoomName");
                int Capacity = resultSet.getInt("Capacity");
                rooms.add(new Room(RoomID, RoomName, Capacity));
            }
            resultSet.close();
            preparedStatement.close();
            con.close();
            return rooms;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void main(String[] args) {
        ArrayList<Room> rooms = RoomQueries.getRooms();
        System.out.println("Printing a list of the room entries in the database table ROOM:");
        for (Room room : rooms) {
            System.out.println(room);
        }
    }
}

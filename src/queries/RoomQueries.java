package queries;

import database.DBConnect;
import models.Room;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class RoomQueries {

    public static ArrayList<Room> getRooms() {
        ArrayList<Room> rooms = new ArrayList<>();
        Connection con = DBConnect.getConnection();
        try {
            String sqlQuery = "SELECT * FROM Room";

            PreparedStatement pstmt = con.prepareStatement(sqlQuery);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                int RoomID = resultSet.getInt("RoomID");
                String RoomName = resultSet.getString("RoomName");
                int Capacity = resultSet.getInt("Capacity");
                rooms.add(new Room(RoomID, RoomName, Capacity));
            }
            resultSet.close();
            pstmt.close();
            con.close();
            return rooms;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void main(String[] args) {
        ArrayList<Room> rooms = RoomQueries.getRooms();
        for (Room room : rooms) {
            System.out.println(room);
        }
    }
}

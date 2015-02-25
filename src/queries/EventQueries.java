package queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import database.DBConnect;
import models.Event;
import models.Calendar;

public class EventQueries {

	public static ArrayList<Event> getEvents(ArrayList<Calendar> cal){
		Connection conn = null;
	    PreparedStatement pstmt = null;
	    try {
	      conn = DBConnect.getConnection();
	      conn.setAutoCommit(false);
	      String query = ""
	      		+ "SELECT into add_batch_table(stringCol, intCol) values(?, ?)";
	      pstmt = conn.prepareStatement(query);
	      pstmt.setString(1, "1");
	      pstmt.setInt(2, 100);
	      pstmt.addBatch();

	      pstmt.setString(1, "2");
	      pstmt.setInt(2, 200);
	      pstmt.addBatch();

	      pstmt.setString(1, "3");
	      pstmt.setInt(2, 300);
	      pstmt.addBatch();

	      int[] updateCounts = pstmt.executeBatch();
	      checkUpdateCoun

		return null;
	}
}

package database;

import java.sql.*;

/**
 * Created by sondrehj on 23.02.2015.
 */
public class DBConnect {

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://mysql.stud.ntnu.no:3306/sondrehj_fellesprosjekt";

    //  Database credentials
    static final String USER = "sondrehj_fp";
    static final String PASS = "1q2w3e4r";

    public static Connection getConnection(){
        Connection conn = null;
        try {
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
    
    public static void main(String[] args){
        DBConnect.getConnection();
    }
    
}
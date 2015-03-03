package database;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DBConnect {
    public static DataSource getMySQLDataSource() {
    	String MYSQL_DB_DRIVER_CLASS="com.mysql.jdbc.Driver";
    	String MYSQL_DB_URL="jdbc:mysql://mysql.stud.ntnu.no:3306/sondrehj_fellesprosjekt";
    	String MYSQL_DB_USERNAME="sondrehj_fp";
    	String MYSQL_DB_PASSWORD="1q2w3e4r";
        
    	MysqlDataSource mysqlDS = null;
        mysqlDS = new MysqlDataSource();
        mysqlDS.setURL(MYSQL_DB_URL);
        mysqlDS.setUser(MYSQL_DB_USERNAME);
        mysqlDS.setPassword(MYSQL_DB_PASSWORD);
        return mysqlDS;
    }

    public static Connection getConnection() {
        DataSource ds = null;
        ds = getMySQLDataSource();
        Connection con = null;
        try {
            con = ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }
}
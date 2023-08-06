import java.sql.*;

public class Database {
	private static final String USER = "root";
	private static final String PASS = "";
	private static final String URL = "jdbc:mysql://127.0.0.1/mydb";
	private static final String DRIVER = "org.cj.mysql.jdbc.Driver";
	
	private Connection con;
	
	public boolean init(){
		// Register JDBC driver
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			System.err.println("Failed to register JDBC driver");
		}
		try {
			con = DriverManager.getConnection(URL, USER, PASS);
		} catch (SQLException e) {
			System.err.println("Failed to connect to database");
		}
		return true;
	}
	
	public boolean databaseSetup() {
		
		return true;
	}
}

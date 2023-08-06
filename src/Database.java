import java.sql.*;

public class Database {
	private static final String USER = "root";
	private static final String PASS = "";
	private static final String URL = "jdbc:mysql://127.0.0.1/mydb";
	private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
	
	private Connection con;
	private static Database d;
	
	public static Database getInstance() {
		if (d == null)
			d = new Database();
		return d;
	}
	
	public boolean init(){
		// Register JDBC driver
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println("Failed to register JDBC driver");
			return false;
		}
		try {
			con = DriverManager.getConnection(URL, USER, PASS);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Failed to connect to database");
			return false;
		}
		return true;
	}
	
	public boolean databaseSetup() {
		
		return true;
	}
}

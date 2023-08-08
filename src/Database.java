import java.sql.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
	
	// Initialize and 
	public boolean init(){
		// Register JDBC driver
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			System.err.println("Failed to register JDBC driver");
			e.printStackTrace();
			return false;
		}
		// Connect to the database
		try {
			con = DriverManager.getConnection(URL, USER, PASS);
		} catch (SQLException e) {
			System.err.println("Failed to connect to database");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void disconnect() {
		try {
			con.close();
			con = null;
		} catch (SQLException e) {
			System.err.println("Disconnecting from sql database failed");
			e.printStackTrace();
		}
	}
	
	public PreparedStatement getStatement(String query) throws SQLException {
		return con.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}
	
	// Executes query without any return, used for setting up database
	private void executeQuery(String query) throws SQLException {
		Statement s = con.createStatement();
		s.execute(query);
		s.close();
	}
	
	public boolean executeFromFile(String path) {
		try {
			File f = new File(path);
			Scanner s = new Scanner(f);
			
			String input = "";
			while(s.hasNextLine()) {
				input = input.concat(s.nextLine());
				if(!input.isEmpty() && 
				   input.charAt(input.length() - 1) == ';') {
					executeQuery(input);
					input = "";
				}
			}
			s.close();
		} catch (FileNotFoundException e) {
			System.err.println("Database files not found");
			e.printStackTrace();
			return false;
		} catch (SQLException e) {
			System.err.println("Failed to execute query");
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean databaseSetup() {
		
		return true;
	}
}

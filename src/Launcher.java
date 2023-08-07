import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Launcher {
	private static final String SCHEMA_PATH = "src/databaseSchema";
	private static final String INSERT_PATH = "src/databaseEntities";
	
	public static void main(String[] args) {
		Database d = Database.getInstance();
		if(!d.init()) {
			System.out.println("Connection could not be established");
			return;
		}
		Scanner s = new Scanner(System.in);
		String input = "";
		
		frontPage(d, s, input);
		System.out.println("Bye!");
		d.disconnect();
		s.close();
	}

	private static void frontPage(Database d, Scanner s, String input) {
		while(!input.equals("q")) {
			System.out.println("Enter i to initialize and load data into database");
			System.out.println("Enter r to register");
			System.out.println("Enter l to log in");
			System.out.println("Enter q to quit program");
			
			input = s.nextLine();
			switch(input) {
			case "i":
				if(d.executeFromFile(SCHEMA_PATH) && 
				   d.executeFromFile(INSERT_PATH))
					System.out.println("Initialize success!");
				break;
			case "r":
				dashboard(s, register(s));
				break;
			case "l":
				dashboard(s, login(s));
				break;
			}
		}
	}
	
	public static void dashboard(Scanner s, int uid) {
		if(uid <=0)
			return;
		
		String input = "";
		System.out.println();
		while(!input.equals("s")) {
			System.out.println("Enter l to enlist a place");
			System.out.println("Enter b to book a listing");
			System.out.println("Enter s to go to settings");
			System.out.println("Enter c to comment/rate a place");
			System.out.println("Enter p to comment/rate a person");
			System.out.println("Enter s to sign out");
			
			input = s.nextLine();
		}
	}
	
	public static int login(Scanner s) {
		Database d = Database.getInstance();
		try {
			String queryStr = "SELECT uid FROM User"
							+ " WHERE username = ? AND password = ?";
			PreparedStatement p = d.getStatement(queryStr);
			
			System.out.println("enter username");
			String user = s.nextLine();
			System.out.println("enter password");
			String password = s.nextLine();
			
			p.setString(1, user);
			p.setString(2, password);
			ResultSet r = p.executeQuery();
			// Check if password is valid
			if(!r.next()) {
				System.out.println("Invalid password or username");
				return -1;
			}
			return r.getInt("uid");
		} catch (SQLException e) {
			System.err.println("Login query failure!");
			e.printStackTrace();
			return -1;
		}
	}
	
	public static int register(Scanner s) {
		Database d = Database.getInstance();
		try {
			String queryStr = "INSERT INTO User (name, address, occupation, sin, birthday, cardNumber, cardHolder, cardExpire, username, password)"
					        + "VALUES (NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, ?, ?)";
			PreparedStatement p = d.getStatement(queryStr);
			
			System.out.println("enter username");
			String user = s.nextLine();
			System.out.println("enter password");
			String password = s.nextLine();
			
			p.setString(1, user);
			p.setString(2, password);
			p.execute();
			return 24;
		} catch (SQLException e) {
			System.err.println("Login query failure!");
			e.printStackTrace();
			return -1;
		}
	}
	
	public static void printUserPrompt() {
		System.out.println("Enter i to initialize and load data into database");
		System.out.println("Enter r to register");
		System.out.println("Enter l to log in");
		System.out.println("Enter q to quit program");
	}
}

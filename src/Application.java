import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Application {
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
		
		frontPage(s, input);
		System.out.println("Bye!");
		d.disconnect();
		s.close();
	}

	private static void frontPage(Scanner s, String input) {
		Database d = Database.getInstance();
		while(!input.equals("q")) {
			printFrontPagePrompt();
			
			input = s.nextLine();
			switch(input) {
			case "i":
				if(d.executeFromFile(SCHEMA_PATH) && 
				   d.executeFromFile(INSERT_PATH))
					System.out.println("Initialize success!");
				break;
			case "r":
				dashboard(s, User.register(s));
				break;
			case "l":
				dashboard(s, User.login(s));
				break;
			}
		}
	}
	
	private static void dashboard(Scanner s, User u) {
		if(u == null)
			return;
		
		String input = "";
		System.out.println();
		while(!input.equals("o")) {
			printDashboardPrompt();
			
			input = s.nextLine();
			switch(input) {
			case "l":
				Listing.enlistListing(s);
				break;
			case "b":
				break;
			case "h":
				break;
			case "s":
				break;
			}
		}
	}

	private static void printDashboardPrompt() {
		System.out.println("Enter l to enlist a place");
		System.out.println("Enter b to book a listing");
		System.out.println("Enter h to look at history");
		System.out.println("Enter s to go to settings");
		System.out.println("Enter o to sign out");
	}
	
	private static void printFrontPagePrompt() {
		System.out.println("Enter i to initialize and load data into database");
		System.out.println("Enter r to register");
		System.out.println("Enter l to log in");
		System.out.println("Enter q to quit program");
	}
}

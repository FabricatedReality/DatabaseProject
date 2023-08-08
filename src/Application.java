import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Application {
	private static final String SCHEMA_PATH = "src/databaseSchema";
	private static final String INSERT_PATH = "src/databaseEntities";
	
	private static User u;
	
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
				u =  User.register(s);
				dashboard(s);
				break;
			case "l":
				u = User.login(s);
				dashboard(s);
				break;
			}
		}
	}
	
	private static void dashboard(Scanner s) {
		if(u == null)
			return;
		
		String input = "";
		while(!input.equals("o")) {
			printDashboardPrompt();
			
			input = s.nextLine();
			switch(input) {
			case "l":
				Listing.enlistListing(s, u.getUid());
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
	
	private static void searchList(Scanner s) {
		String input = "";
		ResultSet r = Listing.getListings();
		while(!input.equals("b")) {
			
			searchListPrompt();
			
			input = s.nextLine();
			if(input.equals("s")) {
				
			}
		}
	}

	private static void searchListPrompt() {
		System.out.println("Enter number to select");
		System.out.println("Enter a to show amentities for filter");
		System.out.println("Enter b to go back");
	}

	private static void printDashboardPrompt() {
		System.out.println("Enter l to enlist a place");
		System.out.println("Enter b to book a listing");
		System.out.println("Enter h to look at history");
		searchListPrompt();
	}
	
	private static void printFrontPagePrompt() {
		System.out.println("Enter i to initialize and load data into database");
		System.out.println("Enter r to register");
		System.out.println("Enter l to log in");
		System.out.println("Enter q to quit program");
	}
}

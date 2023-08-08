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
		while(!input.equals("s")) {
			printDashboardPrompt();
			
			input = s.nextLine();
			switch(input) {
			case "l":
				Listing.enlistListing(s, u.getUid());
				break;
			case "b":
				searchList(s);
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
			int numListing = printListings(r);
				
			searchListPrompt(numListing);
			
			input = s.nextLine();
			if(input.equals("s")) {
				
			} else if(input.equals("a")) {
				
			} else if(!input.equals("b")) {
				try {
					int index = Integer.parseInt(input);
					r.absolute(index);
					Listing.displayInfo(r.getInt(1));
					listPage(s, r.getInt(1))
				} catch(Exception e) {
					System.out.println("Invalid input!");
					e.printStackTrace();
				}
			}
		}
	}

	private static void listPage(Scanner s, int lid) throws SQLException {
		String input = "";
		while(!input.equals("b")) {
			System.out.println("Enter y to book this listing");
			System.out.println("Enter b to go back");
			input = s.nextLine();
			if(input.equals("y")) {
				Listing.updateInfo(lid, u.getUid());
			}
		}
	}

	private static int printListings(ResultSet r) {
		int i = 1;
		try {
			for(;r.next();i++)
				System.out.println(i+". "+r.getString(2) + ", " + r.getString(3) + " - $" + r.getInt(4));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}

	private static void searchListPrompt(int numListing) {
		if(numListing>0)
			System.out.println("Enter number from 1-" + numListing + " to see more info");
		else
			System.out.println("There are no listings");
		System.out.println("Enter a to show amentities for filter");
		System.out.println("Enter b to go back");
	}

	private static void printDashboardPrompt() {
		System.out.println("Enter l to enlist a place");
		System.out.println("Enter b to book a listing");
		System.out.println("Enter h to look at history");
		System.out.println("Enter s to sign out");
	}
	
	private static void printFrontPagePrompt() {
		System.out.println("Enter i to initialize and load data into database");
		System.out.println("Enter r to register");
		System.out.println("Enter l to log in");
		System.out.println("Enter q to quit program");
	}
}

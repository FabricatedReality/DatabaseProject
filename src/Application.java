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
			case "rp":
				
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
				checkHistory(s);
				break;
			}
		}
		u = null;
	}
	
	private static void checkHistory(Scanner s) {
		String input = "";
		String filter = 
		"WHERE ((Calendar.renter = " + u.getUid() +") OR (Calendar.owner = " + u.getUid() +"))";
		while(!input.equals("b")) {
			ResultSet r = Listing.getListings(filter);
			int numListing = printListHistory(r);
			if(numListing > 0)
				System.out.println("Enter number from 1-" + numListing + " to modify");
			else
				System.out.println("There are no listings");
			System.out.println("Enter b to go back");
			
			input = s.nextLine();
			try {
				if(!input.equals("b")) {
					int index = Integer.parseInt(input);
					r.absolute(index);
					modifyBooking(s, r);
				}
				r.close();
			} catch(Exception e) {
				System.out.println("Invalid input!");
				e.printStackTrace();
			}
		}
	}

	private static void modifyBooking(Scanner s, ResultSet r) throws SQLException {
		Database d = Database.getInstance();
		boolean isOwner = r.getInt(3) == u.getUid();
		System.out.println("Enter c to cancel booking/listing");
		if(isOwner) {
			System.out.println("Enter p to change pricing");
			System.out.println("Enter d to change availability");
		}
		System.out.println("Enter b to go back");
		System.out.println(r.getInt(4));
		switch(s.nextLine()) {
		case "c":
			PreparedStatement p = d.getStatement("UPDATE Calendar SET renter = NULL WHERE cid = ?");
			p.setInt(1, r.getInt(1));
			p.execute();
			break;
		case "p":
			if(r.getInt(4) != 0) {
				System.out.println("List booked already!");
				break;
			}
			PreparedStatement p1 = d.getStatement("UPDATE Calendar SET price = ? WHERE cid = ?");
			System.out.println("Enter new price");
			p1.setString(1, s.nextLine());
			p1.setInt(2, r.getInt(1));
			p1.execute();
			break;
		case "d":
			if(r.getInt(4) != 0) {
				System.out.println("List booked already!");
				break;
			}
			PreparedStatement p2 = d.getStatement("UPDATE Calendar SET start = ?, end = ? WHERE cid = ?");
			System.out.println("Enter start date (YYYY-MM-DD)");
			p2.setString(1, s.nextLine());
			System.out.println("Enter end date (YYYY-MM-DD)");
			p2.setString(2, s.nextLine());
			p2.setInt(3, r.getInt(1));
			p2.execute();
			break;
		case "b":
			return;
		}
	}
	
	private static int printListHistory(ResultSet r) {
		int i = 1;
		try {
			for(;r.next();i++)
				System.out.println(i + ". "+r.getString(16) + ", start date: " + 
								   r.getString(5).toString() + ", end date:" + 
								   r.getDate(6).toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i - 1;
	}

	private static void searchList(Scanner s) {
		String input = "";
		String filter = "WHERE Calendar.renter IS NULL";
		while(!input.equals("b")) {
			ResultSet r = Listing.getListings(filter);
			int numListing = printListings(r);
			searchListPrompt(numListing);
			
			input = s.nextLine();
			try {
				if(input.equals("f")) {
					filter = filterPage(s);
				} else if(!input.equals("b")) {
					int index = Integer.parseInt(input);
					r.absolute(index);
					Listing.displayInfo(r);
					listPage(s, r.getInt(1));
				r.close();
			}
			} catch(Exception e) {
				System.out.println("Invalid input!");
				e.printStackTrace();
			}
		}
	}
	
	private static String filterPage(Scanner s) {
		String input = "";
		String filter = "";
		boolean distanceFilter = false;
		int amenities = 0;
		while(!input.equals("f")) {
			System.out.println("Enter amenities name to add filter to it");
			System.out.println("Enter r to add distance filter");
			System.out.println("Enter t to add type filter");
			System.out.println("Enter n to add name filter");
			System.out.println("Enter c to add city filter");
			System.out.println("Enter co to add city filter");
			System.out.println("Enter po to add postal filter");
			System.out.println("Enter p to add price range filter");
			System.out.println("Enter a to abandon filter");
			System.out.println("Enter f if finished with filter");
			input = s.nextLine();
			switch(input) {
			case "a":
				return "";
			case "r":
				distanceFilter = true;
				break;
			case "t":
				System.out.println("Enter the type");
				filter += " AND (Listing.type = '" + s.nextLine() + "')";
				break;
			case "n":
				System.out.println("Enter the exact name");
				filter += " AND (Listing.name = '" + s.nextLine() + "')";
				break;
			case "c":
				System.out.println("Enter the city name");
				filter += " AND (Listing.city = '" + s.nextLine() + "')";
				break;
			case "co":
				System.out.println("Enter the country name");
				filter += " AND (Listing.country = '" + s.nextLine() + "')";
				break;
			case "po":
				System.out.println("Enter the postal name");
				filter += " AND (Listing.postal = '" + s.nextLine() + "')";
				break;
			case "p":
				System.out.println("Enter the lowest price");
				String low = s.nextLine();
				System.out.println("Enter the highest price");
				String high = s.nextLine();
				filter += " AND (Calendar.price > " + low + " AND Calendar.price < " + high + ")";
				break;
			default:
				amenities = amenities | Listing.toAmenities(input);
			}
		}
		if(amenities>0)
			filter += " AND (Listing.amenities & " + amenities + " = "+ amenities +")";
		if(distanceFilter)
			filter += distanceFilter(s);
		return filter;
	}

	private static String distanceFilter(Scanner s) {
		System.out.println("Enter the latitude");
		String latitude = s.nextLine();
		System.out.println("Enter the longitude");
		String longitude = s.nextLine();
		System.out.println("Enter the radius in km");
		String radius = s.nextLine();
		String filter = " AND ( 6371 * ACOS( COS(RADIANS("+ latitude +")) * "
					  + "COS(RADIANS(Listing.latitude)) * COS(RADIANS(Listing.longitude) - "
					  + "RADIANS("+ longitude +")) + SIN(RADIANS("+ latitude +")) * "
					  + "SIN(RADIANS(Listing.latitude)))) <= " + radius
					  + " ORDER BY ( 6371 * ACOS( COS(RADIANS("+ latitude +")) * "
					  + "COS(RADIANS(Listing.latitude)) * COS(RADIANS(Listing.longitude) - "
					  + "RADIANS("+ longitude +")) + SIN(RADIANS("+ latitude +")) * "
					  + "SIN(RADIANS(Listing.latitude))))";
		return filter;
	}

	private static void listPage(Scanner s, int lid) throws SQLException {
		String input = "";
		while(!input.equals("b")) {
			System.out.println("Enter y to book this listing");
			System.out.println("Enter b to go back");
			input = s.nextLine();
			if(input.equals("y")) {
				Listing.updateInfo(lid, u.getUid());
				break;
			}
		}
	}

	private static int printListings(ResultSet r) {
		int i = 1;
		try {
			for(;r.next();i++)
				System.out.println(i+". "+r.getString(14) + ", " + r.getString(15) + " - $" + r.getInt(7));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i - 1;
	}

	private static void searchListPrompt(int numListing) {
		if(numListing > 0)
			System.out.println("Enter number from 1-" + numListing + " to see more info");
		else
			System.out.println("There are no listings");
		System.out.println("Enter f to modify filter");
		System.out.println("Enter b to go back");
	}

	private static void printDashboardPrompt() {
		System.out.println("Enter l to enlist a place");
		System.out.println("Enter b to book a listing");
		System.out.println("Enter h to look at history/booked/owned listings");
		System.out.println("Enter s to sign out");
	}
	
	private static void printFrontPagePrompt() {
		System.out.println("Enter i to initialize and load data into database");
		System.out.println("Enter r to register");
		System.out.println("Enter l to log in");
		System.out.println("Enter rp to generate reports");
		System.out.println("Enter q to quit program");
	}
}

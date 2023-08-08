import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class Listing {
	private static String[]
	AMENITIES = {"wifi", "washer", "air conditioning", "dedicated workspace", "hair dryer", "kitchen",
				 "dryer", "heating", "tv", "iron", "pool", "free parking", "crib", "bbq grill",
				 "indoor fireplace", "hot tub", "ev charger", "gym", "breakfast", "smoking allowed",
				 "waterfront", "smoke alarm", "carbon monoxide alarm"};
	
	public static void listingCalendar(Scanner s, double latitude, double longitude, int owner) throws SQLException {
		Database d = Database.getInstance();
		String queryStr = "INSERT INTO Calendar (lid, owner, renter, start, end, price)"
						+ "VALUES (?, ?, NULL, ?, ?, ?)";
		PreparedStatement p = d.getStatement(queryStr);
		p.setInt(1, getListingID(latitude, longitude));
		p.setInt(2, owner);
		System.out.println("Starting date of availability for renting (YYYY-MM-DD)");
		Date start = Date.valueOf(s.nextLine());
		p.setDate(3, start);
		System.out.println("ending date of availability for renting (YYYY-MM-DD)");
		Date end = Date.valueOf(s.nextLine());
		p.setDate(4, end);
		int averagePrice = recommendPrice(latitude, longitude, numDays(start, end));
		System.out.println("Enter total price, recommended: " + averagePrice);
		p.setInt(5, Integer.parseInt(s.nextLine()));
		p.execute();
		p.close();
	}
	
	private static int numDays(Date start, Date end) {
		long diffInMS = end.getTime() - start.getTime();
		// Changes from millisecond to days
		return (int)(diffInMS / 86400000);
	}
	
	// Gives average pricing of listings within 22km square
	private static int recommendPrice(double latitude, double longitude, int duration) {
		// recommend 100/night by default
		int averagePricing = 100;
		int numListing = 1;
		try {
			ResultSet r = listingWithinRadius(latitude, longitude, 25);
			while(r.next()) {
				int price = r.getInt(1);
				averagePricing += price / numDays(r.getDate(2), r.getDate(3));
				numListing++;
			}
		} catch(SQLException e) {
			System.err.println("Recommend price query failed");
		}
		return duration * averagePricing / numListing;
	}

	/* Query all listings within radius radius in km around given latitude and longitude, 
	 * ordered from smallest to largest
	 */
	public static ResultSet listingWithinRadius(double latitude, double longitude, double radius) throws SQLException {
		Database d = Database.getInstance();
		String queryStr = "SELECT Calendar.price, Calendar.start, Calendar.end FROM Calendar "
						+ "LEFT JOIN Listing ON Calendar.lid = Listing.lid "
						+ "WHERE Calendar.renter IS NULL AND ( 6371 * ACOS( COS(RADIANS(?)) * "
						+ "COS(RADIANS(Listing.latitude)) * COS(RADIANS(Listing.longitude) - "
						+ "RADIANS(?)) + SIN(RADIANS(?)) * "
						+ "SIN(RADIANS(Listing.latitude)))) <= ?"
						+ "ORDER BY ( 6371 * ACOS( COS(RADIANS(?)) * "
					    + "COS(RADIANS(Listing.latitude)) * COS(RADIANS(Listing.longitude) - "
					    + "RADIANS(?)) + SIN(RADIANS(?)) * "
					    + "SIN(RADIANS(Listing.latitude))))";
		PreparedStatement p = d.getStatement(queryStr);
		p.setDouble(1, latitude);
		p.setDouble(2, longitude);
		p.setDouble(3, latitude);
		p.setDouble(4, radius);
		p.setDouble(5, latitude);
		p.setDouble(6, longitude);
		p.setDouble(7, latitude);
		
		ResultSet r = p.executeQuery();
		return r;
	}
	
	public static void updateCalenndar(int cid) {
		
	}
	
	public static int getListingID(double latitude, double longitude) throws SQLException {
		Database d = Database.getInstance();
		String queryStr = "SELECT lid FROM Listing WHERE latitude = ? AND longitude = ?";
		PreparedStatement p = d.getStatement(queryStr);
		p.setDouble(1, latitude);
		p.setDouble(2, longitude);
		ResultSet r = p.executeQuery();
		r.next();
		int lid = r.getInt(1);
		p.close();
		r.close();
		return lid;
	}
	
	public static void enlistListing(Scanner s, int uid) {
		Database d = Database.getInstance();
		try {
			String queryStr = "INSERT INTO Listing (type, amenities, latitude, longitude, postal, city, country, name)"
					        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement p = d.getStatement(queryStr);
			System.out.println("enter type (house, apartment, guesthouse or hotel)");
			p.setString(1, s.nextLine());
			p.setInt(2, getAmentities(s));
			
			// In case the user skipped all amenities in quick succession
			String stopper = "";
			while(stopper.isEmpty()) {
				System.out.println("enter latitude");
				stopper = s.nextLine();
			}
			double latitude = Double.parseDouble(stopper);
			p.setDouble(3, latitude);
			System.out.println("enter longitude");
			double longitude = Double.parseDouble(s.nextLine());
			p.setDouble(4, longitude);
			System.out.println("enter postal code");
			p.setString(5, s.nextLine());
			System.out.println("enter city");
			p.setString(6, s.nextLine());
			System.out.println("enter country");
			p.setString(7, s.nextLine());
			System.out.println("enter listing name");
			p.setString(8, s.nextLine());
			
			p.execute();
			p.close();
			listingCalendar(s, latitude, longitude, uid);
		} catch (SQLException e) {
			System.err.println("enlist query failure!");
			e.printStackTrace();
		} catch (NumberFormatException e) {
			System.err.println("Parsing error");
			e.printStackTrace();
		}
	}
	
	private static int getAmentities(Scanner s) {
		int result = 0;
		for(int i = 0; i < AMENITIES.length; i++) {
			System.out.println("enter y if listing have " + AMENITIES[i]);
			if(s.nextLine().equals("y"))
				result += (int)Math.pow(2, i);
		}
		return result;
	}

	public static ResultSet getListings(String filter) {
		Database d = Database.getInstance();
		try {
			String queryStr = "SELECT Calendar.*, Listing.*  "
					        + "FROM Calendar JOIN Listing ON Calendar.lid = Listing.lid "
					        + filter;
			PreparedStatement p = d.getStatement(queryStr);
			return p.executeQuery();
		} catch (SQLException e) {
			System.err.println("get listing query failure!");
			e.printStackTrace();
			return null;
		}
	}

	public static void displayInfo(ResultSet r) {
		Database d = Database.getInstance();
		try {
			System.out.println(r.getString(16));
			System.out.println(r.getString(14) + ", " + r.getString(15));
			System.out.println("latitude: " + r.getDouble(11) + ", longitude: " + r.getDouble(12));
			System.out.println("start: " + r.getDate(5).toString() + ", end: " + r.getDate(6).toString());
			showAmenities(r.getInt(10));
		} catch (SQLException e) {
			System.err.println("display info failure!");
			e.printStackTrace();
		}
	}
	
	public static void updateInfo(int lid, int uid) {
		Database d = Database.getInstance();
		try {
			String queryStr = "UPDATE Calendar "
							+ "SET renter = ? "
					        + "WHERE lid = ?";
			PreparedStatement p = d.getStatement(queryStr);
			p.setInt(1, uid);
			p.setInt(2, lid);
			p.execute();
			p.close();
		} catch (SQLException e) {
			System.err.println("update listing failure!");
			e.printStackTrace();
		}
	}

	// return amenities value of input, 0 if not an amenity
	public static int toAmenities(String input) {
		for(int i = 0; i < AMENITIES.length; i++) {
			if(input.equals(AMENITIES[i]))
				return (int)Math.pow(2, i);
		}
		return 0;
	}
	
	public static void showAmenities(int amenities) {
		System.out.print("Amenities: ");
		for(int i = 0; i < AMENITIES.length; i++) {
			int a = (int)Math.pow(2, i);
			if((amenities & a) == a)
				System.out.print(AMENITIES[i]+", ");
		}
		System.out.println();
	}
}

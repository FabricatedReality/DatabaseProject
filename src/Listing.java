import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Listing {
	private static String[]
	AMENITIES = {"wifi", "washer", "air conditioning", "dedicated workspace", "hair dryer", "kitchen",
				 "dryer", "heating", "tv", "iron", "pool", "free parking", "crib", "bbq grill",
				 "indoor fireplace", "hot tub", "ev charger", "gym", "breakfast", "smoking allowed",
				 "waterfront", "smoke alarm", "carbon monoxide alarm"};
	
	public static void listingCalendar(Scanner s, int lid) throws SQLException {
		Database d = Database.getInstance();
		String queryStr = "";
		PreparedStatement p = d.getStatement(queryStr);
	}
	
	public static int getListingID(double latitude, double longitude) throws SQLException {
		Database d = Database.getInstance();
		String queryStr = "SELECT id FROM Listing WHERE latitude = ? AND longitude = ?";
		PreparedStatement p = d.getStatement(queryStr);
		p.setDouble(1, latitude);
		p.setDouble(2, longitude);
		ResultSet r = p.executeQuery();
		int lid = r.getInt(1);
		p.close();
		r.close();
		return lid;
	}
	
	public static void enlistListing(Scanner s) {
		Database d = Database.getInstance();
		try {
			String queryStr = "INSERT INTO Listing (type, amenities, latitude, longitude, postal, city, country, name)"
					        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement p = d.getStatement(queryStr);
			System.out.println("enter type (house, apartment, guesthouse, hotel)");
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
			listingCalendar(s, getListingID(latitude, longitude));
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
}

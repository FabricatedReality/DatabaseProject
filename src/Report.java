import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Report {

	public static void numberListing(String scale) {
		Database d = Database.getInstance();
		try {
			PreparedStatement p = 
			d.getStatement("SELECT " + scale + ", COUNT(*) AS total_listings FROM Listing " +
						   "GROUP BY " + scale);
			ResultSet r = p.executeQuery();
			while(r.next()) {
				System.out.print(r.getString(1));
				if(scale.length() > 8)
					System.out.print("," + r.getString(2));
				if(scale.length() > 12)
					System.out.print("," + r.getString(3));
				System.out.println(": "+ r.getInt("total_listings"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void tenPercent() {
		Database d = Database.getInstance();
		try {
			PreparedStatement p = 
			d.getStatement("SELECT l.country, l.city, u.username, COUNT(l.lid) AS total_listings, "
						 + "SUM(CASE WHEN u.uid IS NOT NULL THEN 1 ELSE 0 END) AS host_listings "
						 + "FROM Listing l JOIN Calendar c ON l.lid = c.lid "
						 + "JOIN User u ON c.owner = u.uid "
						 + "GROUP BY l.country, l.city, u.uid "
						 + "HAVING host_listings > 0.1 * total_listings");
			ResultSet r = p.executeQuery();
			while(r.next()) {
				System.out.println(r.getString("u.username") + " from " + 
								   r.getString("l.country") + "," + r.getString("l.city") + 
								   ": "+ r.getInt("host_listings"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}

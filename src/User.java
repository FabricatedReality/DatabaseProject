import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class User {
	private int uid;
	private String name;
	private String address;
	private String occupation;
	private int sin;
	private String birthday;
	
	private String cardNumber;
	private String cardHolder;
	private String expirationDate;
	
	private String username;
	private String password;
	
	public User(int uid, String name, String address, String occupation, int sin, String birthday, String cardNumber,
				String cardHolder, String expirationDate, String username, String password) {
		this.uid = uid;
		this.name = name;
		this.address = address;
		this.occupation = occupation;
		this.sin = sin;
		this.birthday = birthday;
		this.cardNumber = cardNumber;
		this.cardHolder = cardHolder;
		this.expirationDate = expirationDate;
		this.username = username;
		this.password = password;
	}
	
	public User(String username, String password) {
		this.uid = -1;
		this.name = "";
		this.address = "";
		this.occupation = "";
		this.sin = -1;
		this.birthday = "";
		this.cardNumber = "";
		this.cardHolder = "";
		this.expirationDate = "";
		
		this.username = username;
		this.password = password;
	}
	
	private static User retrieveUser(String user, String password) throws SQLException {
		Database d = Database.getInstance();
		String queryStr = "SELECT * FROM User"
				+ " WHERE username = ? AND password = ?";
		PreparedStatement p = d.getStatement(queryStr);
		p.setString(1, user);
		p.setString(2, password);
		ResultSet r = p.executeQuery();
		if(!r.next())
			return null;
		User u = new User(r.getInt(1), r.getString(2), r.getString(3), r.getString(4), 
				  r.getInt(5), r.getString(6), r.getString(7), r.getString(8),
				  r.getString(9), user, password);
		return u;
	}
	
	public static User login(Scanner s) {
		Database d = Database.getInstance();
		try {
			System.out.println("enter username");
			String user = s.nextLine();
			System.out.println("enter password");
			String password = s.nextLine();
			
			User u = retrieveUser(user, password);
			// Check if password is valid
			if(u == null)
				System.out.println("Invalid password or username");
			
			return u;
		} catch (SQLException e) {
			System.err.println("Login query failure!");
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean updateUser(Scanner s, String user, String pass) throws SQLException {
		Database d = Database.getInstance();
		
		String queryStr = "UPDATE User "
		        		+ "SET name = ?, address = ?, occupation = ?, sin = ?, "
		        		+ "birthday = ?, cardNumber = ?, cardHolder = ?, cardExpire = ? "
		        		+ "WHERE username = ? AND password = ?;";
		PreparedStatement p = d.getStatement(queryStr);
		p.setString(9, user);
		p.setString(10, pass);
		
		System.out.println("enter name");
		p.setString(1, s.nextLine());
		System.out.println("enter address");
		p.setString(2, s.nextLine());
		System.out.println("enter occupation");
		p.setString(3, s.nextLine());
		System.out.println("enter sin");
		p.setInt(4, Integer.parseInt(s.nextLine()));
		System.out.println("enter birthday");
		p.setString(5, s.nextLine());
		System.out.println("enter cardNumber");
		p.setString(6, s.nextLine());
		System.out.println("enter cardHolder");
		p.setString(7, s.nextLine());
		System.out.println("enter cardExpire");
		p.setString(8, s.nextLine());
		
		p.execute();
		p.close();
		return true;
	}
	
	public static User register(Scanner s) {
		Database d = Database.getInstance();
		try {
			String user = validifyUsername(s, d);
			
			String queryStr = "INSERT INTO User (name, address, occupation, sin, birthday, cardNumber, cardHolder, cardExpire, username, password)"
					        + "VALUES (NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, ?, ?)";
			PreparedStatement p = d.getStatement(queryStr);
			
			p.setString(1, user);
			System.out.println("enter password");
			String pass = s.nextLine();
			p.setString(2, pass);
			
			p.execute();
			p.close();
			updateUser(s, user, pass);
			return retrieveUser(user, pass);
		} catch (SQLException e) {
			System.err.println("register query failure!");
			e.printStackTrace();
			return null;
		}
	}

	private static String validifyUsername(Scanner s, Database d) throws SQLException {
		String queryStr = "SELECT * FROM User WHERE username = ?";
		while(true) {
			PreparedStatement p = d.getStatement(queryStr);
			System.out.println("enter username");
			String user = s.nextLine();
			p.setString(1, user);
			ResultSet r = p.executeQuery();
			
			boolean validUser = !r.next();
			p.close();
			r.close();
			if(validUser)
				return user;
			System.out.println("username already exist");
		}
	}
}

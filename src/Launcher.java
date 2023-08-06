import java.util.Scanner;

public class Launcher {
	public static void main(String[] args) {
		Database d = Database.getInstance();
		if(!d.init()) {
			System.out.println("Connection could not be established");
			return;
		}
		Scanner s = new Scanner(System.in);
		String input = "";
		
		while(!input.equals("q")) {
			printUserPrompt();
			input = s.nextLine();
			switch(input) {
			case "i":
				break;
			case "c":
				break;
			}
		}
		System.out.println("Bye!");
		s.close();
	}
	
	public static void printUserPrompt() {
		System.out.println("Enter i to initialize and load data into database");
		System.out.println("Enter c to clear database");
		System.out.println("Enter q to quit program");
	}
}

import java.util.Scanner;

public class Launcher {
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		String input = "";
		while(!input.equals("q")) {
			printUI();
			input = s.nextLine();
			if(input.equals("l")) {
				// Bulk load the data into database
			}
		}
		System.out.println("Bye!");
		s.close();
	}
	
	public static void printUI() {
		System.out.print("\033[H\033[2J\n");
        System.out.flush();
		System.out.println("Enter l to load data into database\n" +
			   	   "Enter q to quit program");
	}
}

//TODO
// 3, 4, 5, 6
// Helper method to print tuples for 3 and 5
// Check successful insert in 1, 2, 4, 6
// Take screenshots for submission

package comp421.gr45.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;

public class Main {

	private static java.util.Scanner reader = new java.util.Scanner(System.in);
	private static Statement stmt;
	private static Connection conn;

	public static void main(String[] args) throws SQLException {
		
		// Load the Postgres JDBC driver
		try {
		DriverManager.registerDriver(new org.postgresql.Driver());
		} catch (Exception e) {
			System.out.println("Class not found");
		}
		
		String url = "jdbc:postgresql://comp421.cs.mcgill.ca:5432/cs421";

		// Connect
		conn = DriverManager.getConnection(url, "cs421g45", "compgroup45");
		stmt = conn.createStatement();

		execLoop();

		// Close the connection and statement when done
		stmt.close();
		conn.close();
	}

	public static void execLoop() throws SQLException {

		int choice = 0;

		while (choice != 7) {
			System.out.println("What would you like to do?");

			for (int i = 1; i <= 7; i++) {
				System.out.println(option(i));
			}

			choice = promptVal(1, 7);
			if (choice == 7) {
				System.out.println("Quitting interface.");
				return;
			} else {
				executeChoice(choice);
			}
		}
	}

	private static String option(int num) {
		if (num == 1)
			return "1 - Add Property";
		else if (num == 2)
			return "2 - Create Account";
		else if (num == 3)
			return "3 - List available properties for given city/dates";
		else if (num == 4)
			return "4 - Request Booking";
		else if (num == 5)
			return "5 - List bookings over date range";
		else if (num == 6)
			return "6 - Add Review (as Guest or Host)";
		else
			return "7 - Quit";
	}

	private static int promptVal(int min, int max) {

		System.out.println("Please choose a valid value between " + min + " and " + max);

		int choice = reader.nextInt();

		while (choice < min || choice > max) {
			System.out.println("Invalid value. Please choose a value between " + min + " and " + max);
			choice = reader.nextInt();
		}

		return choice;
	}

	private static void executeChoice(int choice) throws SQLException {
		if (choice == 1) { // Add Property

			System.out.print("To add a new property, please provide the following inputs:");

			String name = requestString("Name");
			String desc = requestString("Description");
			String addr = requestString("Address");
			String city = requestString("City");
			String ctry = requestString("Country");
			String host = requestString("Host username");

			String query = "INSERT INTO Property VALUES (";
			query += formatString(addr, true);
			query += formatString(name, true);
			query += formatString(desc, true);
			query += formatString(city, true);
			query += formatString(ctry, true);
			query += ("0, ");
			query += formatString(host, false);
			query += ")";

			stmt.executeUpdate(query);

		} else if (choice == 2) { // Create Account

			System.out.println("To create a new account, please provide the following inputs:");

			String user = requestString("Username");
			String pass = requestString("Password");
			String name = requestString("Full name");
			String gder = requestString("Gender");
			String date = requestDate("Date of birth");
			
			String query = "INSERT INTO UserAccount VALUES (";
			query += formatString(user, true);
			query += formatString(pass, true);
			query += formatString(name, true);
			query += formatString(gder, true);
			query += formatDate(date, false);
			query += ")";
			
			stmt.executeUpdate(query);

		} else if (choice == 3) { // List available properties

			System.out.println("Please indicate the city and date range for your search.");
			
			String city = requestString("City");
			String start = requestDate("Start Date");
			String end = requestDate("End Date");
			
			

		} else if (choice == 4) { // Request Booking

		} else if (choice == 5) { // List bookings over date range


		} else { // Add review or critique
			int reviewer=0;
			boolean isValid = false;
			while (isValid == false){
				System.out.println("Specify if you are a writing a review as a host or guest. Type 1 if you are the host or 2 if you are the guest: ");
				if (reader.hasNextInt())
				{
					reviewer = reader.nextInt();
					isValid = true;
				}
				else
				{
					System.out.println(
							"Invalid entry. Try again.");
				}
				reader.nextLine();
				if(isValid==true && (reviewer < 1 || reviewer > 2)){
					System.out.println("Invalid number. Please choose enter 1 if you are the host or 2 if you are the guest");
				}
			}

//			while (!reader.hasNextInt() &&(reviewer < 1 || reviewer > 2)) {
//				System.out.println("Invalid character found, please enter 1 if you are the host or 2 if you are the guest");
//				reviewer = reader.next();
//			}

			System.out.println("To add a review, please provide the following inputs:");

			String content = requestString("Review content");
//			System.out.println("Rating number");
			int rating=0;
			isValid = false;
			while (isValid == false){
				System.out.println("Rating number");
				if (reader.hasNextInt())
				{
					rating = reader.nextInt();
					isValid = true;
				}
				else
				{
					System.out.println(
							"Invalid entry. Try again.");
				}
				reader.nextLine();
				if(isValid==true && (rating < 1 || rating > 5)){
					System.out.println("Invalid number. Please choose a number 1 to 5");
				}
			}
			String name = requestString("Your name");
//			int bid = requestInteger("Booking ID");
			int bid = -1;
			isValid = false;
			while (isValid == false){
				System.out.println("Booking ID");
				if (reader.hasNextInt())
				{
					bid = reader.nextInt();
					isValid = true;
				}
				else
				{
					System.out.println(
							"Invalid entry. Try again.");
				}
				reader.nextLine();
			}
			if (reviewer == 1) {
//				System.out.println("host");
				String query = "INSERT INTO Review VALUES (";
				query += formatString(content, true);
				query += formatString(Integer.toString(rating), true);
				query += formatString(name, true);
				query += formatString(Integer.toString(bid), false);
				query += ")";

				checkSuccessInsert(stmt.executeUpdate(query));
			} else if (reviewer == 2) {
//				System.out.println("guest");
				String query = "INSERT INTO Critique VALUES (";
				query += formatString(content, true);
				query += formatString(Integer.toString(rating), true);
				query += formatString(name, true);
				query += formatString(Integer.toString(bid), false);
				query += ")";

				checkSuccessInsert(stmt.executeUpdate(query));
			}
		}
	}

	private static void checkSuccessInsert(int numChangedEntries){
		if (numChangedEntries != 0){
			System.out.println("Successfully inserted the entry into the table");
		}
		else {
			System.out.println("Unsuccessful in inserting entry into table");
		}
	}

	private static String requestString(String inputName) {
		System.out.println(inputName + "?");
		String result = reader.nextLine();
		return result;
	}

	private static int requestInteger(String inputName) {
		System.out.println(inputName + "?");
		int resultNum = reader.nextInt();
//		int resultNum = 0;
//		boolean checkInt= false;
//		while (checkInt==false) {
//			try {
//				resultNum = reader.nextInt();
//				checkInt = true;
//			} catch (Exception e) {
//				System.out.println("Invalid character found, please enter numeric values only");
//			}
//		}
		return resultNum;
	}

	private static String requestDate(String inputName) {
		System.out.println(inputName + "? Please input as DD/MM/YYYY.");
		String result = reader.nextLine();
		while (!dateValid(result)) {
			System.out.println("Invalid date format. Please enter the date as DD/MM/YYYY (including slashes)");
			result = reader.next();
		}
		return result;
	}

	private static String formatString(String val, boolean addComma) {
		String formatted = "'" + val + "'";
		if (addComma)
			formatted += ", ";
		return formatted;
	}
	
	private static String formatDate(String date, boolean addComma) {
		String formatted = "";
		
		formatted += ("TO_DATE('" + date + "', 'DD/MM/YYYY')");
		
		if (addComma)
			formatted += ", ";
		
		return formatted;
	}

	private static boolean dateValid(String date) {
		String[] dashSplit = date.split("/");
		if (dashSplit.length != 3) // check there are 3 parts to the date
			return false;

		int day, month, year;

		try {
			day = Integer.parseInt(dashSplit[0]);
			month = Integer.parseInt(dashSplit[1]);
			year = Integer.parseInt(dashSplit[2]);
		} catch (NumberFormatException e) { // check that each part is a number
			return false;
		}

		if (day < 1 || day > 31) // check day is valid
			return false;

		if (month < 1 || month > 12) // check month is valid
			return false;

		if (year < 2019 || year > 2030) // check year is valid
			return false;

		return true;
	}

}

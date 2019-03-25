package comp421.gr45.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

		stmt = conn.createStatement();

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

			String query = "INSERT INTO Property VALUES (" + formatStrings(name, desc, addr, city, ctry, host) + ")";

			stmt.executeUpdate(query);

		} else if (choice == 2) { // Create Account

			System.out.println("To create a new account, please provide the following inputs:");

			String user = requestString("Username");
			String pass = requestString("Password");
			String name = requestString("Full name");
			String gder = requestString("Gender");
			String date = requestDate("Date of birth");

		} else if (choice == 3) { // List available properties

		} else if (choice == 4) { // Request Booking

		} else if (choice == 5) { // Confirm Booking

		} else { // Add review

		}
	}

	private static String requestString(String inputName) {
		System.out.println(inputName + "?");
		String result = reader.next();
		return result;
	}

	private static String requestDate(String inputName) {
		System.out.println(inputName + "? Please input as DD-MM-YYYY.");
		String result = reader.next();
		while (!dateValid(result)) {
			System.out.println("Invalid date format. Please enter the date as DD-MM-YYYY (including dashes)");
			result = reader.next();
		}
		return result;
	}

	private static String formatStrings(String... vals) {
		String formatted = "";

		for (int i = 0; i < vals.length; i++) {
			formatted += ("'" + vals[i] + "'"); // surround each String with ''
			if (i != vals.length - 1) {
				formatted += ", "; // add a comma after each parameter except the last
			}
		}

		return formatted;
	}
	
	private static String formatDate(String date) {
		String formatted = "";
		
		String[] split = date.split("-");
		
		return formatted;
	}

	private static boolean dateValid(String date) {
		String[] dashSplit = date.split("-");
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

//TODO
// 3, 4, 5, 6 DONE
// Helper method to print tuples for 3 and 5
// Check successful insert in 1, 2, 4, 6 DONE
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

		// Load the Postgre JDBC driver
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

			options();

			choice = promptVal(1, 7);
			if (choice == 7) {
				System.out.println("Quitting interface.");
				return;
			} else {
				executeChoice(choice);
			}
		}
	}

	private static void options() {
		System.out.println("1 - Add Property");
		System.out.println("2 - Create Account");
		System.out.println("3 - List available properties for given city/dates");
		System.out.println("4 - Request Booking");
		System.out.println("5 - List bookings over date range");
		System.out.println("6 - Add Review (as Guest or Host)");
		System.out.println("7 - Quit");
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

			System.out.println("To add a new property, please provide the following inputs:");

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

			try {
				int affectedTuples = stmt.executeUpdate(query);
				checkSuccessInsert(affectedTuples);
			} catch (Exception e) {
				System.out.println("The query could not be executed for the following reason:");
				System.out.println(e.getMessage() + "\n\n");
			}

		} else if (choice == 2) { // Create Account

			System.out.println("To create a new account, please provide the following inputs:");

			String user = requestString("Username");
			String pass = requestString("Password");
			String name = requestString("Full name");
			String gder = requestString("Gender");
			String date = requestDate("Date of birth");
			String type = requestString("Guest or Host");
			while (!type.equalsIgnoreCase("host") && !type.equalsIgnoreCase("guest")) {
				type = requestString("Guest or Host");
			}

			String query = "INSERT INTO UserAccount VALUES (";
			query += formatString(user, true);
			query += formatString(pass, true);
			query += formatString(name, true);
			query += formatString(gder, true);
			query += formatDate(date, false);
			query += ")";

			try {
				int affectedTuples = stmt.executeUpdate(query);
				checkSuccessInsert(affectedTuples);
			} catch (Exception e) {
				System.out.println("The query could not be executed for the following reason:");
				System.out.println(e.getMessage() + "\n\n");
			}

			query = "INSERT INTO ";
			if (type.equalsIgnoreCase("Host")) {
				query += "Host VALUES (";
				query += formatString(user, false);
				query += ")";
			} else {
				query += "Guest VALUES (";
				query += formatString(user, true);
				query += "0";
				query += ")";
			}

			try {
				int affectedTuples = stmt.executeUpdate(query);
				checkSuccessInsert(affectedTuples);
			} catch (Exception e) {
				System.out.println("The query could not be executed for the following reason:");
				System.out.println(e.getMessage() + "\n\n");
			}

		} else if (choice == 3) { // List available properties

			System.out.println("Please indicate the city and date range for your search.");

			String city = requestString("City");
			String start = requestDate("Start Date"), formattedStart = formatDate(start, false);
			String end = requestDate("End Date"), formattedEnd = formatDate(end, false);

			String subquery = "SELECT property FROM Availability WHERE startDate <= " + formattedStart
					+ " AND endDate >= " + formattedEnd;

			String query = "SELECT * FROM Property WHERE cityName = ";
			query += formatString(city, false);
			query += " AND address IN (";
			query += subquery;
			query += ")";

			try {
				stmt.executeQuery(query);
			} catch (Exception e) {
				System.out.println("The query could not be executed for the following reason:");
				System.out.println(e.getMessage() + "\n\n");
			}

		} else if (choice == 4) { // Send message

			System.out.println("To write a new message, please provide the following information:");
			System.out.println("Start time:");
			int hour = requestInteger("\tHour"), minute = requestInteger("\tMinute"), second = 0;

			String date = requestDate("Date");

			String content = requestString("Message content (max 512 chars)");
			String fromUser = requestString("Message sender");
			String toUser = requestString("Message receiver");

			String query = "INSERT INTO Message VALUES (";
			query += formatTime(hour, minute, second, true);
			query += formatDate(date, true);
			query += formatString(content, true);
			query += formatString(fromUser, true);
			query += formatString(toUser, false);
			query += ")";

			try {
				int affectedTuples = stmt.executeUpdate(query);
				checkSuccessInsert(affectedTuples);
			} catch (Exception e) {
				System.out.println("The query could not be executed for the following reason:");
				System.out.println(e.getMessage() + "\n\n");
			}

		} else if (choice == 5) { // List bookings over date range

			System.out.println("Please indicate the date range for your search.");

			String start = requestDate("Start Date"), formattedStart = formatDate(start, false);
			String end = requestDate("End Date"), formattedEnd = formatDate(end, false);

			String query = "SELECT * FROM Booking WHERE startDate >= " + formattedStart + " AND endDate <= "
					+ formattedEnd;

			try {
				stmt.executeQuery(query);
			} catch (Exception e) {
				System.out.println("The query could not be executed for the following reason:");
				System.out.println(e.getMessage() + "\n\n");
			}

		} else { // Add review or critique
			int reviewer = 0;
			boolean isValid = false;
			while (isValid == false) {
				System.out.println(
						"Specify if you are a writing a review as a host or guest. Type 1 if you are the host or 2 if you are the guest: ");
				if (reader.hasNextInt()) {
					reviewer = reader.nextInt();
					isValid = true;
				} else {
					System.out.println("Invalid entry. Try again.");
				}
				reader.nextLine();
				if (isValid == true && (reviewer < 1 || reviewer > 2)) {
					System.out.println(
							"Invalid number. Please choose enter 1 if you are the host or 2 if you are the guest");
				}
			}

			// while (!reader.hasNextInt() &&(reviewer < 1 || reviewer > 2)) {
			// System.out.println("Invalid character found, please enter 1 if you are the
			// host or 2 if you are the guest");
			// reviewer = reader.next();
			// }

			System.out.println("To add a review, please provide the following inputs:");

			String content = requestString("Review content");
			// System.out.println("Rating number");
			int rating = 0;
			isValid = false;
			while (isValid == false) {
				System.out.println("Rating number");
				if (reader.hasNextInt()) {
					rating = reader.nextInt();
					isValid = true;
				} else {
					System.out.println("Invalid entry. Try again.");
				}
				reader.nextLine();
				if (isValid == true && (rating < 1 || rating > 5)) {
					System.out.println("Invalid number. Please choose a number 1 to 5");
				}
			}

			String name = requestString("Your name");
			// int bid = requestInteger("Booking ID");
			int bid = -1;
			isValid = false;
			while (isValid == false) {
				System.out.println("Booking ID");
				if (reader.hasNextInt()) {
					bid = reader.nextInt();
					isValid = true;
				} else {
					System.out.println("Invalid entry. Try again.");
				}
				reader.nextLine();
			}
			if (reviewer == 1) {
				// System.out.println("host");
				String query = "INSERT INTO Review VALUES (";
				query += formatString(content, true);
				query += formatString(Integer.toString(rating), true);
				query += formatString(name, true);
				query += formatString(Integer.toString(bid), false);
				query += ")";

				try {
					int affectedTuples = stmt.executeUpdate(query);
					checkSuccessInsert(affectedTuples);
				} catch (Exception e) {
					System.out.println("The query could not be executed for the following reason:");
					System.out.println(e.getMessage() + "\n\n");
				}

			} else if (reviewer == 2) {
				// System.out.println("guest");
				String query = "INSERT INTO Critique VALUES (";
				query += formatString(content, true);
				query += formatString(Integer.toString(rating), true);
				query += formatString(name, true);
				query += formatString(Integer.toString(bid), false);
				query += ")";

				try {
					int affectedTuples = stmt.executeUpdate(query);
					checkSuccessInsert(affectedTuples);
				} catch (Exception e) {
					System.out.println("The query could not be executed for the following reason:");
					System.out.println(e.getMessage() + "\n\n");
				}

			}
		}
	}

	private static void checkSuccessInsert(int numChangedEntries) {
		if (numChangedEntries != 0) {
			System.out.println("Successfully inserted the entry into the table");
		} else {
			System.out.println("Unsuccessful in inserting entry into table");
		}
	}

	private static String requestString(String inputName) {
		System.out.println(inputName + "?");
		String result = reader.next();
		return result;
	}

	private static int requestInteger(String inputName) {
		System.out.println(inputName + "?");
		int resultNum = -1;

		while (resultNum == -1) {
			try {
				resultNum = reader.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("Invalid. Please write an integer.");
				reader.next();
			}
		}
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
		String queryFormattedDate = "'";

		String[] split = date.split("/");
		int day = Integer.parseInt(split[0]), month = Integer.parseInt(split[1]), year = Integer.parseInt(split[2]);

		queryFormattedDate += (year + "-");

		if (month < 10)
			queryFormattedDate += ("0" + month + "-");
		else
			queryFormattedDate += (month + "-");

		if (day < 10)
			queryFormattedDate += ("0" + day);
		else
			queryFormattedDate += day;

		if (addComma)
			queryFormattedDate += "', ";
		else
			queryFormattedDate += "'";

		return queryFormattedDate;
	}

	private static String formatTime(int hour, int minute, int second, boolean addComma) {
		String formattedTime = "'";

		if (hour < 10)
			formattedTime += ("0" + hour + ":");
		else
			formattedTime += (hour + ":");

		if (minute < 10)
			formattedTime += ("0" + minute + ":");
		else
			formattedTime += (minute + ":");

		if (second < 10) {
			formattedTime += ("0" + second);
		} else
			formattedTime += second;

		if (addComma)
			formattedTime += "', ";
		else
			formattedTime += "'";

		return formattedTime;
	}

	private static boolean dateValid(String date) {
		String[] split = date.split("/");
		if (split.length != 3) // check there are 3 parts to the date
			return false;

		int day, month, year;

		try {
			day = Integer.parseInt(split[0]);
			month = Integer.parseInt(split[1]);
			year = Integer.parseInt(split[2]);
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

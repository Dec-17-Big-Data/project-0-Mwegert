package com.revature.bank;

import java.util.NoSuchElementException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.connection.ConnectionUtil;
import com.revature.services.BankAccountService;
import com.revature.services.SuperUserService;
import com.revature.services.TransactionService;
import com.revature.services.UserService;

// Mason Wegert
// 12/31/18
// This abstract class is used to decipher user input
// based on a set of booleans that determine state.

public class Speaker { // change to singleton
	private static Speaker speaker = null;
	private CurrentUser currentUser;
	private static final Logger log = LogManager.getLogger(ConnectionUtil.class);


	private UserService userService = null;
	private SuperUserService superUserService = null;
	private BankAccountService bankAccountService = null;
	private TransactionService transactionService = null;

	private Speaker() {
		this.currentUser = new CurrentUser();
		this.userService = UserService.getService();
		this.superUserService = SuperUserService.getService();
		this.bankAccountService = BankAccountService.getService();
		this.transactionService = TransactionService.getService();
	}

	public Speaker getSpeaker() {
		if (speaker == null) {
			speaker = new Speaker();
		}
		return speaker;
	}

	public void action() throws CancelException{
		Scanner scanner = new Scanner(System.in);
		entry(scanner);
		// user should be logged in now
		while(currentUser.isLoggedIn()) {
			try {
				performAction(scanner);
			} catch (CancelException e) {
				log.traceExit(e.getMessage());
				reset();
				scanner.close();
				throw e;
			}
		}
	}

	private void performAction(Scanner scanner) throws CancelException{
		while(currentUser.isLoggedIn()) {
			System.out.println("Enter a command or type 'help' for more info.");
			String input = scanner.nextLine().trim();
			try {
				input = decipher(input);

				if(currentUser.isSuperUser()) { // superuser commands
					switch(input) {
					case "createuser": createUser();
					break;
					case "viewusers": viewUsers();
					break;
					case "viewuser": viewUser();
					break;
					case "edituser": editUser();
					break;
					case "deleteuser": deleteUser();
					break;
					case "logout": System.out.println("Goodbye.");
					throw new CancelException("Superuser logged out.");
					default: log.traceExit("Should never reach this point.");
						break;
					}
				}

				else { // normal user commands
					switch(input) {
					case "getbalances": getBalances();
					break;
					case "getbalance": getBalance();
					break;
					case "createaccount": createAccount();
					break;
					case "deleteaccount": deleteAccount();
					break;
					case "transfer": transferMoney();
					break;
					case "deposit": deposit();
					break;
					case "withdraw": withdraw();
					break;
					case "viewaccount": viewAccount();
					break;
					case "viewaccounts": viewAccounts();
					break;
					case "viewhistory": viewHistory();
					break;
					case "logout": 
						System.out.println("Goodbye.");
						throw new CancelException("User logged out.");
					default: log.traceExit("Should never reach this point.");
					break;
					}
				}
			} catch (InvalidInputException e) {
				System.out.println("Invalid input. Please try again.");
			}
		}
	}

	private void viewHistory() {
		try {

		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
		}
	}

	private void viewAccounts() {
		try {

		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
		}
	}

	private void viewAccount() {
		try {

		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
		}
	}

	private void withdraw() {
		try {

		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
		}
	}

	private void deposit() {
		try {

		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
		}
	}

	private void transferMoney() {
		try {

		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
		}
	}

	private void deleteAccount() {
		try {

		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
		}
	}

	private void createAccount() {
		try {

		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
		}
	}

	private void getBalance() {
		try {

		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
		}
	}

	private void getBalances() {
		try {

		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
		}
	}

	private void deleteUser() {
		try {

		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
		}	
	}

	private void editUser() {
		try {

		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
		}

	}

	private void viewUser() {
		try {

		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
		}

	}

	private void viewUsers() {
		try {

		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
		}

	}

	private void createUser() {
		try {

		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
		}

	}

	private void entry(Scanner scanner) throws CancelException{
		while (!currentUser.isLoggedIn()) {
			System.out.println("Would you like to 'log in' as an existing user or 'register' as a new user?\n");
			String input = scanner.nextLine().trim();
			if ("cancel".equals(input)) {
				throw new CancelException();
			}
			try {
				input = decipher(input);
				if (input.equals("log in")){
					login(scanner);
				} else if (input.equals("register")){
					register(scanner);
				}
				else {
					throw new InvalidInputException();
				}
			} catch (InvalidInputException e) {
				System.out.println("Invalid input. Please try again.");
			} catch (CancelException e){
				log.trace("User backed out of log in or register.");
			}
		}
	}

	private void login(Scanner scanner) throws CancelException{
		String username = "";
		String password = "";
		User existingUser = null;
		SuperUser existingSuperUser = null;

		while(!currentUser.isLoggedIn()) { // existing user wants to log in
			System.out.println("Please enter your username then press enter.");
			username = scanner.nextLine().trim();
			System.out.println("Please enter your password then press enter.");
			password = scanner.nextLine().trim();
			if ("cancel".equals(username) || "cancel".equals(password)) {
				throw new CancelException();
			}
			try {
				if (superUserService.getSuperUser(username).isPresent()) { // this is a superuser
					existingSuperUser = superUserService.getSuperUser(username).get();
					if (password.equals(existingSuperUser.getPassword())) { // correct PW!
						currentUser.setUsername(username);
						currentUser.setPassword(password);
						currentUser.setUserID(existingUser.getUserID());
						currentUser.setSuperUser(true);
						currentUser.setLoggedIn(true);
						System.out.println("Successfully logged in as superuser " + username + ".");
						log.traceExit();
					}
				}
				if (UserService.getService().getUser(username).isPresent()){
					existingUser = UserService.getService().getUser(username).get(); 
					if (password.equals(existingUser.getPassword())) { // correct PW!
						currentUser.setUsername(username);
						currentUser.setPassword(password);
						currentUser.setUserID(existingUser.getUserID());
						currentUser.setSuperUser(false);
						currentUser.setLoggedIn(true);
						System.out.println("Successfully logged in as " + username + ".");
						log.traceExit();
					}
				}

				if (superUserService.getSuperUser(username).isPresent() || 
						UserService.getService().getUser(username).isPresent()) {
					// username exists in db, but invalid PW
					throw new PasswordException();
				} else {
					// username does not exist in DB
					throw new NoSuchElementException();
				}
			} catch (NoSuchElementException e) {
				System.out.println("Username does not exist in the database.");
			} catch (PasswordException e) {
				System.out.println("invalid Password");
			}
		}
	}

	private void register(Scanner scanner) {
		String username = "";
		String password = "";
		User registeredUser = null;
		while(!currentUser.isLoggedIn()) { // new user wants to register
			System.out.println("Please enter a username.");
			username = scanner.nextLine().trim();
			System.out.println("Please enter a password.");
			password = scanner.nextLine().trim();
			try {
				registeredUser = superUserService.createUser(username, password).get();
				currentUser.setUsername(username);
				currentUser.setPassword(password);
				currentUser.setSuperUser(false);
				currentUser.setUserID(registeredUser.getUserID());
				currentUser.setLoggedIn(true);
				System.out.println("Successfully created account " + username + "You are now logged in.\n");
			} catch (NoSuchElementException e) {
				System.out.println("That username already exists in the database. Please enter a unique username.");
				log.traceExit(e.getMessage());
			}
		}
	}

	private String decipher(String gibberish) throws CancelException, InvalidInputException{
		gibberish = normalize(gibberish); // no spaces, all lower case
		if (!currentUser.isLoggedIn()) {
			switch(gibberish) {
			case "login": 
			case "logon":
			case "log on":
			case "log":
			case "existing":
			case "current":
				return "log in";
			case "l":
			case "register": 
			case "reg": 
			case "registor":
			case "registr":
			case "new":
			case "r":
				return "register";
			case "break":
			case "quit":
			case "cancel": throw new CancelException();
			default: throw new InvalidInputException();
			}
		}
		if (currentUser.isSuperUser()) { // ADD A BUNCH OF STUFF TO ME
			switch(gibberish) {
			case "createuser":
			return "createuser";
			case "logout":
			case "log":
			case "out":
			case "cancel":
			case "break":
			case "l":
				return "logout";
			default:
				throw new InvalidInputException();
			}
		} else { // ADD A BUNCH OF STUFF
			return gibberish;
		}

	}

	private void reset() {
		speaker = null;
	}

	private String normalize(String s) { // used to fix user input
		return s.replaceAll(" ","").toLowerCase();
	}



}

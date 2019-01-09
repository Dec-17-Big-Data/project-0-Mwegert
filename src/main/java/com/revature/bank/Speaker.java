package com.revature.bank;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.connection.ConnectionUtil;
import com.revature.services.BankAccountService;
import com.revature.services.SuperUserService;
import com.revature.services.TransactionService;
import com.revature.services.UserService;

// Mason Wegert
// 12/31/18
// This class is used to decipher user input
// based on a set of booleans that determine state.

public class Speaker { // change to singleton
	private static Speaker speaker = null;
	public CurrentUser currentUser = null;
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

	public static Speaker getSpeaker() {
		if (speaker == null) {
			speaker = new Speaker();
		}
		return speaker;
	}

	public void start(Scanner scanner) throws CancelException{

		try {
			Properties props = new Properties();
			InputStream in = new FileInputStream("C:\\Users\\mason\\OneDrive\\Documents\\Revature-Code\\JBDCBank\\resources\\connection.properties");
			props.load(in);
			String fileUsername = props.getProperty("superuser.username");
			String filePassword = props.getProperty("superuser.password");
			superUserService.createSuperUser(fileUsername, filePassword);
		} catch (Exception e) {
			System.out.println("Unable to create new SuperUser - file not found.");
			log.trace("SuperUser creation file not found.");
		}
		while(true) {
			// log in or register:
			entry(scanner);
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
			if(currentUser.isSuperUser()) System.out.println("Successfully logged in as superuser " + currentUser.getUsername() + ".");
			else System.out.println("Successfully logged in as " + currentUser.getUsername() + ".");

			// user should be logged in now
			while(currentUser.isLoggedIn()) {
				try {
					performAction(scanner);
				} catch (CancelException e) {
					log.traceExit(e.getMessage());
					reset();
					break;
				}
			}
		}
	}

	private void performAction(Scanner scanner) throws CancelException{

		System.out.println("\n> Enter a command or type 'help' for more info.\n");
		String input = scanner.nextLine().trim();
		try {
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
			input = decipher(input);

			if(currentUser.isSuperUser()) { // superuser commands
				switch(input) {
				case "createuser": createUser(scanner);
				break;
				case "viewusers": viewUsers(scanner);
				break;
				case "viewuser": viewUser(scanner);
				break;
				case "edituser": editUser(scanner);
				break;
				case "deleteuser": deleteUser(scanner);
				break;
				case "help":
					System.out.println("The following are valid commands:"
							+ "'create user', 'view users'/'view all', "
							+ "'view user'/'view one', 'edit user', 'delete user'\n");
					break;
				case "logout": 
					log.traceExit("Superuser logged out");
					throw new CancelException("Superuser logged out.");
				default: log.traceExit("Should never reach this point.");
				break;
				}
			}

			else { // normal user commands
				switch(input) {
				case "getbalances": getBalances(scanner);
				break;
				case "getbalance": getBalance(scanner);
				break;
				case "createaccount": createAccount(scanner);
				break;
				case "deleteaccount": deleteAccount(scanner);
				break;
				case "transfer": transferMoney(scanner);
				break;
				case "deposit": deposit(scanner);
				break;
				case "withdraw": withdraw(scanner);
				break;
				case "viewhistory": viewHistory(scanner);
				break;
				case "sendmoney": sendMoney(scanner);
				break;
				case "help":
					System.out.println("The following are valid commands: "
							+ "'view all balances'/'view all', 'view one balance'/'view one',"
							+ " 'create account', 'delete account', 'transfer', 'deposit',"
							+ " 'withdraw', 'view history', 'send money'\n");
					break;
				case "logout": 
					log.traceExit("user logged out");
					throw new CancelException("User logged out.");
				default: log.traceExit("Should never reach this point.");
				break;
				}
			}
		} catch (InvalidInputException e) {
			System.out.println("Invalid input. Please try again.");
		}

	}

	private void sendMoney(Scanner scanner) {
		log.traceEntry();
		String input = "";
		BankAccount account1 = null;
		BankAccount account2 = null;
		double transferAmount;
		Map<String, Double> userBalances = null;
		Set<String> accountNames = null; 
		int user2ID;

		try {
			System.out.println("Your accounts are displayed below. Enter the name of the account "
					+ "from which you would like to send.\nOtherwise, type anything and your "
					+ "primary account will be chosen by default. \nThese are CASE SENSITIVE. (Type 'cancel' to exit.)");
			userBalances = userService.getBalances(currentUser.getUserID()).get();
			accountNames = userBalances.keySet();
			System.out.println(accountNames);

			input = scanner.nextLine();
			if ("cancel".equals(normalize(input))) throw new CancelException();

			if (!accountNames.contains(input)) {
				// set account name to default.
				account1 = bankAccountService.getAccount(currentUser.getUserID()).get();

			} else {
				account1 = bankAccountService.getAccount(currentUser.getUserID(), input).get();
			}

			while(true) {
				try {
					System.out.println("How much would you like to send from " + account1.getAccountName() + "?");
					input = scanner.nextLine();
					if (input.charAt(0) == '$') input = input.substring(1);
					transferAmount = Double.parseDouble(input);
					if (transferAmount > account1.getBalance()) {
						System.out.println("You don't have sufficient funds.");
						continue;
					}
					break;
				} catch (NumberFormatException e) {
					log.traceExit(e);
					throw new CancelException("Number format");
				}
			} //transfer amount is set and good to go. Figure out account 2.

			while(true) {
				try {
					System.out.println("Enter the name of the user to whom you would like to send this money.\n"
							+ "Note that this is case sensitive.");
					input = scanner.nextLine();
					if("cancel".equals(normalize(input))) throw new CancelException();
					user2ID = userService.getUser(input).get().getUserID();
					account2 = bankAccountService.getAccount(user2ID).get();
					break;
				} catch(NoSuchElementException e) {
					System.out.println("That user was not found in our system.\n");
				}
			} //ID's and transfer amount set for both

			if (!(bankAccountService.sendMoney(account1.getAccountID(), account2.getAccountID(), transferAmount))) {
				System.out.println("An unknown error occurred. Please find a software engineer.");
				throw new CancelException("Unknown error.");// shouldnt happen, failsafe
			}

			log.traceExit("Successfully transferred $" + transferAmount + " to " + input);
			System.out.println("Successfully transferred $" + transferAmount + " to " + input);
		} catch (CancelException e) {
			System.out.println("Cancelled action.\n");
			log.traceExit(e);
		}

	}

	private void viewHistory(Scanner scanner) {
		log.traceEntry();
		String input = "";
		try {
			System.out.println("Would you like to view 'all' transaction history, or just "
					+ "a 'single' account?");
			input = normalize(scanner.nextLine());
			if ("cancel".equals(input)) throw new CancelException();

			switch(input) {
			case "all":
			case "al":
			case "a":
			case "'all'":
				System.out.println(transactionService.getTransactionsByUser(currentUser.getUserID()).get());
				break;
			case "'single'":
			case "s":
			case "single":
			case "one":
			case "account":
				Map<String, Double> userBalances = null;
				Set<String> accountNames = null; 
				BankAccount userAccount = null;
				System.out.println("Your accounts are displayed below. Enter the name of the account"
						+ "you would like to view.\nOtherwise, type anything and your "
						+ "primary account will be chosen by default. These are CASE SENSITIVE. (Type 'cancel' to exit.)");
				userBalances = userService.getBalances(currentUser.getUserID()).get();
				accountNames = userBalances.keySet();
				System.out.println(accountNames);

				input = scanner.nextLine();
				if ("cancel".equals(normalize(input))) throw new CancelException();

				if (!accountNames.contains(input)) {
					// set account name to default.
					userAccount = bankAccountService.getAccount(currentUser.getUserID()).get();

				} else {
					userAccount = bankAccountService.getAccount(currentUser.getUserID(), input).get();
				}
				System.out.println("Your transaction history for " + userAccount.getAccountName() +
						" is displayed below: ");
				System.out.println(transactionService.getTransactionsByAccount(userAccount.getAccountID()).get());
				log.traceExit("Successfully returned transaction history of a single account.");
			}
		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
			log.traceExit(e);
		} catch(NoSuchElementException e) {
			System.out.println("No transaction history available.");
			log.traceExit(e);
		}
	}

	private void withdraw(Scanner scanner) {
		log.traceEntry();
		Map<String, Double> userBalances = null;
		Set<String> accountNames = null; 
		String input = "";
		Double withdrawlAmount;
		BankAccount userAccount = null;

		try {
			System.out.println("Your accounts are displayed below. Enter the name of the account "
					+ "from which you would like to withdraw.\nOtherwise, type anything and your "
					+ "primary account will be chosen by default. \nThese are CASE SENSITIVE. (Type 'cancel' to exit.)");
			userBalances = userService.getBalances(currentUser.getUserID()).get();
			accountNames = userBalances.keySet();
			System.out.println(accountNames);

			input = scanner.nextLine();
			if ("cancel".equals(normalize(input))) throw new CancelException();

			if (!accountNames.contains(input)) {
				// set account name to default.
				userAccount = bankAccountService.getAccount(currentUser.getUserID()).get();

			} else {
				userAccount = bankAccountService.getAccount(currentUser.getUserID(), input).get();
			}

			while(true) {
				try {
					System.out.println("How much would you like to withdraw from " + userAccount.getAccountName() + "?");
					input = scanner.nextLine();
					if (input.charAt(0) == '$') input = input.substring(1);
					withdrawlAmount = Double.parseDouble(input);
					if (withdrawlAmount > userAccount.getBalance()) {
						System.out.println("You don't have sufficient funds.");
						continue;
					}
					bankAccountService.withdraw(userAccount.getAccountID(), withdrawlAmount);
					log.traceExit("User withdrew " + -withdrawlAmount + " from " + userAccount.getAccountName());
					System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
					System.out.println("Here is your money. Your remaining balance is $" + 
							(userAccount.getBalance() - withdrawlAmount));
					System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$\n");
					break;
				} catch (NumberFormatException e) {
					log.traceExit(e);
					throw new CancelException("Number format");
				}
			}
		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
			log.traceExit(e);
		} catch(NoSuchElementException e) {
			System.out.println("You don't have any balances! You must create an account first!");
			log.traceExit("User tried to withdraw despite having no account.");
		}
	}

	private void deposit(Scanner scanner) {
		log.traceEntry();
		Map<String, Double> userBalances = null;
		Set<String> accountNames = null; 
		String input = "";
		BankAccount userAccount = null;
		Double depositAmount;

		try {
			System.out.println("Your accounts are displayed below. Enter the name of the account "
					+ "into which you would like to deposit.\nOtherwise, type anything and your "
					+ "primary account will be chosen by default. \nThese are CASE SENSITIVE. (Type 'cancel' to exit.)");
			userBalances = userService.getBalances(currentUser.getUserID()).get();
			accountNames = userBalances.keySet();
			System.out.println(accountNames);

			input = scanner.nextLine();
			if ("cancel".equals(normalize(input))) throw new CancelException();

			if (!accountNames.contains(input)) {
				// set account name to default.
				userAccount = bankAccountService.getAccount(currentUser.getUserID()).get();

			} else {
				userAccount = bankAccountService.getAccount(currentUser.getUserID(), input).get();
			}

			System.out.println("How much would you like to deposit into " + userAccount.getAccountName() + "?");
			input = scanner.nextLine();
			if (input.charAt(0) == '$') input = input.substring(1);
			try {
				depositAmount = Double.parseDouble(input);
				bankAccountService.deposit(userAccount.getAccountID(), depositAmount);
				System.out.println("Successfully deposited $" + depositAmount + " into " + userAccount.getAccountName());
				log.traceExit("Successfully deposited $" + depositAmount + " into " + userAccount.getAccountName());
			} catch(NumberFormatException e) {
				log.traceExit(e);
				throw new CancelException("User has no money to deposit.");
			}

		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
			log.traceExit(e);
		} catch(NoSuchElementException e) {
			System.out.println("You must first create an account before making a deposit.");
			log.traceExit("User tried to make a deposit without an account");
		}
	}

	private void transferMoney(Scanner scanner) {
		log.traceEntry();
		Map<String, Double> userBalances = null;
		Set<String> accountNames = null; 
		String input = "";
		BankAccount firstAccount = null;
		BankAccount secondAccount = null;
		Double transferAmount;
		try {
			System.out.println("Your accounts are displayed below. Enter the name of the account "
					+ "FROM which you would like to withdraw.\nOtherwise, type anything and your "
					+ "primary account will be chosen by default. \nThese are CASE SENSITIVE. (Type 'cancel' to exit.)");
			userBalances = userService.getBalances(currentUser.getUserID()).get();
			if (userBalances.size() < 2) throw new NoSuchElementException("Only 1 account.");

			accountNames = userBalances.keySet();
			System.out.println(accountNames);

			input = scanner.nextLine();
			if ("cancel".equals(normalize(input))) throw new CancelException();

			if (!accountNames.contains(input)) {
				// set account name to default.
				firstAccount = bankAccountService.getAccount(currentUser.getUserID()).get();

			} else {
				firstAccount = bankAccountService.getAccount(currentUser.getUserID(), input).get();
			}

			while(true) {
				try {
					System.out.println("How much would you like to withdraw from " + firstAccount.getAccountName() + "?");
					input = scanner.nextLine();
					if (input.charAt(0) == '$') input = input.substring(1);
					transferAmount = Double.parseDouble(input);
					if (transferAmount > firstAccount.getBalance()) {
						System.out.println("You don't have sufficient funds.");
						continue;
					}
					log.trace("User has sufficient funds to make a transfer.");
					break;
				} catch (NumberFormatException e) {
					log.traceExit(e);
					throw new CancelException("Number format");
				}
			}

			System.out.println("Enter the name of the account "
					+ "into which you would like to deposit.\nOtherwise, type anything and your "
					+ "primary account will be chosen by default.(Type 'cancel' to exit.)");
			System.out.println("Account names (case sensitive): \n" + accountNames);

			input = scanner.nextLine();
			if ("cancel".equals(normalize(input))) throw new CancelException();

			if (!accountNames.contains(input)) {
				// set account name to default.
				secondAccount = bankAccountService.getAccount(currentUser.getUserID()).get();

			} else {
				secondAccount = bankAccountService.getAccount(currentUser.getUserID(), input).get();
			}

			if (firstAccount.equals(secondAccount)) {
				System.out.println("You can't transfer from an account to the same account.");
				log.traceExit("User tried to transfer from an account to the same account");
				throw new CancelException("Same account.");
			}

			// good to go on the transfer:
			userService.transfer(currentUser.getUserID(), firstAccount.getAccountName(),
					secondAccount.getAccountName(), transferAmount);
			log.traceExit("Transfer of " + transferAmount + " was successful!");

		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
			log.traceExit(e);
		} catch(NoSuchElementException e) {
			System.out.println("You need at least 2 accounts to transfer between them");
			log.traceExit("User doesnt have 2+ accounts.");
		}
	}

	private void deleteAccount(Scanner scanner) {
		log.traceEntry();
		Map<String, Double> userBalances = null;
		Set<String> accountNames = null; 
		String input = "";
		BankAccount userAccount = null;
		try {
			System.out.println("Your accounts are displayed below. Enter the name of the account "
					+ "you would like to DELETE. \nThese are case sensitive. "
					+ "Note that this action is PERMANENT. \n"
					+ "Ensure that all funds are transferred off the account"
					+ "before deletion. (Type 'cancel' to exit.)");
			userBalances = userService.getBalances(currentUser.getUserID()).get();
			accountNames = userBalances.keySet();
			System.out.println(accountNames);

			input = scanner.nextLine();
			if ("cancel".equals(normalize(input))) throw new CancelException();

			userAccount = bankAccountService.getAccount(currentUser.getUserID(), input).get();

			if (userAccount.getBalance() > 0) {
				System.out.println("This account has a balance of $" + userAccount.getBalance() + ". "
						+ "Please transfer or withdraw this before deletion.");
				throw new CancelException("User has funds remaining on the account");
			}
			System.out.println("Are you ABSOLUTELY SURE that you want to delete " + input + "? ('yes' or 'no')");
			input = normalize(scanner.nextLine());
			switch(input) {
			case "yes":
			case "ye":
			case "yeah":
			case "y":
			case "1":
			case "'yes'":
			case "ya":
				break;
			default: throw new CancelException("User changed their mind");
			}

			userService.deleteAccount(currentUser.getUserID(), userAccount.getAccountName());
			System.out.println(userAccount.getAccountName() + " successfully deleted.");
			log.traceExit(userAccount.getAccountName() + " successfully deleted.");
		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
			log.traceExit(e);
		} catch(NoSuchElementException e) {
			System.out.println("No account exists with that name.");
			log.traceExit(e);
		}
	}

	private void createAccount(Scanner scanner) {
		log.traceEntry();
		String accountName = "";
		double initialDeposit;
		try {
			while(true) {
				System.out.println("Please enter a name for your account.");
				accountName = scanner.nextLine();
				if ("cancel".equals(normalize(accountName))) throw new CancelException();
				if (userService.getBalance(currentUser.getUserID(), accountName).isPresent()) {
					System.out.println("You already have an existing account with that name.\n");
					continue;
				}
				break;
			}

			System.out.println("Please enter an initial deposit (optional)");
			try {
				String temp = scanner.nextLine();
				if (temp.charAt(0) == '$') {
					temp = temp.substring(1);
				}
				initialDeposit = Double.parseDouble(temp);
				initialDeposit = (initialDeposit < 0) ? 0 : initialDeposit;
			} catch(NumberFormatException e) {
				initialDeposit = 0;
			}

			userService.createAccount(currentUser.getUserID(), initialDeposit, accountName);
			log.traceExit("User successfully created an account: " + accountName);
			System.out.println(accountName + " successfully created! Your balance is $" + initialDeposit);

		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
			log.traceExit(e);
		}
	}


	private void getBalance(Scanner scanner) {
		log.traceEntry();
		Map<String, Double> userBalances = null;
		Set<String> accountNames = null; 
		String input = "";
		BankAccount userAccount = null;
		try {
			System.out.println("Your account names are displayed below. Enter the name of the account "
					+ "you would like to view.\nOtherwise, type anything and your "
					+ "primary account will be chosen by default. \nThese are CASE SENSITIVE. (Type 'cancel' to exit.)\n");
			userBalances = userService.getBalances(currentUser.getUserID()).get();
			if (userBalances.size() == 0) throw new NoSuchElementException("No balances"); // safety net

			accountNames = userBalances.keySet();
			System.out.println(accountNames);

			input = scanner.nextLine();
			if ("cancel".equals(normalize(input))) throw new CancelException();

			if (!accountNames.contains(input)) {
				// set account name to default.
				userAccount = bankAccountService.getAccount(currentUser.getUserID()).get();

			} else {
				userAccount = bankAccountService.getAccount(currentUser.getUserID(), input).get();
			}

			System.out.println("You account information is displayed below: \n");
			System.out.println(userAccount + "\n");
			log.traceExit(userAccount);
		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
			log.traceExit(e);
		} catch(NoSuchElementException e) {
			System.out.println("You don't have any accounts!");
			log.traceExit(e);
		}
	}


	private void getBalances(Scanner scanner) {
		log.traceEntry();
		try {
			System.out.println("The following balances were found for " + currentUser.getUsername() + ": \n");
			System.out.println(userService.getBalances(currentUser.getUserID()).get());
		} catch (NoSuchElementException e) {
			System.out.println("No balances to display.");
			log.traceExit(e);
		}
		log.traceExit();
	}


	// superuser
	private void deleteUser(Scanner scanner) {
		log.traceEntry();
		String doomedUser = "";
		boolean repeat = true;
		try {
			do {
				System.out.println("Enter the name of the user you would like to delete, or 'cancel'.");
				doomedUser = scanner.nextLine();
				if ("cancel".equals(doomedUser)) throw new CancelException();

				repeat = !superUserService.deleteUser(doomedUser);
			} while (repeat);
			log.traceExit("Successfully deleted user");
		} catch(CancelException e) {
			log.traceExit(e);
			System.out.println("Cancelled action.\n");
		}	
	}


	private void editUser(Scanner scanner) {
		log.traceEntry();
		String thisUsername = null;
		boolean repeat = true;
		Optional<User> userOpt = Optional.empty();
		try {
			do {
				System.out.println("Enter the name of the user you would like to edit, or 'cancel'.");
				thisUsername = scanner.nextLine();
				if ("cancel".equals(thisUsername)) throw new CancelException();

				userOpt = superUserService.viewUser(thisUsername);
				if (!userOpt.isPresent()) {
					System.out.println("Username is not present in our system. Check your spelling and try again.");
					continue;
				}

				// user DOES exist in DB

				while(true) {
					System.out.println("Would you like to edit this user's 'username' " + thisUsername +
							", or their 'password' " + userOpt.get().getPassword() + ", or 'cancel' ?");
					String input = normalize(scanner.nextLine());
					if ("cancel".equals(input)) break;
					switch(input) {
					case "un":
					case "username":
					case "name":
					case "user":
						while (true) {
							System.out.println("Enter a new username for this user.");
							input = scanner.nextLine();
							if ("cancel".equals(normalize(input))) break;
							if (superUserService.changeUsername(thisUsername, input)) {
								repeat = false; // entire method was a success
								log.traceExit("Successfully changed a username");
								break; // break if new username is unique
							}
							System.out.println("Duplicate username in database. Please try again.");
						}
						break;
					case "pw":
					case "password":
					case "pass":
					case "word":
						System.out.println("Enter a new password for this user.");
						input = scanner.nextLine();
						if ("cancel".equals(normalize(input))) break;
						superUserService.changePassword(thisUsername, input);
						repeat = false;
						log.traceExit("Successfully changed a password");
						break;
					default: System.out.println("I didn't understand that. Please try again.");
					continue;
					}
					break; // only occurs if
				}
			} while(repeat);

		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
			log.traceExit(e);
		}

	}


	private void viewUser(Scanner scanner) {
		log.traceEntry();
		String input = "";
		Optional<User> userOpt = Optional.empty();
		try {
			while(true) {
				System.out.println("Enter the name of the user you would like to view.");
				input = normalize(scanner.nextLine());
				if ("cancel".equals(input)) throw new CancelException();

				userOpt = superUserService.viewUser(input);
				if (!userOpt.isPresent()) { // user doesnt exist in db
					System.out.println("User doesn't exist in database. Check your spelling and try again.");
					continue;
				}

				// user IS present:
				System.out.println("User was found in the database: ");
				System.out.println(userOpt.get());
				log.traceExit("User found in database.");
				break;
			}
		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
			log.traceExit(e);
		}

	}


	private void viewUsers(Scanner scanner) {
		log.traceEntry();
		System.out.println("Here are all users in the database: ");
		System.out.println(superUserService.viewUsers().get());
		log.traceExit();
	}


	private void createUser(Scanner scanner) {
		log.traceEntry();
		String username_in = "";
		String password_in = "";
		try {
			while(true) {
				System.out.println("Enter a username for the new user: ");
				username_in = scanner.nextLine();
				if ("cancel".equals(normalize(username_in))) throw new CancelException("cancelled @ username");
				if (superUserService.viewUser(username_in).isPresent()) {
					System.out.println("User already exists in the database. Please choose a UNIQUE username.");
					continue;
				}
				System.out.println("Enter a password for the new user: ");
				if ("cancel".equals(normalize(password_in))) throw new CancelException("cancelled @ password");
				password_in = scanner.nextLine();
				superUserService.createUser(username_in, password_in);
				System.out.println("Successfully inserted " + username_in + " into Users.");
				log.traceExit("Successfully created " + username_in);
				break;
			}
		} catch(CancelException e) {
			System.out.println("Cancelled action.\n");
			log.traceExit(e);
		}

	}


	private void entry(Scanner scanner) throws CancelException{
		while (!currentUser.isLoggedIn()) {
			System.out.println("Would you like to 'log in' as an existing user or 'register' as a new user?\n");
			String input = scanner.nextLine();
			if ("cancel".equals(normalize(input))) {
				throw new CancelException();
			}
			try {
				input = decipher(input);
				if (input.equals("login")){
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
			if ("cancel".equals(username)) {
				throw new CancelException();
			}
			System.out.println("Please enter your password then press enter.");
			password = scanner.nextLine().trim();
			if ("cancel".equals(password)){
				throw new CancelException();
			}

			try {
				if (superUserService.getSuperUser(username).isPresent()) { // this is a superuser
					existingSuperUser = superUserService.getSuperUser(username).get();
					if (password.equals(existingSuperUser.getPassword())) { // correct PW!
						currentUser.setUsername(username);
						currentUser.setPassword(password);
						currentUser.setUserID(existingSuperUser.getUserID());
						currentUser.setSuperUser(true);
						currentUser.setLoggedIn(true);
						log.traceExit();
						break;
					}
				}
				if (userService.getUser(username).isPresent()){ // optimize this?
					existingUser = UserService.getService().getUser(username).get(); 
					if (password.equals(existingUser.getPassword())) { // correct PW!
						currentUser.setUsername(username);
						currentUser.setPassword(password);
						currentUser.setUserID(existingUser.getUserID());
						currentUser.setSuperUser(false);
						currentUser.setLoggedIn(true);
						log.traceExit();
						break;
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
				System.out.println("Invalid password");
			}
		}
	}


	private void register(Scanner scanner) throws CancelException{
		String username = "";
		String password = "";
		User registeredUser = null;
		while(!currentUser.isLoggedIn()) { // new user wants to register
			System.out.println("Please enter a username.");
			username = scanner.nextLine().trim();
			if ("cancel".equals(normalize(username))) throw new CancelException();
			System.out.println("Please enter a password.");
			password = scanner.nextLine().trim();
			if ("cancel".equals(normalize(password))) throw new CancelException();
			try {
				try {
					superUserService.getSuperUser(username).get();
					throw new CancelException(); // throw if it DOES exist
				} catch (NoSuchElementException e) {
				}

				try {
					registeredUser = superUserService.createUser(username, password).get();
				} catch (NoSuchElementException e) {
					throw new CancelException(); // throw if creation FAILS
				}
				currentUser.setUsername(username);
				currentUser.setPassword(password);
				currentUser.setSuperUser(false);
				currentUser.setUserID(registeredUser.getUserID());
				currentUser.setLoggedIn(true);
				System.out.println("Successfully created account " + username + " You are now logged in.\n");
			} catch (CancelException e) {
				System.out.println("That username already exists in the database. Please enter a unique username.");
				log.traceExit(e.getMessage());
			}
		}
	}


	private String decipher(String gibberish) throws CancelException, InvalidInputException{
		gibberish = normalize(gibberish); // no spaces, all lower case
		if ("cancel".equals(gibberish)) throw new CancelException();

		if (!currentUser.isLoggedIn()) { // not logged in
			switch(gibberish) {
			case "login": 
			case "logon":
			case "log on":
			case "log":
			case "existing":
			case "current":
			case "l":
				return "login";
			case "register": 
			case "reg": 
			case "registor":
			case "registr":
			case "new":
			case "r":
				return "register";
			case "break":
			case "quit":
			default: throw new InvalidInputException();
			}
		}
		if (currentUser.isSuperUser()) { // superusers only
			switch(gibberish) {
			case "createuser":
			case "create":
			case "c":
			case "newuser":
			case "new":
				return "createuser";
			case "viewusers":
			case "view":
			case "viewall":
			case "all":
				return "viewusers";
			case "viewuser":
			case "single":
			case "viewone":
			case "viewspecific":
				return "viewuser";
			case "edituser":
			case "edit":
			case "e":
				return "edituser";
			case "deleteuser":
			case "delete":
			case "del":
			case "d":
				return "deleteuser";
			case "help":
			case "h":
			case "man":
			case "?":
				return "help";
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
		} else { // Normal user
			switch(gibberish) {
			case "getbalances":
			case "balances":
			case "viewall":
			case "viewallbalances":
			case "all":
			case "viewbalances":
				return "getbalances";
			case "view":
			case "getbalance":
			case "single":
			case "viewbalance":
			case "one":
			case "viewonebalance":
			case "viewone":
			case "getsinglebalance":
			case "getsingle":
			case "viewsingle":
				return "getbalance";
			case "createaccount":
			case "create":
			case "new":
			case "newaccount":
			case "newbalance":
				return "createaccount";
			case "deleteaccount":
			case "delete":
			case "remove":
			case "del":
			case "removeaccount":
			case "delaccount":
				return "deleteaccount";
			case "transfer":
			case "transferfunds":
			case "transfermoney":
			case "t":
			case "tr":
				return "transfer";
			case "sendmoney":
			case "send":
			case "sendfunds":
			case "pay":
			case "makepayment":
				return "sendmoney";
			case "deposit":
			case "d":
			case "depo":
			case "drop":
			case "give":
			case "deposits":
				return "deposit";
			case "withdraw":
			case "take":
			case "w":
				return "withdraw";
			case "viewhistory":
			case "history":
			case "transactionhistory":
			case "transaction":
			case "transactions":
				return "viewhistory";
			case "help":
			case "h":
			case "man":
			case "?":
				return "help";
			case "logout":
			case "log":
			case "out":
			case "cancel":
			case "break":
			case "l":
				return "logout";
			default: throw new InvalidInputException();
			}
		}
	}


	private void reset() {
		this.currentUser = new CurrentUser();
	}



	private String normalize(String s) { // used to fix user input
		return s.replaceAll("\\s","").toLowerCase(); 
	}

}

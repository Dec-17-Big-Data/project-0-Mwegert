package com.revature.bank;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class AUserSpeaker {

	public static Map<String, String> loginInfo = new HashMap<>(); // need to store this info into a file
	public static Set<String> superUsers = new LinkedHashSet<>();
	
	private boolean isUser;
	private boolean superUser;
	private String userName = "";
	private String password = "";
	public boolean correctPassword;
	
	public boolean getIsUser() {
		return isUser;
	}
	
	public void testIfUser(Scanner scanner) {
		boolean userLoop = true;

		outerLoop: while(userLoop) {
			System.out.println("Would you like to 'log in' as an existing user or 'register' as a new user?\n");
			try {
				String inputString = normalize(scanner.nextLine());
				switch(inputString) {
				case "login": isUser = true;
				break;
				case "logon":isUser = true;
				break;
				case "log on":isUser = true;
				break;
				case "log":isUser = true;
				break;
				case "existing":isUser = true;
				break;
				case "current":isUser = true;
				break;
				case "l":isUser = true;
				break;
				case "register": isUser = false;
				break;
				case "reg": isUser = false;
				break;
				case "registor": isUser = false;
				break;
				case "registr": isUser = false;
				break;
				case "new": isUser = false;
				break;
				case "r": isUser = false;
				break;
				case "cancel": break outerLoop;
				default: throw new IOException();
				}
				userLoop = false; // no exception thrown, go to step two
			}
			catch(IOException E) {
				System.out.println("I can't hear you. Try again or 'cancel'.");
			}
		}
	} // END METHOD
	
	
	public void login(Scanner scanner) {
		if (isUser) { //existing user
			System.out.println("Please enter your username or type 'cancel': \n");
			while(true) {
				String inputString = normalize(scanner.nextLine());
				if (inputString.equals("cancel")) {
					isUser = false;
					break;
				}
				else if (loginInfo.containsKey(inputString.toString())) {
					userName = inputString.toString();
					inputPassword(scanner, inputString);
					if (password.equals(loginInfo.get(userName))) {
						correctPassword = true;
						if (superUsers.contains(userName)) superUser = true;
						break;
					}
				}
				System.out.println("I didnt find that username in my database. Type 'cancel' to exit or try again.\n");
			}
		}
		
		else { // new user
			System.out.println("Enter a new username\n");
			userName = scanner.nextLine().trim();
			String pw1 = "pw1";
			String pw2 = "pw2";
			while (!pw1.equals(pw2)) {
				System.out.println("Enter a password\n");
				pw1 = scanner.nextLine().trim();
				System.out.println("Please type your password again.\n");
				pw2 = scanner.nextLine().trim();
			} // repeat until user can input the same thing twice...
			password = pw1;
			correctPassword = true;
			writeName(userName,password);
			System.out.println("You have been registered as a new user!\n");
		}
	} // END METHOD
	
	
	private void inputPassword(Scanner scanner, String username) {
		while(!password.equals(loginInfo.get(userName))) {
			System.out.println("Enter password or 'cancel' if you don't know it.");
			password = scanner.nextLine().trim();
			if (password.equals("cancel")) break;
		}
	}
	
	static String normalize(String s) { // used to fix user input
		return s.replaceAll(" ","").toLowerCase();
	}
	
	private static void writeName(String username, String pw) { // use this to write to a file later
		loginInfo.put(username, pw);
	}
	
	String getUserName() {
		return userName;
	}

	String getPassword() {
		return password;
	}
	
	Boolean IsSuperUser() {
		return superUser;
	}
	
	public AUserSpeaker() {
		isUser = false; superUser = false; correctPassword = false;
		superUsers.add("mason");
		loginInfo.put("mason", "admin");
	}

}

package com.revature.bank;

import java.util.List;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.connection.ConnectionUtil;
import com.revature.services.SuperUserService;
import com.revature.services.UserService;

public class BankMain {
	private static final Logger log = LogManager.getLogger(ConnectionUtil.class);


	public static void main(String[] args) {
		log.trace("MAIN METHOD START");
		try {
			SuperUserService superUserService = SuperUserService.getService();
			//List<User> userList = superUserService.viewUsers().get();
			//UserService userService = UserService.getService();
			
			User a = superUserService.createUser("quintin", "pass").get();
			System.out.println(a);
//			for (User user:userList) {
//				System.out.println(user);
//			}
//			System.out.println();
//			
//			superUserService.createUser("Henry", "userid22?");
//			System.out.println();
//			
//			userList = superUserService.viewUsers().get();
//			for (User user:userList) {
//				System.out.println(user);
//			}
			
			//System.out.println(superUserService.viewUser("quintin").get());
		} catch(NoSuchElementException e) {
			System.out.println("No such element.");
		}
	}
}
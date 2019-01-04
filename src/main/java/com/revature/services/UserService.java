package com.revature.services;

import java.util.Map;
import java.util.Optional;

import com.revature.bank.User;
import com.revature.bank.UserDao;
import com.revature.bank.UserOracle;

public class UserService {

	private static UserService userService;
	final static UserDao userDao = UserOracle.getDao();

	private UserService() {}

	public static UserService getService() {
		if (userService == null) {
			userService = new UserService();
		}
		return userService;
	}


	public Optional<Map<String, Double>> getBalances(int userID){
		return userDao.getBalances(userID);
	}

	public Optional<Double> getBalance(int userID){
		return userDao.getBalance(userID);
	}
	
	public Optional<Double> getBalance(int userID, String accountName){
		return userDao.getBalance(userID, accountName);
	}
	
	public Optional<User> getUser(String username){
		return userDao.getUser(username);
	}
	public Optional<User> getUser(int userID){
		return userDao.getUser(userID);
	}
	public boolean createAccount(int userID, double initialDeposit, String accountName) {
		return userDao.createAccount(userID, initialDeposit, accountName);
	}
	
	public boolean createAccount(String username, double initialDeposit, String accountName) {
		return userDao.createAccount(username, initialDeposit, accountName);
	}

	public boolean deleteAccount(int userID, String accountName) {
		return userDao.deleteAccount(userID, accountName);
	}

	public boolean transfer(int userID, String account1, String account2, double amount) {
		return userDao.transfer(userID, account1, account2, amount);
	}
	
	//public boolean sendMoney : to another user
}

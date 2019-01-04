package com.revature.bank;

import java.util.Map;
import java.util.Optional;

public interface UserDao {
	Optional<Map<String, Double>> getBalances(int userID);
	Optional<Double> getBalance(int userID);
	Optional<Double> getBalance(int userID, String accountName);
	Optional<User> getUser(String username);
	Optional<User> getUser(int userID);
	
	boolean createAccount(int userID, double initialDeposit, String accountName);
	boolean createAccount(String username, double initialDeposit, String accountName);
	boolean deleteAccount(int userID, String accountName);
	boolean transfer(int userID, String account1, String account2, double amount);
}

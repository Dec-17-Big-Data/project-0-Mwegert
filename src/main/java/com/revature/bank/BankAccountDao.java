package com.revature.bank;

import java.util.Optional;

public interface BankAccountDao {
	Optional<BankAccount> getAccount(int userID, String accountName);
	void deposit(int accountID, double amount);
	boolean withdraw(int accountID, double amount);
}
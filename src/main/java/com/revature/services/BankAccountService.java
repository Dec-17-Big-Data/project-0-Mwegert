package com.revature.services;

import java.util.Optional;

import com.revature.bank.BankAccount;
import com.revature.bank.BankAccountDao;
import com.revature.bank.BankAccountOracle;

public class BankAccountService {
	
	private static BankAccountService bankAccountService;
	final static BankAccountDao bankAccountDao = BankAccountOracle.getDao();
	
	private BankAccountService() {}
	
	public static BankAccountService getService() {
		if (bankAccountService == null) {
			bankAccountService = new BankAccountService();
		}
		return bankAccountService;
	}
	
	Optional<BankAccount> getAccount(int userID, String name){
		return bankAccountDao.getAccount(userID, name);
	}
	
	void deposit(int accountID, double amount) {
		bankAccountDao.deposit(accountID, amount);
	}
	
	boolean withdraw(int accountID, double amount) {
		return bankAccountDao.withdraw(accountID, amount);
	}
}

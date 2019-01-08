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
	
	public Optional<BankAccount> getAccount(int userID, String name){
		return bankAccountDao.getAccount(userID, name);
	}
	
	public Optional<BankAccount> getAccount(int userID){
		return bankAccountDao.getAccount(userID);
	}
	
	public void deposit(int accountID, double amount) {
		bankAccountDao.deposit(accountID, amount);
	}
	
	public boolean withdraw(int accountID, double amount) {
		return bankAccountDao.withdraw(accountID, amount);
	}
	
	public boolean sendMoney(int accountOneID, int accountTwoID, double amount) {
		return bankAccountDao.sendMoney(accountOneID, accountTwoID, amount);
	}
}

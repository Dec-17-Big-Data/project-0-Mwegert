package com.revature.services;

import java.util.List;
import java.util.Optional;

import com.revature.bank.Transaction;
import com.revature.bank.TransactionDao;
import com.revature.bank.TransactionOracle;

public class TransactionService {
	private static TransactionService transactionService;
	static final TransactionDao transactionDao = TransactionOracle.getDao();
	
	private TransactionService() {}
	
	public static TransactionService getService() {
		if (transactionService == null) {
			transactionService = new TransactionService();
		}
		return transactionService;
	}
	
	Optional<List<Transaction>> getTransactions(){
		return transactionDao.getTransactions();
	}
	
	Optional<List<Transaction>> getTransactionsByUser(int userID){
		return transactionDao.getTransactionsByUser(userID);
	}
	
	Optional<List<Transaction>> getTransactionsByAccount(int accountID){
		return transactionDao.getTransactionsByAccount(accountID);
	}
	
	Optional<Transaction> getTransactionByID(int transactionID){
		return transactionDao.getTransactionByID(transactionID);
	}
}

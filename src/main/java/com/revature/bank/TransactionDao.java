package com.revature.bank;

import java.util.List;
import java.util.Optional;

public interface TransactionDao {
	Optional<List<Transaction>> getTransactions();
	Optional<List<Transaction>> getTransactionsByUser(int userID);
	Optional<List<Transaction>> getTransactionsByAccount(int accountID);
	Optional<Transaction> getTransactionByID(int transactionID);
}

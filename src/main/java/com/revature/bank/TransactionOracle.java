package com.revature.bank;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.connection.ConnectionUtil;

import oracle.jdbc.OracleTypes;

public class TransactionOracle implements TransactionDao {
	private static final Logger log = LogManager.getLogger(ConnectionUtil.class);
	private static TransactionOracle transactionOracle;
	
	private TransactionOracle() {}
	
	public static TransactionDao getDao() {
		if (transactionOracle == null) {
			transactionOracle = new TransactionOracle();
		}
		return transactionOracle;
	}

	@Override
	public Optional<List<Transaction>> getTransactions() {
		log.traceEntry();
		List<Transaction> outputList = new LinkedList<>();
		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			return Optional.empty();
		}

		try {
			CallableStatement cb = con.prepareCall("call getTransactions(?)");
			cb.registerOutParameter(1, OracleTypes.CURSOR);
			cb.execute();
			ResultSet rs = (ResultSet) cb.getObject(1); // id, amount, date, account_id
			
			while(rs.next()) {
				outputList.add(new Transaction(rs.getInt(1), rs.getDouble(2), rs.getTimestamp(3).toLocalDateTime(), rs.getInt(4)));
			}

			
			rs.close();
			cb.close();
			log.traceExit(outputList.size() + " transactions returned.");
			return Optional.of(outputList);
		} catch (SQLException e) {
			System.out.println("Action cannot be completed due to a database error. Please try again.");
			log.traceExit(e);
		}

		return Optional.empty();
	}

	@Override
	public Optional<List<Transaction>> getTransactionsByUser(int userID) {
		log.traceEntry();
		List<Transaction> outputList = new LinkedList<>();
		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			return Optional.empty();
		}

		try {
			//user_id_INPUT users.user_id%type, transactions_OUT out SYS_REFCURSOR
			CallableStatement cb = con.prepareCall("call getTransactionsByUser(?,?)");
			cb.setInt(1, userID);
			cb.registerOutParameter(2, OracleTypes.CURSOR);
			cb.execute();
			ResultSet rs = (ResultSet) cb.getObject(2);
			
			while(rs.next()) {
				outputList.add(new Transaction(rs.getInt(1), rs.getDouble(2), rs.getTimestamp(3).toLocalDateTime(), rs.getInt(4)));
			}
			
			rs.close();
			cb.close();
			log.traceExit(outputList.size() + " transactions returned.");
			return Optional.of(outputList);
		} catch (SQLException e) {
			System.out.println("Action cannot be completed due to a database error. Please try again.");
			log.traceExit(e);
		}

		return Optional.empty();
	}

	@Override
	public Optional<List<Transaction>> getTransactionsByAccount(int accountID) {
		log.traceEntry();
		List<Transaction> outputList = new LinkedList<>();
		Connection con = ConnectionUtil.getConnection();
		
		if (con == null) {
			return Optional.empty();
		}

		try {
			CallableStatement cb = con.prepareCall("call getTransactionsByAccount(?,?)");
			cb.setInt(1, accountID);
			cb.registerOutParameter(2, OracleTypes.CURSOR);
			cb.execute();
			ResultSet rs = (ResultSet) cb.getObject(2);
			
			while(rs.next()) {
				outputList.add(new Transaction(rs.getInt(1), rs.getDouble(2), rs.getTimestamp(3).toLocalDateTime(), accountID));
			}
			
			rs.close();
			cb.close();
			log.traceExit(outputList.size() + " transactions returned.");
			return Optional.of(outputList);
		} catch (SQLException e) {
			System.out.println("Action cannot be completed due to a database error. Please try again.");
			log.traceExit(e);
		}

		return Optional.empty();
	}

	@Override
	public Optional<Transaction> getTransactionByID(int transactionID) {
		log.traceEntry();
		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			return Optional.empty();
		}

		try {
			CallableStatement cb = con.prepareCall("call getTransactionByID(?,?)");
			cb.setInt(1, transactionID);
			cb.registerOutParameter(2, OracleTypes.CURSOR);
			cb.execute();
			ResultSet rs = (ResultSet) cb.getObject(2);
			
			log.traceExit("Returned transaction successfully");
			Optional<Transaction> returnMe = Optional.of(new Transaction(transactionID, rs.getDouble(1), 
					rs.getTimestamp(2).toLocalDateTime(), rs.getInt(3)));
			rs.close();
			cb.close();
			return returnMe;
			
		} catch (SQLException e) {
			System.out.println("Action cannot be completed due to a database error. Please try again.");
			log.traceExit(e);
		}

		return Optional.empty();
	}
	
	

}

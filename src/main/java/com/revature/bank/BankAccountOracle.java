package com.revature.bank;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.connection.ConnectionUtil;

public class BankAccountOracle implements BankAccountDao{
	private static final Logger log = LogManager.getLogger(ConnectionUtil.class);
	private static BankAccountOracle bankAccountOracle;
	
	private BankAccountOracle() {}
	
	public static BankAccountDao getDao() {
		if (bankAccountOracle == null){
			bankAccountOracle = new BankAccountOracle();
		}
		return bankAccountOracle;
	}

	@Override
	public Optional<BankAccount> getAccount(int userID, String accountName) {
		log.traceEntry();
		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			return Optional.empty();
		}

		try {
			CallableStatement cb = con.prepareCall("call getAccount(?,?,?,?)");
			//(account_ID_OUTPUT out number, name_INPUT varchar2, balance_OUTPUT out binary_float, user_id_INPUT number)
			cb.setString(2, accountName);
			cb.setInt(4, userID);
			cb.registerOutParameter(1, java.sql.Types.NUMERIC);
			cb.registerOutParameter(3, java.sql.Types.FLOAT);
			cb.execute();
			
			log.traceExit("Successfully fetched BankAccount object");
			// id, name, balance, userid
			DecimalFormat df = new DecimalFormat("#########.##");
			return Optional.of(new BankAccount(cb.getInt(1), accountName, Double.parseDouble(
					df.format(cb.getDouble(3))), userID));
		} catch (SQLException e) {
			System.out.println("Action cannot be completed due to a database error. Please try again.");
			log.traceExit(e);
		}

		return Optional.empty();
	}

	@Override
	public void deposit(int accountID, double amount) {
		log.traceEntry();
		Connection con = ConnectionUtil.getConnection();
		double fixedAmount = Math.abs(amount); // always deposit a positive amount
		
		if (con == null) {
			return;
		}

		try {
			CallableStatement cb = con.prepareCall("call withdrawOrDeposit(?,?)");
			// (account_id_INPUT number, amount_INPUT binary_float
			cb.setInt(1, accountID);
			cb.setDouble(2, fixedAmount);
			cb.execute();
			
			log.traceExit("Successfully deposited " + fixedAmount + " into " + accountID);
			return;
		} catch (SQLException e) {
			System.out.println("Action cannot be completed due to a database error. Please try again.");
			log.traceExit(e);
		}
		return;
	}

	@Override
	public boolean withdraw(int accountID, double amount) {
		log.traceEntry();
		Connection con = ConnectionUtil.getConnection();
		double fixedAmount = -Math.abs(amount); // always withdraw a negative amount

		if (con == null) {
			return false;
		}

		try {
			CallableStatement cb = con.prepareCall("call withdrawOrDeposit(?,?)");
			// (account_id_INPUT number, amount_INPUT binary_float
			cb.setInt(1, accountID);
			cb.setDouble(2, fixedAmount);
			cb.execute();
			
			System.out.println("Successfully withdrew " + -fixedAmount);
			log.traceExit("Successfully withdrew " + -fixedAmount + " from " + accountID);
			return true;
		} catch (SQLException e) {
			System.out.println("Action cannot be completed due to a database error. Please try again.");
			log.traceExit(e);
		}

		return false;
	}

	@Override
	public Optional<BankAccount> getAccount(int userID) {
		log.traceEntry();
		Connection con = ConnectionUtil.getConnection();
		
		if (con == null) {
			return Optional.empty();
		}
		try {
			CallableStatement cb = con.prepareCall("call getMaxAccount(?,?,?,?)");
			//getMaxAccount(account_ID_OUTPUT out number, name_OUTPUT out varchar2, 
			// balance_OUTPUT out binary_float, user_id_INPUT number)
			cb.setInt(4, userID);
			cb.registerOutParameter(1, java.sql.Types.NUMERIC);
			cb.registerOutParameter(2, java.sql.Types.VARCHAR);
			cb.registerOutParameter(3, java.sql.Types.FLOAT);
			cb.execute();
			
			if (cb.getString(2) == null) {
				log.traceExit("Account not found in db");
				return Optional.empty();
			}
			log.traceExit("Successfully returned BankAccount");
			// (int accountID, String accountName, double balance, int userID)
			return Optional.of(new BankAccount(cb.getInt(1), cb.getString(2), cb.getDouble(3), userID));
		} catch (SQLException e) {
			log.traceExit(e);
			return Optional.empty();
		}
	}
}

package com.revature.bank;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import oracle.jdbc.OracleTypes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.connection.ConnectionUtil;

public class UserOracle implements UserDao{
	private static UserOracle userOracle;
	private static final Logger log = LogManager.getLogger(ConnectionUtil.class);

	private UserOracle() {}

	public static UserDao getDao() {
		if (userOracle == null) {
			userOracle = new UserOracle();
		}
		return userOracle;
	}

	@Override
	public Optional<Map<String, Double>> getBalances(int userID) {
		log.traceEntry();
		Connection con = ConnectionUtil.getConnection();
		Map<String, Double> outputMap = new TreeMap<>();
		
		if (con == null) {
			return Optional.empty();
		}

		try {
			CallableStatement cb = con.prepareCall("call getBalances(?,?)");
			cb.setInt(1, userID);
			cb.registerOutParameter(2, OracleTypes.CURSOR);
			cb.execute();
			ResultSet rs = (ResultSet) cb.getObject(2); // name, balance -> String, Double
			
			while(rs.next()){
				DecimalFormat df = new DecimalFormat("#########.##");
				outputMap.put(rs.getString(1), Double.parseDouble(df.format(rs.getDouble(2))));
			}
			rs.close();
			cb.close();
			log.traceExit("Returned map of size: " + outputMap.size());
			if (outputMap.size() == 0) return Optional.empty(); // safety measure
			return Optional.of(outputMap);
		} catch (SQLException e) {
			System.out.println("Action cannot be completed due to a database error. Please try again.");
			log.traceExit(e);
		}

		return Optional.empty();
	}

	@Override
	public Optional<Double> getBalance(int userID) {
		log.traceEntry();
		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			return Optional.empty();
		}

		try (CallableStatement cb = con.prepareCall("call getLargestBalance(?,?,?)");){
			
			cb.setInt(1, userID);
			cb.registerOutParameter(2, java.sql.Types.VARCHAR); // account name
			cb.registerOutParameter(3, java.sql.Types.FLOAT); // largest balance
			cb.execute();
			
			log.traceExit(cb.getDouble(3));
			DecimalFormat df = new DecimalFormat("#########.##");
			return Optional.of(Double.parseDouble(df.format(cb.getDouble(3))));
		} catch (SQLException e) {
			log.traceExit(e);
		}
		return Optional.empty();
	}
	
	@Override
	public Optional<Double> getBalance(int userID, String accountName) {
		log.traceEntry();
		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			return Optional.empty();
		}

		try (CallableStatement cb = con.prepareCall("call getBalance(?,?,?)");){
			
			cb.setString(1, accountName);
			cb.setInt(2, userID);
			cb.registerOutParameter(3, java.sql.Types.FLOAT); // balance
			cb.execute();
			
			log.traceExit(cb.getDouble(3));
			DecimalFormat df = new DecimalFormat("#########.##");
			return Optional.of(Double.parseDouble(df.format(cb.getDouble(3))));
		} catch (SQLException e) {
			log.traceExit(e);
		}
		return Optional.empty();
	}

	@Override
	public boolean createAccount(int userID, double initialDeposit, String accountName) {
		log.traceEntry();
		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			return false;
		}

		try(CallableStatement cb = con.prepareCall("call addAccountByUserID(?,?,?)");){
			
			cb.setInt(1, userID);
			cb.setDouble(2, initialDeposit);
			cb.setString(3, accountName);
			
			cb.execute();
			
			log.traceExit();
			return true;
		} catch (SQLException e) {
			System.out.println("Action cannot be completed due to a database error. Please try again.");
			log.traceExit(e);
		}

		return false;
	}

	@Override
	public boolean createAccount(String username, double initialDeposit, String accountName) {
		log.traceEntry();
		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			return false;
		}

		try (CallableStatement cb = con.prepareCall("call addAccountByUsername(?,?,?)");){
			
			cb.setString(1, accountName);
			cb.setDouble(2, initialDeposit);
			cb.setString(3, username);
			cb.execute();
			
			log.traceExit();
			System.out.println(accountName + " successfully created.");
			return true;
		} catch (SQLException e) {
			System.out.println("Action cannot be completed due to a database error. Please try again.");
			log.traceExit(e);
		}

		return false;
	}
	
	@Override
	public boolean deleteAccount(int userID, String accountName) {
		log.traceEntry();
		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			return false;
		}

		try (CallableStatement cb = con.prepareCall("call deleteAccountbyUserID(?,?,?)");){
			
			cb.setInt(1, userID);
			cb.setString(2, accountName);
			cb.registerOutParameter(3, java.sql.Types.INTEGER);
			cb.execute();
			
			if (cb.getInt(3) == 0) throw new SQLException();
			
			log.traceExit();
			return true;
		} catch (SQLException e) {
			log.traceExit(e);
		}
		return false;
	}


	@Override
	public boolean transfer(int userID, String account1, String account2, double amount) {
		log.traceEntry();
		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			return false;
		}

		try (CallableStatement cb = con.prepareCall("call transferFundsbyUserID(?,?,?,?,?)");){
			
			//(name1 varchar2, name2 varchar2, transfer_amount binary_float, user_id_INPUT number, succeeded out number)
			cb.setString(1, account1);
			cb.setString(2, account2);
			cb.setDouble(3, amount);
			cb.setInt(4, userID);
			cb.registerOutParameter(5, java.sql.Types.NUMERIC);
			cb.execute();
			
			if (cb.getInt(5) == 1) { // transfer successful
				System.out.println("Transfer of " + amount + " from '" + account1 + "' to '" + account2 + "' was successful.");
				log.traceExit("Transfer of " + amount + " from '" + account1 + "' to '" + account2 + "' was successful.");
				return true;
			}
			System.out.println("Transfer aborted due to insufficient funds.");
			log.traceExit("Transfer aborted due to insufficient  funds.");
		} catch (SQLException e) {
			log.traceExit(e);
		}

		return false;
	}

	@Override
	public Optional<User> getUser(String username) {
		log.traceEntry();
		Connection con = ConnectionUtil.getConnection();
		
		if (con == null) {
			return Optional.empty();
		}
		
		try (CallableStatement cb = con.prepareCall("call getUserInfo(?,?,?,?,?)");){
			
			//(username_INPUT varchar2, password_OUT out varchar2, user_id_OUT out number, 
			// numAccounts out number, totalbalance out binary_float)
			cb.setString(1, username);
			cb.registerOutParameter(2, java.sql.Types.VARCHAR);
			cb.registerOutParameter(3, java.sql.Types.NUMERIC);
			cb.registerOutParameter(4, java.sql.Types.NUMERIC);
			cb.registerOutParameter(5, java.sql.Types.FLOAT);
			cb.execute();
			
			if (cb.getString(2) == null) {
				log.trace("User not found in DB");
				throw new SQLException();
			}
			log.traceExit("Successfully returned user info.");
			return Optional.of(new User(username, cb.getString(2), cb.getInt(3), cb.getInt(4), cb.getDouble(5)));
		} catch (SQLException e) {
			log.traceExit(e);
		}
		return Optional.empty();
	}

}

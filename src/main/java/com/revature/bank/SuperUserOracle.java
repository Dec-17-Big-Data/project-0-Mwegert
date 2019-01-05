package com.revature.bank;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.connection.ConnectionUtil;

public class SuperUserOracle implements SuperUserDao{
	private static SuperUserOracle superUserOracle;
	private static final Logger log = LogManager.getLogger(ConnectionUtil.class);
	
	private SuperUserOracle() {}

	public static SuperUserDao getDao() {
		if (superUserOracle == null) {
			superUserOracle = new SuperUserOracle();
		}
		return superUserOracle;
	}

	@Override
	public Optional<User> createUser(String username, String password) {
		log.traceEntry();
		Connection con = ConnectionUtil.getConnection();
		int userID = 0;
		
		if (con == null) {
			return Optional.empty();
		}
		
		try {
			CallableStatement cb = con.prepareCall("call addUser(?, ?, ?)");
			cb.setString(1, username);
			cb.setString(2,password);
			cb.registerOutParameter(3, java.sql.Types.NUMERIC);
			cb.execute();
			userID = cb.getInt(3);
			
			System.out.println("Successfully inserted " + username + " into Users");
			log.traceExit("Successfully inserted " + username + " into Users");
			return Optional.of(new User(username, password, userID));
			
		} catch (SQLException e) {
			log.traceExit(e.getMessage());
			System.out.println("That username is already taken, or a database error has occurred. Please try again.");
		}
		log.traceExit("Failed to add new user due to duplicate username or SQL exception");
		return Optional.empty();
	}

	@Override
	public Optional<List<User>> viewUsers() {
		log.traceEntry();
		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			return Optional.empty();
		}

		try {
			String sql = "select username, password, user_id from users";
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			
			List<User> allUsers = new ArrayList<>();

			while(rs.next()) {
				CallableStatement cb = con.prepareCall("call getUserAccountInfo(?, ?, ?)");
				cb.setInt(1, rs.getInt("user_id"));
				cb.registerOutParameter(2, java.sql.Types.NUMERIC);
				cb.registerOutParameter(3, java.sql.Types.FLOAT);
				cb.execute();
				
				allUsers.add(new User(rs.getString("username"), rs.getString("password"), rs.getInt("user_id"), 
						cb.getInt(2), cb.getDouble(3)));
			}
			
			rs.close();
			ps.close();
			log.traceExit("Returned array size: " + allUsers.size());
			
			return Optional.of(allUsers);
		} catch (SQLException e) {
			log.traceExit(e.getMessage());
			System.out.println("Action cannot be completed due to a database error. Please try again.");
		}
		log.traceExit("viewUsers returned empty");
		return Optional.empty();
	}

	@Override
	public boolean changeUsername(String username, String newUsername) {
		log.traceEntry();
		Connection con = ConnectionUtil.getConnection();
		int succeeded = 0;
		
		if (con == null) {
			return false;
		}
		try {
		CallableStatement cb = con.prepareCall("call updateusername(?, ?, ?)");
		cb.setString(1, username);
		cb.setString(2, newUsername);
		cb.registerOutParameter(3, java.sql.Types.NUMERIC);	
		
		cb.execute();
		succeeded = cb.getInt(3);
		
		if (succeeded == 1) {
			System.out.println("Successfully updated " + username + " to " + newUsername + "\n");
			log.traceExit("Successfully updated " + username + " to " + newUsername);
			return true;
		}
		
		System.out.println("That username was not found. Check your spelling and try again.");
		log.traceExit("Successfully executed procedure. Nothing updated.");
		return false;
		} catch(SQLException e) {
			log.traceExit(e.getMessage());
			System.out.println("Action cannot be completed due to a database error. Please try again.");
		}
		
		log.traceExit("SQL Exception when trying to update a username.");
		return false;
	}

	@Override
	public boolean changePassword(String username, String newPassword) {
		log.traceEntry();
		Connection con = ConnectionUtil.getConnection();
		int succeeded = 0;
		if (con == null) {
			return false;
		}
		
		try {
			CallableStatement cb = con.prepareCall("call updatepassword(?,?,?)");
			cb.setString(1, username);
			cb.setString(2, newPassword);
			cb.registerOutParameter(3, java.sql.Types.NUMERIC);
			
			cb.execute();
			succeeded = cb.getInt(3);
			
			if (succeeded == 1) {
				System.out.println("Successfully updated " + username + "'s password to " + newPassword);
				log.traceExit("Successfully updated " + username + "'s password to " + newPassword);
				return true;
			}
			
			System.out.println("That username was not found. Check your spelling and try again.");
			log.traceExit("Successfully executed procedure. Nothing updated.");
			return false;
		} catch (SQLException e) {
			log.traceExit(e.getMessage());
			System.out.println("Action cannot be completed due to a database error. Please try again");
		}
		
		log.traceExit("SQL Exception when trying to update a password.");
		return false;
	}

	@Override
	public boolean deleteUser(String username) {
		log.traceEntry();
		Connection con = ConnectionUtil.getConnection();
		int succeeded = 0;
		
		if (con == null) {
			return false;
		}
		
		try {
			CallableStatement cb = con.prepareCall("call deleteUser(?, ?)");
			cb.setString(1, username);
			cb.registerOutParameter(2, java.sql.Types.NUMERIC);
			cb.execute();
			
			succeeded = cb.getInt(2);
			
			if (succeeded == 1) {
				System.out.println("Successfully deleted " + username + " from users");
				log.traceExit("Successfully deleted " + username + " from users");
				return true;
			}
			System.out.println("That username was not found. Check your spelling and try again.");
			log.traceExit("Successfully executed procedure. Nothing deleted.");
			
		} catch (SQLException e) {
			log.traceExit(e.getMessage());
			System.out.println("Action cannot be completed due to a database error. Please try again.");
		}
		
		log.traceExit("Failed to delete user.");
		return false;
	}

	@Override
	public Optional<User> viewUser(String username) {
		log.traceEntry();
		Connection con = ConnectionUtil.getConnection();
		
		if (con == null) {
			return Optional.empty();
		}
		try{
			CallableStatement cb = con.prepareCall("call getUserInfo(?, ?, ?, ?, ?)");
			cb.setString(1, username);
			cb.registerOutParameter(2, java.sql.Types.VARCHAR); // password
			cb.registerOutParameter(3, java.sql.Types.NUMERIC); // userid
			cb.registerOutParameter(4, java.sql.Types.NUMERIC); // numaccounts
			cb.registerOutParameter(5, java.sql.Types.NUMERIC); // totalbalance
			cb.execute();
			
			if (cb.getString(2) == null) {
				throw new SQLException("Username does not exist in database.");
			}
			return Optional.of(new User(username, cb.getString(2), cb.getInt(3), cb.getInt(4), cb.getInt(5)));
		} catch (SQLException e) {
			log.traceExit(e.getMessage());
			System.out.println("Action cannot be completed due to a database error. Please try again.");
		}
		
		log.traceExit("SQL Exception when trying to view a user");
		return Optional.empty();
	}

	@Override
	public Optional<User> viewUser(Integer userid) {
		log.traceEntry();
		Connection con = ConnectionUtil.getConnection();
		
		if (con == null) {
			return Optional.empty();
		}
		try{
			CallableStatement sb = con.prepareCall("call getUserInfoByID(?, ?, ?, ?, ?)");
			sb.setInt(1, userid);
			sb.registerOutParameter(2, java.sql.Types.VARCHAR); // username
			sb.registerOutParameter(3, java.sql.Types.VARCHAR); // password
			sb.registerOutParameter(4, java.sql.Types.NUMERIC); // numaccounts
			sb.registerOutParameter(5, java.sql.Types.FLOAT); // totalbalance
			sb.execute();
			
			if (sb.getString(2) == null) {
				throw new SQLException("UserID does not exist in database.");
			}
			return Optional.of(new User(sb.getString(2), sb.getString(3), userid, sb.getInt(4), sb.getDouble(5)));
		} catch (SQLException e) {
			log.traceExit(e.getMessage());
			System.out.println("Action cannot be completed due to a database error. Please try again.");
		}
		
		log.traceExit("SQL Exception when trying to view a user");
		return Optional.empty();
	}

	@Override
	public Optional<SuperUser> getSuperUser(String username) {
		log.traceEntry();
		Connection con = ConnectionUtil.getConnection();
		
		if (con == null) {
			return Optional.empty();
		}
		try{
			CallableStatement cb = con.prepareCall("call viewSuperUser(?, ?, ?)");
			//(username_INPUT varchar2, superuser_ID_OUTPUT out number, password_OUTPUT out varchar2)
			cb.setString(1, username);
			cb.registerOutParameter(2, java.sql.Types.NUMERIC);
			cb.registerOutParameter(3, java.sql.Types.VARCHAR);
			cb.execute();
			
			if (cb.getString(2) == null) {
				throw new SQLException("SuperUserID does not exist in database.");
			}
			log.traceExit("Successfully returned non null superuser");
			return Optional.of(new SuperUser(cb.getInt(2), username, cb.getString(3)));
		} catch (SQLException e) {
			log.traceExit(e.getMessage());
		}
		
		log.traceExit("SQL Exception when trying to view a SuperUser");
		return Optional.empty();
	}
}

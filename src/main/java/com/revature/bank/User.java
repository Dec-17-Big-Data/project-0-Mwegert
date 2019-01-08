package com.revature.bank;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Map;

public class User implements Serializable{
	private static final long serialVersionUID = 3144942864396536202L;
	
	private String username;
	private String password;
	private int userID;
	private int numAccounts;
	private double totalBalance;
	private Map<String, Double> balances;

	
	public User(String username, String password, int userID, int numAccounts, double totalBalance,
			Map<String, Double> balances) {
		super();
		this.username = username;
		this.password = password;
		this.userID = userID;
		this.numAccounts = numAccounts;
		this.totalBalance = totalBalance;
		this.balances = balances;
	}

	public User(String username, String password, int userID, int numAccounts, double totalBalance) {
		super();
		this.username = username;
		this.password = password;
		this.userID = userID;
		this.numAccounts = numAccounts;
		this.totalBalance = totalBalance;
	}

	public User(String username, String password, int userID, Map<String, Double> balances) {
		super();
		this.username = username;
		this.password = password;
		this.userID = userID;
		this.balances = balances;
		this.numAccounts = balances.size();
		for (String s:balances.keySet()) {
			this.totalBalance += balances.get(s);
		}
	}
	
	public User(String username, String password, int userID, int numAccounts) {
		super();
		this.username = username;
		this.password = password;
		this.userID = userID;
		this.numAccounts = numAccounts;
	}

	public User(String username, String password, int userID) {
		super();
		this.username = username;
		this.password = password;
		this.userID = userID;
	}	

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getNumAccounts() {
		return numAccounts;
	}

	public void setNumAccounts(int numAccounts) {
		this.numAccounts = numAccounts;
	}

	public double getTotalBalance() {
		DecimalFormat df = new DecimalFormat("#########.##");
		return Double.parseDouble(df.format(totalBalance));
	}

	public void setTotalBalance(int totalBalance) {
		this.totalBalance = totalBalance;
	}

	public Map<String, Double> getBalances() {
		return balances;
	}

	public void setBalances(Map<String, Double> balances) {
		this.balances = balances;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((balances == null) ? 0 : balances.hashCode());
		result = prime * result + numAccounts;
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		long temp;
		temp = Double.doubleToLongBits(totalBalance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + userID;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (balances == null) {
			if (other.balances != null)
				return false;
		} else if (!balances.equals(other.balances))
			return false;
		if (numAccounts != other.numAccounts)
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (Double.doubleToLongBits(totalBalance) != Double.doubleToLongBits(other.totalBalance))
			return false;
		if (userID != other.userID)
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("#########.##");
		return "\nUser [username=" + username + ", password=" + password + ", userID=" + userID + ", numAccounts="
				+ numAccounts + ", totalBalance=$" + df.format(totalBalance) + "]";
	}

	
}

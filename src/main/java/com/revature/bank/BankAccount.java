package com.revature.bank;

import java.io.Serializable;

public class BankAccount implements Serializable{

	private static final long serialVersionUID = 2957122529398050600L;
	
	private int accountID;
	private String accountName;
	private double balance;
	private int userID;
	
	public BankAccount(String accountName, double balance, int userID) {
		super();
		this.accountName = accountName;
		this.balance = balance;
		this.userID = userID;
	}

	public BankAccount(int accountID, String accountName, double balance, int userID) {
		super();
		this.accountID = accountID;
		this.accountName = accountName;
		this.balance = balance;
		this.userID = userID;
	}

	public int getAccountID() {
		return accountID;
	}

	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + accountID;
		result = prime * result + ((accountName == null) ? 0 : accountName.hashCode());
		long temp;
		temp = Double.doubleToLongBits(balance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + userID;
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
		BankAccount other = (BankAccount) obj;
		if (accountID != other.accountID)
			return false;
		if (accountName == null) {
			if (other.accountName != null)
				return false;
		} else if (!accountName.equals(other.accountName))
			return false;
		if (Double.doubleToLongBits(balance) != Double.doubleToLongBits(other.balance))
			return false;
		if (userID != other.userID)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BankAccount [accountID=" + accountID + ", accountName=" + accountName + ", balance=" + balance
				+ ", userID=" + userID + "]";
	}
	
}

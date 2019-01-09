package com.revature.bank;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction implements Serializable{

	private static final long serialVersionUID = -444728062040982913L;
	private int transactionID;
	private double amount;
	private LocalDateTime transactionDate;
	private int accountID;
	
	
	public Transaction(int transactionID, double amount, LocalDateTime transactionDate, int accountID) {
		super();
		this.transactionID = transactionID;
		this.amount = amount;
		this.transactionDate = transactionDate.minusHours(5); // EAST COAST TIME ZONE
		this.accountID = accountID;
	}
	
	public int getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(int transactionID) {
		this.transactionID = transactionID;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}
	public int getAccountID() {
		return accountID;
	}
	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + accountID;
		long temp;
		temp = Double.doubleToLongBits(amount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((transactionDate == null) ? 0 : transactionDate.hashCode());
		result = prime * result + transactionID;
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
		Transaction other = (Transaction) obj;
		if (accountID != other.accountID)
			return false;
		if (Double.doubleToLongBits(amount) != Double.doubleToLongBits(other.amount))
			return false;
		if (transactionDate == null) {
			if (other.transactionDate != null)
				return false;
		} else if (!transactionDate.equals(other.transactionDate))
			return false;
		if (transactionID != other.transactionID)
			return false;
		return true;
	}

	@Override
	public String toString() {
		DateTimeFormatter dt = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm:ss");
		DecimalFormat df = new DecimalFormat("#########.##");
		return "\nTransaction [transactionID= " + transactionID + ", amount= " + df.format(amount) + ", transactionDate= "
				+ transactionDate.format(dt) + ", accountID= " + accountID + "]";
	}
	
	
	
}

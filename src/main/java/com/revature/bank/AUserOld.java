package com.revature.bank;

import java.util.LinkedList;

public class AUserOld {
	String username = "";
	String password = "";
	LinkedList<String> Balances;
	LinkedList<String> viewBalances() {
		return Balances;}
	void createAccount() {} // create bank account
	void deleteAccount() {} // delete bank account
	boolean addMoney(BankAccount currentAccount) {
		return false;} // return true if successful
	boolean withdrawMoney(BankAccount currentAccount) {
		return false;} // return true if successful
	void logout() {}
}

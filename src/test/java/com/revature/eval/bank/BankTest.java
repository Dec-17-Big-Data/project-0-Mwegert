package com.revature.eval.bank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import com.revature.bank.CancelException;
import com.revature.bank.Speaker;
import com.revature.bank.SuperUser;
import com.revature.bank.User;
import com.revature.services.BankAccountService;
import com.revature.services.SuperUserService;
import com.revature.services.TransactionService;
import com.revature.services.UserService;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BankTest {
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private static Speaker speaker = Speaker.getSpeaker();
	private SuperUserService suService = SuperUserService.getService();
	private String username = "testUser";
	private String password = "testPassword";
	private int userID = 98; // "Perm Account"
	private String deletionUsername = "deleteUser";
	private String deletionPassword = "deletePassword";
	UserService uService = UserService.getService();
	TransactionService tService = TransactionService.getService();
	BankAccountService baService = BankAccountService.getService();
	int accountID = 98; // yes 98 - coincidence
	// 26 tests
	//expectedException.expect(IllegalArgumentException.class);

	@Test
	public void testACreation() {
		suService.deleteUser(deletionUsername);
		User newUser = suService.createUser(deletionUsername, deletionPassword).get();
		assertTrue(newUser.getUsername().equals(deletionUsername));
		assertTrue(newUser.getPassword().equals(deletionPassword));
	}

	@Test
	public void testBDuplicateCreation() {
		Optional<User> duplicateUser = suService.createUser(deletionUsername, deletionPassword);
		assertEquals(duplicateUser, Optional.empty());
	}

	@Test
	public void testCView() {
		Optional<User> thisUser = suService.viewUser(username);
		assertEquals(thisUser.get().getUsername(), username);
		assertEquals(thisUser.get().getPassword(), password);
	}

	@Test
	public void testDViewByID() { // assuming testView works as intended
		Optional<User> thisUser = suService.viewUser(userID);
		assertEquals(thisUser.get().getUserID(), userID);
	}

	@Test
	public void testEViewAll() {
		Optional<List<User>> allUsers = suService.viewUsers();
		User thisUser = suService.viewUser(username).get();
		assertTrue(allUsers.get().contains(thisUser));
	}

	@Test
	public void testFGetSuperUser() {
		Optional<SuperUser> thisSuperUser = suService.getSuperUser("wegert");
		assertEquals(thisSuperUser.get().getUsername(),"wegert");
		assertEquals(thisSuperUser.get().getPassword(),"admin");
	}

	@Test
	public void testGChangeUsername() {
		String newUsername = "temporaryUsername";
		suService.changeUsername(newUsername, this.username);
		suService.changeUsername(this.username, newUsername);
		assertEquals(suService.viewUser(newUsername).get().getUsername(),newUsername);
		suService.changeUsername(newUsername, this.username); // change back
		assertEquals(suService.viewUser(username).get().getUsername(),this.username);
	}

	@Test
	public void testHChangePassword() {
		String password = "newPassword";
		suService.changePassword(username, password);
		assertEquals(suService.viewUser(username).get().getPassword(),password);
		suService.changePassword(username, this.password); // change back
		assertEquals(suService.viewUser(username).get().getPassword(),this.password);
	}

	@Test
	public void testHDChangePasswordForNonexistentUser() {
		String newPassword = "newPassword";
		assertFalse(suService.changePassword("Idontexist", newPassword));
	}

	@Test
	public void testIDeletion() {
		suService.deleteUser(deletionUsername);
		assertEquals(suService.viewUser(deletionUsername),Optional.empty());
	}

	@Test
	public void testJDuplicateDeletion() {
		assertFalse(suService.deleteUser(deletionUsername));
	}

	// TODO: create more superuser tests for edge cases

	// NORMAL USER TESTS
	// uservice

	@Test
	public void testKCreateAccount() { // accounts w/ same name for 1 user?
		uService.deleteAccount(userID, "Temp Account");
		uService.createAccount(userID, 11.23, "Temp Account");
		double temp = uService.getBalance(this.userID, "Temp Account").get();
		assertEquals(Math.round(temp * 100), Math.round(11.23 * 100));
	}

	@Test
	public void testLCreateAccountWithNumbersAndUsername() {
		uService.deleteAccount(userID, "Account2");
		uService.createAccount(username, 0, "Account2");
		double temp = uService.getBalance(userID, "Account2").get();
		assertEquals(Math.round(temp * 100), Math.round(0.0 * 100));
	}

	@Test
	public void testMCreateAccountWithNegativeDeposit() {
		assertFalse(uService.createAccount(userID, -30, "In the red"));
	}

	@Test
	public void testNDeleteAccount() {
		uService.deleteAccount(userID, "Account2");
		assertEquals(uService.getBalance(userID, "Account2"), Optional.empty());
	}

	@Test
	public void testODeleteNonExistentAccount() {
		assertFalse(uService.deleteAccount(userID, "sdfsdf"));
	}

	@Test
	public void testPTransfer() {
		uService.deleteAccount(userID, "transfer from me");
		uService.deleteAccount(userID, "transfer to me");
		
		uService.createAccount(userID, 42.42, "transfer from me");
		uService.createAccount(userID, 0, "transfer to me");
		uService.transfer(userID, "transfer from me", "transfer to me", 10.00);
		assertEquals(Math.round(uService.getBalance(userID, "transfer from me").get() * 100), Math.round(32.42 * 100));
		assertEquals(Math.round(uService.getBalance(userID, "transfer to me").get() * 100), Math.round(10.00 * 100));
		uService.deleteAccount(userID, "transfer from me");
		uService.deleteAccount(userID, "transfer to me");
	}

	@Test
	public void testQGetBalance() {
		assertEquals(Math.round(uService.getBalance(userID, "Temp Account").get()* 100), Math.round(11.23*100));
	}

	@Test
	public void testRGetMaxBalance() {
		assertEquals(Math.round(uService.getBalance(userID).get()* 100), 
				Math.round(uService.getBalance(userID, "Perm Account").get()*100));
	}

	@Test
	public void testSGetUserByName() {
		assertEquals(uService.getUser(username).get().getUsername(),username);
	}

	@Test
	public void testTgetAllBalances() {
		assertEquals(Math.round(uService.getBalances(userID).get().get("Temp Account")* 100), Math.round(11.23*100));
	}

	@Test
	public void testUTransferWithInsufficientFunds() {
		assertFalse(uService.transfer(userID, "Temp Account", "wef", 10000));
	}

	//baservice
	@Test
	public void testVDeposit() {
		double beforebalance = uService.getBalance(userID, "Perm Account").get();
		baService.deposit(accountID, 10);
		assertEquals(Math.round(uService.getBalance(userID, "Perm Account").get() * 100), Math.round((beforebalance + 10) * 100));
	}

	@Test
	public void testWWithdrawl() {
		double beforebalance = uService.getBalance(userID, "Perm Account").get();
		baService.withdraw(accountID, 10);
		assertEquals(Math.round(uService.getBalance(userID, "Perm Account").get() * 100), Math.round((beforebalance - 10) * 100));
	}

	@Test
	public void testXWithdrawlWithInsufficientFunds() {
		assertFalse(baService.withdraw(accountID, 1000));
	}

	@Test
	public void testYGetMaxAccount() {
		assertTrue(baService.getAccount(userID).get().getAccountName().equals("Perm Account"));
	}

	@Test
	public void testZGetSpecificAccount() {
		assertTrue(baService.getAccount(userID, "Perm Account").get().getAccountName().equals("Perm Account"));
	}

	@Test
	public void testaSendMoney() {
		suService.deleteUser("sendmemoney");
		suService.createUser("sendmemoney", "pw").get();
		uService.createAccount("sendmemoney", 10.0, "myacc");
		double beforeBalance = uService.getBalance(userID, "Perm Account").get();
		
		int accountOneID = baService.getAccount(uService.getUser("sendmemoney").get().getUserID(), "myacc").get().getAccountID();
		baService.sendMoney(accountOneID, 98, 10);
		assertEquals(Math.round(uService.getBalance(98).get() * 100), ((Math.round((beforeBalance + 10) * 100))));
		assert(uService.getBalance(accountOneID).get().equals(0.0));
		baService.withdraw(98, 10);
		suService.deleteUser("sendmemoney");
	}

	@Test
	public void testbGetTransactionByUser() {
		assertNotEquals(Optional.empty(), tService.getTransactionsByUser(userID).get());
	}

	@Test
	public void testbGetTransactionByAccount() {
		assertNotEquals(Optional.empty(), tService.getTransactionsByAccount(accountID).get());
	}

	@Test
	public void testbGetallTransactions() {
		assertNotEquals(Optional.empty(), tService.getTransactions().get());
	}

	@Test
	public void testbGetTransactionByID() {
		assertEquals(156, tService.getTransactionByID(156).get().getTransactionID());
	}

	@Test
	public void ztestFrontEnd() {
		try {
			String inputs = "register\n" + "revature\n" + "securepassword\n" + "help\n" + "create\n" + "Primary\n" + "100\n"
					+ "create\n" + "Secondary\n" + "0\n" + "all\n" + "one\n" + "asdf\n" + "delete\n" + "Secondary\n" + "yes\n" + "withdraw\n"
					+ "asdf\n" + "$20.42\n" + "logout\n" + "cancel\n";
			Scanner scanner = new Scanner(inputs);
			speaker.start(scanner);
		} catch (CancelException e) {
			int thisUserID = uService.getUser("revature").get().getUserID();
			assertEquals(Math.round(uService.getBalance(thisUserID, "Primary").get() * 100), Math.round((100-20.42) * 100));
		}
	}


	@Test
	public void zztestFrontEnd() {
		suService.createUser("deleteme123", "wefwef");
		try {
			String inputs = "login\n" + "wegert\n" + "admin\n" + "help\n" + "one\n" + "revature\n" + "all\n" + "delete\n" + "deleteme123\n"
					+ "edit\n" + "revature\n" + "password\n" + "securepassword123\n" + "logout\n" + "cancel\n";
			Scanner scanner = new Scanner(inputs);
			speaker.start(scanner);
		} catch (CancelException e) {
			assertEquals(uService.getUser("revature").get().getPassword(), "securepassword123");
		}
	}

	@Test
	public void zzztestFrontEnd() {
		double initialBalance = uService.getBalance(8, "Primary account").get();
		try {
			String inputs = "login\n" + "quintin\n" + "python\n" + "withdraw\n" + "Primary account\n" + "100000\n" +
					"cancel\n" + "transfer\n" + "Secondary account\n" + "1.12\n" + "Primary account\n" + "send\n" + "asdf\n" +
					"4.30\n" + "revature\n" + "logout\n" + "cancel\n";
			Scanner scanner = new Scanner(inputs);
			speaker.start(scanner);
		} catch (CancelException e) {
			assertEquals(Math.round(100 * (initialBalance + 1.12 - 4.30)), Math.round(100 * uService.getBalance(8, "Primary account").get()));
		}
	}

	@Test
	public void zzzztestFrontEnd() {
		try {
			String inputs = "login\n" + "wegert\n" + "admin\n" + "delete\n" + "revature\n" + "logout\n" + "cancel\n";
			Scanner scanner = new Scanner(inputs);
			speaker.start(scanner);
		} catch(CancelException e) {
			assertEquals(uService.getUser("revature"), Optional.empty());
		}
	}
}


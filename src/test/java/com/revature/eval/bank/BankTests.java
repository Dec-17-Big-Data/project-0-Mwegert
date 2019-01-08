package com.revature.eval.bank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.revature.bank.CancelException;
import com.revature.bank.Speaker;
import com.revature.bank.SuperUser;
import com.revature.bank.User;
import com.revature.services.BankAccountService;
import com.revature.services.SuperUserService;
import com.revature.services.UserService;

public class BankTests {
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private static Speaker speaker = Speaker.getSpeaker();
	private SuperUserService suService = SuperUserService.getService();
	private String username = "testUser";
	private String password = "testPassword";
	private int userID = 98;
	private String deletionUsername = "deleteUser";
	private String deletionPassword = "deletePassword";
	UserService uService = UserService.getService();
	
	BankAccountService baService = BankAccountService.getService();
	int accountID = 98; // set manually
	// 26 tests
	//expectedException.expect(IllegalArgumentException.class);
	
	@Test
	public void testCreation() {
		User newUser = suService.createUser(deletionUsername, deletionPassword).get();
		assertTrue(newUser.getUsername().equals(deletionUsername));
		assertTrue(newUser.getPassword().equals(deletionPassword));
	}

	@Test
	public void testDuplicateCreation() {
		Optional<User> duplicateUser = suService.createUser(deletionUsername, deletionPassword);
		assertEquals(duplicateUser, Optional.empty());
	}

	@Test
	public void testView() {
		Optional<User> thisUser = suService.viewUser(username);
		assertEquals(thisUser.get().getUsername(), username);
		assertEquals(thisUser.get().getPassword(), password);
	}

	@Test
	public void testViewByID() { // assuming testView works as intended
		int userID = suService.viewUser(username).get().getUserID();
		Optional<User> thisUser = suService.viewUser(userID);
		assertEquals(thisUser.get().getUserID(), userID);
	}

	@Test
	public void testViewAll() {
		Optional<List<User>> allUsers = suService.viewUsers();
		User thisUser = suService.viewUser(username).get();
		assertTrue(allUsers.get().contains(thisUser));
	}
	
	@Test
	public void testGetSuperUser() {
		Optional<SuperUser> thisSuperUser = suService.getSuperUser("wegert");
		assertEquals(thisSuperUser.get().getUsername(),"wegert");
		assertEquals(thisSuperUser.get().getPassword(),"admin");
	}

	@Test
	public void testChangeUsername() {
		String username = "newUsername";
		suService.changeUsername(this.username, username);
		assertEquals(suService.viewUser(username).get().getUsername(),username);
		suService.changeUsername(username, this.username); // change back
		assertEquals(suService.viewUser(this.username).get().getUsername(),this.username);
	}
	
	@Test
	public void testChangePassword() {
		String password = "newPassword";
		suService.changePassword(username, password);
		assertEquals(suService.viewUser(username).get().getPassword(),password);
		suService.changePassword(username, this.password); // change back
		assertEquals(suService.viewUser(username).get().getPassword(),this.password);
	}
	
	@Test
	public void testDeletion() {
		suService.deleteUser(deletionUsername);
		assertEquals(suService.viewUser(deletionUsername),Optional.empty());
	}

	@Test
	public void testDuplicateDeletion() {
		assertFalse(suService.deleteUser(deletionUsername));
	}

	// TODO: create more superuser tests for edge cases

	// NORMAL USER TESTS
	// uservice
	
	@Test
	public void testCreateAccount() { // accounts w/ same name for 1 user?
		uService.createAccount(this.userID, 10.23, "Temp Account");
		double temp = uService.getBalance(this.userID, "Temp Account").get();
		assertEquals(Math.round(temp * 100), Math.round(10.23 * 100));
	}
	
	@Test
	public void testCreateAccountWithNumbersAndUsername() {
		uService.deleteAccount(userID, "Account2");
		uService.createAccount(username, 0, "Account2");
		double temp = uService.getBalance(userID, "Account2").get();
		assertEquals(Math.round(temp * 100), Math.round(0.0 * 100));
	}
	
	@Test
	public void testCreateAccountWithNegativeDeposit() {
		assertFalse(uService.createAccount(userID, -30, "In the red"));
	}
	
	@Test
	public void testDeleteAccount() {
		uService.deleteAccount(userID, "Account2");
		assertEquals(uService.getBalance(userID, "Account2"), Optional.empty());
	}
	
	@Test
	public void testDeleteNonExistentAccount() {
		assertFalse(uService.deleteAccount(userID, "sdfsdf"));
	}
	
	@Test
	public void testTransfer() {
		uService.createAccount(userID, 42.42, "transfer from me");
		uService.createAccount(userID, 0, "transfer to me");
		uService.transfer(userID, "transfer from me", "transfer to me", 10.00);
		assertEquals(Math.round(uService.getBalance(userID, "transfer from me").get() * 100), Math.round(32.42 * 100));
		assertEquals(Math.round(uService.getBalance(userID, "transfer to me").get() * 100), Math.round(10.00 * 100));
		uService.deleteAccount(userID, "transfer from me");
		uService.deleteAccount(userID, "transfer to me");
	}

	@Test
	public void testGetBalance() {
		assertEquals(Math.round(uService.getBalance(userID, "Temp Account").get()* 100), Math.round(10.23*100));
	}

	@Test
	public void testGetMaxBalance() {
		assertEquals(Math.round(uService.getBalance(userID).get()* 100), Math.round(10.23*100));
	}
	
	@Test
	public void testGetUserByName() {
		assertEquals(uService.getUser(username).get().getUsername(),username);
	}
	
	@Test
	public void getAllBalances() {
		assertEquals(Math.round(uService.getBalances(userID).get().get("Temp Account")* 100), Math.round(10.23*100));
	}
	
	
	//baservice
	@Test
	public void testDeposit() {
		baService.deposit(accountID, 10);
		assertEquals(Math.round(uService.getBalance(userID, "Perm Account").get() * 100), Math.round((10 + 10.23) * 100));
	}
	
	@Test
	public void testWithdrawl() {
		baService.withdraw(accountID, 10);
		assertEquals(Math.round(uService.getBalance(userID, "Perm Account").get() * 100), Math.round((10.23) * 100));
	}
	
	@Test
	public void testWithdrawlWithInsufficientFunds() {
		assertFalse(baService.withdraw(accountID, 100));
	}

	@Test
	public void testTransferWithInsufficientFunds() {
		assertFalse(uService.transfer(userID, "Temp Account", "wef", 100));
	}
	




	// FRONT END TESTS
	@Test
	public void testLogin() {
		try {
			String inputs = "log in\n" + "bob\n" + "password1\n" + "logout\n";
			Scanner scanner = new Scanner(inputs);
			speaker.start(scanner);
			assert(speaker.currentUser.isLoggedIn());
			assert(!speaker.currentUser.isSuperUser());
		} catch (CancelException e) {}
	}

	@Test
	public void testSuperLogin() {
		try {
			String inputs = "log in\n" + "wegert\n" + "admin\n" + "logout\n";
			Scanner scanner = new Scanner(inputs);
			speaker = Speaker.getSpeaker(); // ??? test
			speaker.start(scanner);
			assert(speaker.currentUser.isLoggedIn());
			assert(speaker.currentUser.isSuperUser());
		} catch (CancelException e) {}
	}

}


package com.revature.services;

import java.util.List;
import java.util.Optional;

import com.revature.bank.SuperUser;
import com.revature.bank.SuperUserDao;
import com.revature.bank.SuperUserOracle;
import com.revature.bank.User;

public class SuperUserService {
	
	private static SuperUserService superUserService;
	final static SuperUserDao superUserDao = SuperUserOracle.getDao();
	
	private SuperUserService() {}
	
	public static SuperUserService getService() {
		if (superUserService == null){
			superUserService = new SuperUserService();
		}
		return superUserService;
	}
	
	public Optional<User> createUser(String username, String password){
		return superUserDao.createUser(username, password);
	}
	
	public Optional<List<User>> viewUsers(){
		return superUserDao.viewUsers();
	}
	
	public boolean changeUsername(String username, String newUsername) {
		return superUserDao.changeUsername(username, newUsername);
	}
	
	public boolean changePassword(String username, String newPassword) {
		return superUserDao.changePassword(username, newPassword);
	}
	
	public void deleteUser(String username) {
		superUserDao.deleteUser(username);
	}
	
	public Optional<User> viewUser(String username){
		return superUserDao.viewUser(username);
	}
	
	public Optional<User> viewUser(Integer userid){
		return superUserDao.viewUser(userid);
	}
	
	public Optional<SuperUser> getSuperUser(String username){
		return superUserDao.getSuperUser(username);
	}
}

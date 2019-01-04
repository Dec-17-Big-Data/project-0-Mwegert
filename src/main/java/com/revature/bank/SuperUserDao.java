package com.revature.bank;

import java.util.List;
import java.util.Optional;

public interface SuperUserDao {
	Optional<User> createUser(String username, String password);
	Optional<List<User>> viewUsers();
	Optional<User> viewUser(String username);
	Optional<User> viewUser(Integer userid);
	Optional<SuperUser> getSuperUser(String username);
	
	boolean changeUsername(String username, String newUsername);
	boolean changePassword(String username, String newPassword);
	void deleteUser(String username);
}

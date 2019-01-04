package com.revature.bank;

public class CurrentUser { // singleton that stores all the session data.
	private boolean superUser = false;
	private boolean loggedIn = false;
	private String username = "";
	private String password = "";
	private int userID;
	
	public CurrentUser() {}
	
	public boolean isSuperUser() {
		return superUser;
	}
	public void setSuperUser(boolean superUser) {
		this.superUser = superUser;
	}
	public boolean isLoggedIn() {
		return loggedIn;
	}
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (loggedIn ? 1231 : 1237);
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + (superUser ? 1231 : 1237);
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
		CurrentUser other = (CurrentUser) obj;
		if (loggedIn != other.loggedIn)
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (superUser != other.superUser)
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
		return "CurrentUser [superUser=" + superUser + ", loggedIn=" + loggedIn + ", username=" + username
				+ ", password=" + password + ", userID=" + userID + "]";
	}
	public CurrentUser(boolean superUser, boolean loggedIn, String username, String password, int userID) {
		super();
		this.superUser = superUser;
		this.loggedIn = loggedIn;
		this.username = username;
		this.password = password;
		this.userID = userID;
	}
	
	
}

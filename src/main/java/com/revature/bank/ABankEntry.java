package com.revature.bank;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import com.revature.connection.ConnectionUtil;

public class ABankEntry {

	public static void main(String[] args) throws SQLException {
		Connection con = ConnectionUtil.getConnection();
		String sql = "Select * FROM champions";
		PreparedStatement ps = con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		
		while(rs.next()) {
			System.out.println(rs.getString("CHAMPION_NAME"));
			
		}
		
		Scanner userInput = new Scanner(System.in);
		AUserSpeaker currentUser = new AUserSpeaker();

		while(!currentUser.correctPassword) {
			currentUser.testIfUser(userInput);
			currentUser.login(userInput);
			if (!currentUser.correctPassword) {
				currentUser = new AUserSpeaker();
			}
		}

		if (currentUser.IsSuperUser()) {
			System.out.println("Welcome, super user!!");
		}

		userInput.close();
	}

	static String listen(Scanner sc) {
		outerloop: while(true) {
			try {
				String inputString = AUserSpeaker.normalize(sc.nextLine());
				switch(inputString) {
				case "method1":
					break;
				default: throw new IOException();
				}
				break outerloop;
			}
			catch(IOException E) {
				System.out.println("I can't hear you. Try again or 'cancel'.");
			}
		}

	return "";

	}
}

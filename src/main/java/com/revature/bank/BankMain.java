package com.revature.bank;

import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.connection.ConnectionUtil;

public class BankMain {
	private static final Logger log = LogManager.getLogger(ConnectionUtil.class);


	public static void main(String[] args) {
		log.trace("MAIN METHOD START");
		Scanner scanner = new Scanner(System.in);
		while(true) {
			try {
				Speaker speaker = Speaker.getSpeaker();
				speaker.start(scanner);
			} catch(CancelException e) {
				System.out.println("Logged out. Goodbye.\n");
			}
		}
	}
}
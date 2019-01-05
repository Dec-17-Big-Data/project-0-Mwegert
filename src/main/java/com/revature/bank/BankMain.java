package com.revature.bank;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.connection.ConnectionUtil;
import com.revature.services.TransactionService;

public class BankMain {
	private static final Logger log = LogManager.getLogger(ConnectionUtil.class);


	public static void main(String[] args) {
		log.trace("MAIN METHOD START");
//		try {
//			Speaker speaker = Speaker.getSpeaker();
//			speaker.start();
//		} catch(CancelException e) {
//			System.out.println("Logged out. Goodbye.");
//		}
		System.out.println(TransactionService.getService().getTransactionsByAccount(41).get());
	}
}
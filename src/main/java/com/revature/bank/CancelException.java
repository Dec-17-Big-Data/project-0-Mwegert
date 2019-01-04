package com.revature.bank;

import java.io.IOException;

public class CancelException extends IOException {
	public CancelException(String string) {
		super(string);
	}
	
	public CancelException() {
		super();
	}

	private static final long serialVersionUID = 7887340448748850782L;
}

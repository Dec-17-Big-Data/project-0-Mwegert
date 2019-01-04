package com.testing;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ThrowExceptions{
	public static void main() throws Exception{
		try {
			throw new IOException();
		} catch(IOException e) {
			throw new FileNotFoundException();
		}
		finally {
			throw new Exception();
		}
	}
}

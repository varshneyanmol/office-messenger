package com.app.server;

public class Test {

	public static void main(String[] args) {
		String msg = "111/i/Anmol/i/Developer";
		String[] arr = msg.split("/i/");
		System.out.println("length: " + arr.length);
		for (int i = 0; i < arr.length; i++) {
			System.out.println("arr[" + i + "]" + arr[i]);
		}
	}

}

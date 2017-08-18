package com.app.client.test;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPDemo {
	public static void main(String[] args) {
		File file = new File("src/com/app/client/resources/birthdayMessage.webm");
		byte[] mybytearray = new byte[(int) file.length()];
		System.out.println("FILE LENGTH AT CLIENT SIDE: " + file.length());
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedInputStream bis = new BufferedInputStream(fis);
		try {
			bis.read(mybytearray, 0, mybytearray.length);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println("Sending demo file of(" + mybytearray.length + " bytes)");
		Socket socket = null;
		try {
			socket = new Socket("localhost", 7890);
			System.out.println("CLIENT TCP socket running");
		} catch (UnknownHostException e) {
			System.out.println("Could not open CLIENT TCP socket aat port 7890");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not open CLIENT TCP socket at port 7890");
			e.printStackTrace();
		}

		try {
			DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
			outToServer.write(mybytearray, 0, mybytearray.length);
			// outToServer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

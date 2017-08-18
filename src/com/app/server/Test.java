package com.app.server;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Test {
	public static void main(String[] args) {
		ServerSocket serverSocket;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(7890);
			socket = serverSocket.accept();
			System.out.println("TCP socket running");
		} catch (IOException e) {
			System.out.println("Could not open TCP socket at port 7890");
			e.printStackTrace();
		}

		// InputStreamReader isr = null;
		// try {
		// isr = new InputStreamReader(socket.getInputStream());
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }
		// BufferedReader br = new BufferedReader(isr);
		// String received = null;
		// try {
		// received = br.readLine();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		byte[] mybytearray = new byte[3500000];
		FileInputStream is;
		try {
			is = (FileInputStream) socket.getInputStream();
			// FileOutputStream fos = new
			// FileOutputStream("src/com/app/client/resources/demoFile1.txt");
			FileOutputStream fos = new FileOutputStream("/home/hduser/Documents/video.webm");
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			int bytesRead = is.read(mybytearray, 0, mybytearray.length);

			int current = bytesRead;
			do {
				bytesRead = is.read(mybytearray, current, mybytearray.length - current);
				if (bytesRead >= 0) {
					current = current + bytesRead;
				}
			} while (bytesRead > -1);

			bos.write(mybytearray, 0, current);
			bos.flush();

			System.out.println("File received server.");

		} catch (IOException e) {
			e.printStackTrace();
		}

		// System.out.println("message received at SERVER: " + received);
	}
}

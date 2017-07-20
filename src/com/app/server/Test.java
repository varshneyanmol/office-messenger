package com.app.server;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.ResourceBundle;

public class Test {
	public static void main(String[] args) {

		String name = "/r/clientUserName/i/groupID";
		String[] arr = name.split("/r/|/i/");
		System.out.println(arr.length);
		for (int i = 0; i < arr.length; i++) {
			System.out.println(arr[i]);
		}

		// Properties properties = new Properties();
		// properties.setProperty("uname", "Anmol");
		// properties.setProperty("password", "anaconda");
		// try {
		// OutputStream outputStream = new
		// FileOutputStream("src/com/app/myProp.properties");
		// properties.store(outputStream, "storing into PROPEEEEERtY FILE");
		// System.out.println("printed");
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		//
		// ResourceBundle config = ResourceBundle.getBundle("com.app.myProp");
		// System.out.println("uname: " + config.getString("uname"));
		// System.out.println("password: " + config.getString("password"));

		// InetAddress ip = null;
		// try {
		// ip = InetAddress.getByName("127.0.0.1");
		// } catch (UnknownHostException e) {
		// e.printStackTrace();
		// }
		// System.out.println("InetAddress: " + ip.getHostAddress());

	}
}

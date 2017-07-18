package com.app.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class ServerMain {
	private Server server;

	public ServerMain(InetAddress ip, int port) {
		server = new Server(ip, port);
	}

	public static void main(String[] args) {
		InetAddress ip = null;
		int port;

		/**
		 * Either Administrator needs to input both <IP> and <port> or else
		 * server will start on default IP and default port.
		 */
		if (args.length > 2) {
			System.out.println("usage: java -jar ChatServer.jar [IP] [port]");
			return;

		} else if (args.length < 2) {
			ResourceBundle config = ResourceBundle.getBundle("com.app.config");
			try {
				ip = InetAddress.getByName(config.getString("default-server-ip"));
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			port = Integer.parseInt(config.getString("default-server-port"));

		} else {
			try {
				ip = InetAddress.getByName(args[0]);
			} catch (UnknownHostException e) {
				System.out.println("IP format:  x.x.x.x");
				e.printStackTrace();
			}
			port = Integer.parseInt(args[1]);
		}
		new ServerMain(ip, port);
	}
}

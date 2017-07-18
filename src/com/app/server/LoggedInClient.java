package com.app.server;

import java.net.InetAddress;

public class LoggedInClient {
	RegisteredClient client;
	InetAddress ip;
	int port;

	public LoggedInClient(RegisteredClient client, InetAddress ip, int port) {
		this.client = client;
		this.ip = ip;
		this.port = port;
	}
}

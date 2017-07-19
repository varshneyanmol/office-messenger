package com.app.server;

import java.net.InetAddress;

public class LoggedInClient {
	private RegisteredClient client;
	private InetAddress ip;
	private int port;

	public LoggedInClient(RegisteredClient client, InetAddress ip, int port) {
		this.client = client;
		this.ip = ip;
		this.port = port;
	}

	public RegisteredClient getClient() {
		return client;
	}

	public InetAddress getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

}

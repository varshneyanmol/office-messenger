package com.app.server;

import java.net.InetAddress;

public class LoggedInClient {
	private RegisteredClient client;
	private InetAddress ip;
	private int port;
	private int attempts;

	public LoggedInClient(RegisteredClient client, InetAddress ip, int port) {
		this.client = client;
		this.ip = ip;
		this.port = port;
		this.attempts = 0;
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

	public int getAttempts() {
		return this.attempts;
	}

	public void increaseAttempts() {
		this.attempts++;
	}

	public void resetAttempts() {
		this.attempts = 0;
	}
}

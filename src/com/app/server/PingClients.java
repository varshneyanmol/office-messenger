package com.app.server;

import java.util.ArrayList;
import java.util.ResourceBundle;

public class PingClients {

	private static PingClients pingClients = null;
	private Server server;

	private ArrayList<LoggedInClient> loggedInClients;
	private ArrayList<String> pings;

	private ResourceBundle config = ResourceBundle.getBundle("com.app.config");
	private String pingIdentifier = config.getString("ping-identifier");
	private int maxPingAttempsIdentifier = Integer.parseInt(config.getString("max-ping-attempts-identifier"));
	private String pingMessage = pingIdentifier;

	private PingClients(Server server) {
		this.server = server;
		this.pings = new ArrayList<String>();
	}

	public static PingClients getPingClients(Server server) {
		if (pingClients == null) {
			pingClients = new PingClients(server);
		}
		return pingClients;
	}

	public void ping() {
		loggedInClients = server.getLoggedInClients();
		for (int i = 0; i < loggedInClients.size(); i++) {
			LoggedInClient client = loggedInClients.get(i);
			server.send(pingMessage, client.getIp(), client.getPort());
		}

	}

	public void add(String clientID) {
		if (!pings.contains(clientID)) {
			pings.add(clientID);
		}
	}

	public void check() {
		loggedInClients = server.getLoggedInClients();
		for (int i = 0; i < loggedInClients.size(); i++) {
			LoggedInClient client = loggedInClients.get(i);
			if (pings.contains(client.getClient().getId())) {
				pings.remove(client.getClient().getId());
				client.resetAttempts();

			} else {
				if (client.getAttempts() < maxPingAttempsIdentifier) {
					client.increaseAttempts();
				} else {
					server.logout(client, true);
				}
			}
		}
	}
}

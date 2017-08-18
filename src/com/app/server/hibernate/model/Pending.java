package com.app.server.hibernate.model;

public class Pending {
	private long id;
	private RegisteredClient client;
	// private Message message;

	// public enum Discriminator {
	// PRIVATE, GROUP
	// }

	// private Discriminator discriminator;

	public Pending() {

	}

	public Pending(RegisteredClient client) {
		this.client = client;
		// this.message = message;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public RegisteredClient getClient() {
		return client;
	}

	public void setClient(RegisteredClient client) {
		this.client = client;
	}

	// public Message getMessage() {
	// return message;
	// }
	//
	// public void setMessage(Message message) {
	// this.message = message;
	// }
	//
	// public Discriminator getDiscriminator() {
	// return discriminator;
	// }
	//
	// public void setDiscriminator(Discriminator discriminator) {
	// this.discriminator = discriminator;
	// }

}

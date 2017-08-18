package com.app.server.hibernate.model;

import java.util.Date;

public class Message {
	private long id;
	private Message parent;
	private String body;
	private Date time;
	private RegisteredClient sender;

	public Message() {

	}

	public Message(String body, Date time, RegisteredClient sender) {
		this.body = body;
		this.time = time;
		this.sender = sender;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Message getParent() {
		return parent;
	}

	public void setParent(Message parent) {
		this.parent = parent;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public RegisteredClient getSender() {
		return sender;
	}

	public void setSender(RegisteredClient sender) {
		this.sender = sender;
	}
}

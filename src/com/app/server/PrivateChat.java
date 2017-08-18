package com.app.server;

import java.util.Date;

import com.app.server.hibernate.model.RegisteredClient;

public class PrivateChat {
	private String id;
	private RegisteredClient client1;
	private RegisteredClient client2;
	private Date createTime;

	public PrivateChat() {
	}

	public PrivateChat(String id, RegisteredClient client1, RegisteredClient client2, Date createTime) {
		this.id = id;
		this.client1 = client1;
		this.client2 = client2;
		this.createTime = createTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public RegisteredClient getClient1() {
		return client1;
	}

	public void setClient1(RegisteredClient client1) {
		this.client1 = client1;
	}

	public RegisteredClient getClient2() {
		return client2;
	}

	public void setClient2(RegisteredClient client2) {
		this.client2 = client2;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}

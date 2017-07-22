package com.app.server;

public class RegisteredClient {
	private String id;
	private String name;
	private String designation;
	private String userName;
	private String password;

	public RegisteredClient() {

	}

	public RegisteredClient(String id, String name, String designation, String userName, String password) {
		this.id = id;
		this.name = name;
		this.designation = designation;
		this.userName = userName;
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}

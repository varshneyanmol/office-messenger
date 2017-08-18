package com.app.server.hibernate.model;

import java.util.Date;
import java.util.Set;

import com.app.server.hibernate.model.Group;

public class RegisteredClient {
	private String id;
	private String name;
	private String designation;
	private String userName;
	private String password;
	private Date createTime;

	private Set<Group> groups;

	public RegisteredClient() {

	}

	public RegisteredClient(String id, String name, String designation, String userName, String password,
			Date createTime) {
		this.id = id;
		this.name = name;
		this.designation = designation;
		this.userName = userName;
		this.password = password;
		this.createTime = createTime;
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

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}

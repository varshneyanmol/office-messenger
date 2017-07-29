package com.app.server;

import java.util.Date;
import java.util.Set;

public class Group {
	private int id;
	private String name;
	private Date createTime;
	private Set<RegisteredClient> members;

	private RegisteredClient creator;

	public Group() {

	}

	public Group(int id, String name, Date createTime) {
		super();
		this.id = id;
		this.name = name;
		this.createTime = createTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RegisteredClient getCreator() {
		return creator;
	}

	public void setCreator(RegisteredClient creator) {
		this.creator = creator;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Set<RegisteredClient> getMembers() {
		return members;
	}

	public void setMembers(Set<RegisteredClient> members) {
		this.members = members;
	}
}

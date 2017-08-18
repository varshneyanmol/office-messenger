package com.app.server.hibernate.model;

import java.util.Date;

public class Central {
    private int id;
    private String fileName;
    private RegisteredClient sender;
    private Date uploadTime;

    public Central() {
    }

    public Central(String fileName, RegisteredClient sender, Date uploadTime) {
        this.fileName = fileName;
        this.sender = sender;
        this.uploadTime = uploadTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public RegisteredClient getSender() {
        return sender;
    }

    public void setSender(RegisteredClient sender) {
        this.sender = sender;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }
}

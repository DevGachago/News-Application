package com.example.newsapplication.models;

import java.util.HashMap;

public class Users {
    private String userId;
    private String username;
    private String phoneNumber;
    private String email;
    private boolean isVerified; // Add this field
    private HashMap<String, Object> newsIds;

    private long joinDate;

    public Users() {
        // Default constructor required for Firebase deserialization
    }

    public Users(String userId, String username, String phoneNumber, String email) {
        this.userId = userId;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.newsIds = new HashMap<>();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(long joinDate) {
        this.joinDate = joinDate;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public HashMap<String, Object> getNewsIds() {
        return newsIds;
    }

    public void addNewsId(String newsId) {
        newsIds.put(newsId, true);
    }
}

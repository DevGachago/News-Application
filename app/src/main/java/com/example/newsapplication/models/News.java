package com.example.newsapplication.models;

public class News {
    private String id;
    private String category;
    private String title;
    private String content;
    private String datePosted;
    private String owner;
    private String username; // New field
    private String imageUrl;

    public News() {
        // Default constructor required for Firebase
    }

    public News(String id, String category, String title, String content, String datePosted, String owner, String username, String imageUrl) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.content = content;
        this.datePosted = datePosted;
        this.owner = owner;
        this.username = username;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

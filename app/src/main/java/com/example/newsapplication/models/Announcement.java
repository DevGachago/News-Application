package com.example.newsapplication.models;

public class Announcement {
    private String id;
    private final String title;
    private final String description;
    private String datePosted;
    private String mediaUrl;
    private boolean isApproved; // Add this field
    private boolean isImage;
    private int viewsCount;
    private String contacts;



    public Announcement(String title, String description, String contacts, String datePosted, String mediaUrl, boolean isImage, int viewsCount) {
        this.title = title;
        this.description = description;
        this.mediaUrl = mediaUrl;
        this.isImage = isImage;
        this.viewsCount = viewsCount;
        this.contacts = contacts;
        this.datePosted = datePosted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }
    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        isImage = image;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean Approved) {
        isApproved = Approved;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }
}


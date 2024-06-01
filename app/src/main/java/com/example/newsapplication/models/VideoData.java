package com.example.newsapplication.models;

public class VideoData {
    private String id;
    private String userId;
    private String title;
    private String description;
    private String location;
    private String date;
    private String streamUrl;
    private String videoUrl;

    private int playbackPosition; // Add this field

    public VideoData() {
        // Default constructor required for Firebase
    }

    public VideoData(String id, String userId, String title, String description, String location, String date, String streamUrl, String videoUrl, int playbackPosition) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
        this.streamUrl = streamUrl;
        this.videoUrl = videoUrl;
        this.playbackPosition = playbackPosition;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setPlaybackPosition(int playbackPosition) {
        this.playbackPosition = playbackPosition;
    }

    public int getPlaybackPosition() {
        return playbackPosition;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}

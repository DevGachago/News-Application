package com.example.newsapplication.models;

public class Messages {
    private String messageId;
    private String messageContent;
    private String senderName;
    private long timestamp;
    private String messageType;

    public Messages() {
        // Default constructor required for Firebase Realtime Database
    }

    public Messages(String messageId, String messageContent, String senderName, long timestamp, String messageType) {
        this.messageId = messageId;
        this.messageContent = messageContent;
        this.senderName = senderName;
        this.timestamp = timestamp;
        this.messageType = messageType;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}

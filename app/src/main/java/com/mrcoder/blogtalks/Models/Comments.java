package com.mrcoder.blogtalks.Models;

import com.google.firebase.database.ServerValue;

public class Comments {

    private String content, userId, userImage, userName;
    private Object timeStamp;

    public Comments(String content, String userId, String userImage, String userName) {
        this.content = content;
        this.userId = userId;
        this.userImage = userImage;
        this.userName = userName;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public Comments(String content, String userId, String userImage, String userName, Object timeStamp) {
        this.content = content;
        this.userId = userId;
        this.userImage = userImage;
        this.userName = userName;
        this.timeStamp = timeStamp;
    }

    public Comments() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}

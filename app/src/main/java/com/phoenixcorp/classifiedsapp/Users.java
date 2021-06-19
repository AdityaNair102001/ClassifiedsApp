package com.phoenixcorp.classifiedsapp;

public class Users {
    String Uid;
    String userName;
    String userEmail;
    String imageURI;

    public Users(){}

    public Users(String uid, String userName, String userEmail, String imageURI) {
        Uid = uid;
        this.userName = userName;
        this.userEmail = userEmail;
        this.imageURI = imageURI;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }
}

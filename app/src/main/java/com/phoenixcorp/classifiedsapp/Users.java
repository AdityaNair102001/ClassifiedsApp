package com.phoenixcorp.classifiedsapp;

public class Users {
    String Uid;
    String userName;
    String userEmail;
    long phoneNo;
    String imageURI;

    public Users(){}

    public Users(String uid, String userName, String userEmail, long phoneNo, String imageURI) {
        Uid = uid;
        this.userName = userName;
        this.userEmail = userEmail;
        this.imageURI = imageURI;
        this.phoneNo = phoneNo;
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

    public long getPhoneNo() { return phoneNo;  }

    public void setPhoneNo(long phoneNo) { this.phoneNo = phoneNo;  }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }
}

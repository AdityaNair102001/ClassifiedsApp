package com.phoenixcorp.classifiedsapp;

public class Users {
    String Uid;
    String userName;
    String userEmail;
    String phoneNo;
    String imageURI;

    public Users(){}

    public Users(String uid, String userName, String userEmail,String imageURI, String phoneNo) {
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

    public String getPhoneNo() { return phoneNo;  }

    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo;  }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }
}

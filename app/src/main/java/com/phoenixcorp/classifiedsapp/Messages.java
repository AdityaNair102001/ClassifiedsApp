package com.phoenixcorp.classifiedsapp;

public class Messages {

    String chat;
    String senderID;
    long timeStamp;

    public Messages(){}

    public Messages(String chat, String senderID, long timeStamp) {
        chat.trim();
        this.chat = chat;
        this.senderID = senderID;
        this.timeStamp = timeStamp;
    }


    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

}

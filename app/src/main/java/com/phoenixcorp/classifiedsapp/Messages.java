package com.phoenixcorp.classifiedsapp;

import java.util.Comparator;

public class Messages {

    String receiverName;
    String chat;
    String senderID;
    String imageURI;
    long timeStamp;

    public Messages(){}

    public Messages(String receiverName, String imageURI, String chat, String senderID, long timeStamp) {
        this.receiverName = receiverName;
        this.chat = chat;
        this.senderID = senderID;
        this.timeStamp = timeStamp;
        this.imageURI = imageURI;

    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
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

    public static Comparator<Messages> MessagesComparator = new Comparator<Messages>() {
        @Override
        public int compare(Messages messages, Messages t1) {

            long timeStamp1 = messages.getTimeStamp();
            long timeStamp2 = messages.getTimeStamp();

            return (int) (timeStamp1 - timeStamp2);
        }
    };

}

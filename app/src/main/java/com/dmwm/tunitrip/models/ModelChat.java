package com.dmwm.tunitrip.models;

public class ModelChat {
    String message,receiver,sender,time,isSeen;


    public ModelChat() {

    }

    public String getMessage() {
        return message;
    }

    public ModelChat( String isSeen,String message, String receiver, String sender, String time) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.time = time;
        this.isSeen = isSeen;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String isSeen() {
        return isSeen;
    }

    public void setSeen(String seen) {
        isSeen = seen;
    }
}

package com.example.sipmobileapp.event;

public class RefreshEvent {

    private int attachID;

    public RefreshEvent(int attachID) {
        this.attachID = attachID;
    }

    public int getAttachID() {
        return attachID;
    }

    public void setAttachID(int attachID) {
        this.attachID = attachID;
    }
}

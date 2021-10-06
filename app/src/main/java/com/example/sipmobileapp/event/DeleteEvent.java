package com.example.sipmobileapp.event;

public class DeleteEvent {

    private int attachID;

    public DeleteEvent(int attachID) {
        this.attachID = attachID;
    }

    public int getAttachID() {
        return attachID;
    }

    public void setAttachID(int attachID) {
        this.attachID = attachID;
    }
}

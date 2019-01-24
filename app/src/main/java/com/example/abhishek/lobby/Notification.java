package com.example.abhishek.lobby;



public class Notification {
    String type,lobby_handle,contact,key;

    public void setLobby_handle(String lobby_handle) {
        this.lobby_handle = lobby_handle;
    }

    public String getLobby_handle() {
        return lobby_handle;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContact() {
        return contact;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}

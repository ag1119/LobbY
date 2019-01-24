package com.example.abhishek.lobby;

import java.io.Serializable;

/**
 * Created by Abhishek on 4/4/2018.
 */

public class User_info implements Serializable {
    String lobby_handle,name,contact,imageUri,followRequest;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContact() {
        return contact;
    }

    public void setFollowRequest(String followRequest) {
        this.followRequest = followRequest;
    }

    public String getFollowRequest() {
        return followRequest;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setLobby_handle(String lobby_handle) {
        this.lobby_handle = lobby_handle;
    }

    public String getLobby_handle() {
        return lobby_handle;
    }
}

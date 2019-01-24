package com.example.abhishek.lobby;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Abhishek on 4/16/2018.
 */

public class    NewsFeed implements Serializable{
    String imageUri,lobby_handle,user_profile_image,key,contact,aboutPic,love_count="0",dislike_count="0",pierced_count="0";
    String date,time;
    HashMap<String,Reaction> reactions=new HashMap<>();
    HashMap<String,Comment> comments=new HashMap<>();
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

    public void setUser_profile_image(String user_profile_image) {
        this.user_profile_image = user_profile_image;
    }

    public String getUser_profile_image() {
        return user_profile_image;
    }


    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setContact(String contect) {
        this.contact = contect;
    }

    public String getContact() {
        return contact;
    }

    public void setAboutPic(String aboutPic) {
        this.aboutPic = aboutPic;
    }

    public String getAboutPic() {
        return aboutPic;
    }

    public void setLove_count(String love_count) {
        this.love_count = love_count;
    }

    public String getLove_count() {
        return love_count;
    }

    public void setDislike_count(String dislike_count) {
        this.dislike_count = dislike_count;
    }

    public String getDislike_count() {
        return dislike_count;
    }

    public void setPierced_count(String pierced_count) {
        this.pierced_count = pierced_count;
    }

    public String getPierced_count() {
        return pierced_count;
    }

    public void setReactions(HashMap<String, Reaction> reactions) {
        this.reactions = reactions;
    }

    public HashMap<String, Reaction> getReactions() {
        return reactions;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }
}

class Reaction implements Serializable
{
    String type,lobby_handle,contact;

    public void setLobby_handle(String lobby_handle) {
        this.lobby_handle = lobby_handle;
    }

    public String getLobby_handle() {
        return lobby_handle;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContact() {
        return contact;
    }

}

class Comment implements Serializable{
    String lobby_handle,contact,comment,imageUri;

    public void setLobby_handle(String lobby_handle) {
        this.lobby_handle = lobby_handle;
    }

    public String getLobby_handle() {
        return lobby_handle;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContact() {
        return contact;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageUri() {
        return imageUri;
    }
}
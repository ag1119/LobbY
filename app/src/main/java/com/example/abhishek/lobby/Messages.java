package com.example.abhishek.lobby;

/**
 * Created by Abhishek on 4/26/2018.
 */

public class Messages {

    public String message,from;


    public void setFrom(String type) {
        this.from = type;
    }

    public String getFrom() {
        return from;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

    public  Messages(){

    }

}
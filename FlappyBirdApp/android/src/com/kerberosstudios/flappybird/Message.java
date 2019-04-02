package com.kerberosstudios.flappybird;

/**
 * Created by Zinzano on 3/14/2018.
 * Class the will hold the data for a message list item
 */
public class Message {

    private String message;
    private String username;

    public Message(String m, String u){
        message = m;
        username = u;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

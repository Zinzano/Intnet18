package com.kerberosstudios.flappybird;

/**
 * Created by Zinzano on 3/11/2018.
 * Class the will hold the data for a high score list item
 */
public class HighScoreUser {

    private String name;
    private String highScore;
    private String date;

    public HighScoreUser( String n, String hs, String d){
        name = n;
        highScore = hs;
        date = d;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHighScore() {
        return highScore;
    }

    public void setHighScore(String highScore) {
        this.highScore = highScore;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

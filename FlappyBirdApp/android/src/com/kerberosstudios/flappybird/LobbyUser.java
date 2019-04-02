package com.kerberosstudios.flappybird;

/**
 * Created by Zinzano on 3/9/2018.
 * Class the will hold the data for a lobby user list item
 */
public class LobbyUser
{
    private String name;
    private String currentScore;

    public LobbyUser( String n, String cs){
        name = n;
        currentScore = cs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(String currentScore) {
        this.currentScore = currentScore;
    }
}

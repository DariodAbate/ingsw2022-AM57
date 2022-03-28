package it.polimi.ingsw;

import java.util.ArrayList;

public class Player {
    final private int id;
    private String nickname;
    private ArrayList<AssistantCard> hand = new ArrayList<>();
    private AssistantCard discardCard;
    //private Board Playerboard

    public Player(int id) {
        this.id = id;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

}

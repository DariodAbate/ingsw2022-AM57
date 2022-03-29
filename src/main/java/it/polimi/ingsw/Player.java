package it.polimi.ingsw;

import java.util.ArrayList;

// testing method to be added
public class Player {
    final private int id;
    private String nickname;
    private ArrayList<AssistantCard> hand = new ArrayList<>();
    private AssistantCard discardCard;
    private Board board;

    public Player(int id) {
        //this.board = new Board();  //check Player constructor
        this.id = id;
        for (int i = 0; i < 10; i++) {
            hand.add(i, new AssistantCard(i + 1, (i + 1) / 2 + (i + 1) % 2));
        }
    }

    public void setNickname(String nickname) {
        if (nickname != null)
            this.nickname = nickname;
        else
            throw new NullPointerException();
    }

    public String getNickname() {
        return nickname;
    }

    public void chooseBack(CardBack back) {
        for (int i = 0; i < 10; i ++) {
            hand.get(i).setCardBack(back);
        }
    }

    public ArrayList<AssistantCard> getPlayableCard() {
        return new ArrayList<>(hand);
    }

    public AssistantCard playCard(int chosenCard) {
        discardCard = hand.get(chosenCard);
        AssistantCard temp = hand.get(chosenCard);
        hand.remove(chosenCard);
        return temp;
    }

    public AssistantCard viewLastCard() {
        return discardCard;
    }

}



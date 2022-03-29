package it.polimi.ingsw;

import java.util.ArrayList;


public class Player {
    final private int id;
    private String nickname;
    private ArrayList<AssistantCard> hand = new ArrayList<>();
    private AssistantCard discardCard;
    private Board board;

    public Player(int id /*int numPlayer*/) {
        //this.board = new Board(numPlayer);
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

    // Coin management to be implemented
}



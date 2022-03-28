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
        //this.board = new Board();
        this.id = id;
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
        for (int i = 0; i < 10; i++) {
            hand.add(i, new AssistantCard(i + 1, (i + 1) / 2 + (i + 1) % 2, back));
        }
    }

    public AssistantCard playCard(int chosenCard) throws AlreadyPlayedCard {
        if (!hand.get(chosenCard).getHasBeenPlayed()) {
            hand.get(chosenCard).setHasBeenPlayed();
            discardCard = (hand.get(chosenCard));
            return hand.get(chosenCard);
        } else {
            throw new AlreadyPlayedCard();
        }
    }

    public AssistantCard viewLastCard() {
        return discardCard;
    }

    // method added just for testing purpose
    public ArrayList<AssistantCard> getHand() {
        return hand;
    }
}



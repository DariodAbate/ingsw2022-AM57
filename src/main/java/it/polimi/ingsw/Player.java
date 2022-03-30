package it.polimi.ingsw;

import java.util.ArrayList;


public class Player {
    final private int id;
    private String nickname;
    private ArrayList<AssistantCard> hand = new ArrayList<>();
    private AssistantCard discardCard;
    private Board board;
    private int coin;

    public Player(int id /*int numPlayer*/) {
        //this.board = new Board(numPlayer);
        this.id = id;
        for (int i = 0; i < 10; i++) {
            hand.add(i, new AssistantCard(i + 1, (i + 1) / 2 + (i + 1) % 2));
        }
    }

    public void setNickname(String nickname) {
        if (nickname == null)
            throw new NullPointerException();
        else
            this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void chooseBack(CardBack back) {
        if (back == null)
            throw new NullPointerException();
        else
            for (int i = 0; i < 10; i ++) {
                hand.get(i).setCardBack(back);
            }
    }

    public ArrayList<AssistantCard> getPlayableCard() {
        return new ArrayList<>(hand);
    }

    public AssistantCard playCard(int chosenCard) {
        if (chosenCard < 0 || chosenCard > 9) {
            throw new IllegalArgumentException("The card index should be between 0 and 9");
        } else {
            discardCard = hand.get(chosenCard);
            AssistantCard playedCard = new AssistantCard(discardCard.getPriority(), discardCard.getMovement());
            playedCard.setCardBack(discardCard.getCardBack());
            hand.remove(chosenCard);
            return playedCard;
        }
    }

    public AssistantCard viewLastCard() {
        return discardCard;
    }

    // Coin management to be implemented
}



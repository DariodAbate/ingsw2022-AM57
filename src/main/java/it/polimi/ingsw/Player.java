package it.polimi.ingsw;

import java.util.ArrayList;


public class Player {
    private String nickname;
    private ArrayList<AssistantCard> hand = new ArrayList<>();
    private AssistantCard discardCard;
    private Board board;
    private int coin;

    public Player(String nick, int numPlayer) {
        this.board = new Board(numPlayer);
        this.nickname = nick;
        for (int i = 0; i < 10; i++) {
            hand.add(i, new AssistantCard(i + 1, (i + 1) / 2 + (i + 1) % 2));
        }
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

    public AssistantCard playCard(int chosenCard) {
        if (chosenCard < 0 || chosenCard > hand.size()-1) {
            throw new IllegalArgumentException("The card index should be between 0 and the index of the last card");
        } else {
            discardCard = hand.get(chosenCard);
            AssistantCard playedCard = new AssistantCard(discardCard.getPriority(), discardCard.getMovement());
            playedCard.setCardBack(discardCard.getCardBack());
            hand.remove(chosenCard);
            return playedCard;
        }
    }

    public ArrayList<AssistantCard> getPlayableCard() {
        return new ArrayList<>(hand);
    }

    public AssistantCard viewLastCard() {
        return discardCard;
    }

    // Coin management to be implemented
}



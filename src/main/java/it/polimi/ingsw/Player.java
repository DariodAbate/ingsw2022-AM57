package it.polimi.ingsw;

import java.util.ArrayList;

/**
 * The Class Player contains all the information about the player and the methods to actually play the game.
 * In addition to the necessary methods to choose and play the assistant's card the class also contains methods
 * to take new coins and use it as well.
 *
 * @author Luca Bresciani
 */
public class Player {
    final private int id;
    private String nickname;
    private ArrayList<AssistantCard> hand = new ArrayList<>();
    private AssistantCard discardCard;
    private Board board;
    private int coin;


    public Player(int id, int numPlayer) {
        if (numPlayer < 2 || numPlayer > 3)
            throw new IllegalArgumentException("The number of Player should be between 2 and 3");
        board = new Board(numPlayer);
        this.id = id;
        for (int i = 0; i < 10; i++) {
            hand.add(i, new AssistantCard(i + 1, (i + 1) / 2 + (i + 1) % 2));
        }
    }

    public int getId() {
        return id;
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

    public int getNumCoin() {
        return coin;
    }

    public Board getBoard() {
        return board;
    }

     //Coin management to be added
}



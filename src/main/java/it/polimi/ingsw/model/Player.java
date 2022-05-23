package it.polimi.ingsw.model;

import it.polimi.ingsw.model.constantFactory.GameConstants;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * The Player class contains all the information about the player and the methods to actually play the game.
 *
 * @author Luca Bresciani
 */
public class Player implements Serializable {
    private GameConstants gameConstants;
    private String nickname;
    private ArrayList<AssistantCard> hand = new ArrayList<>();
    private AssistantCard discardCard;
    private Board board;
    private int id;

    /**
     * Constructor of the class. It initializes the player attributes and the
     * 10 cards that each player have in their hand. The cards are initialized
     * with a for loop where while the priority increases one by one the movement
     * increases by one point every two cards.
     * @param nick nickname of the player
     * @param gameConstants is the object with all the constants in the game
     * @throws IllegalArgumentException if the number of player isn't 2 or 3
     */
    public Player(String nick, GameConstants gameConstants) {
        this.gameConstants = gameConstants;
        this.board = new Board(gameConstants);
        this.nickname = nick;
        for (int i = 0; i < gameConstants.NUM_ASSISTANT_CARD; i++) {
            hand.add(i, new AssistantCard(i + 1, (i + 1) / 2 + (i + 1) % 2));
        }
    }

    /**
     * Constructor added for testing that identifies each player with an id
     * instead of the nickname.
     * @param id integer that identifies each player
     * @param gameConstants is the object with all the constants in the game
     * @throws IllegalArgumentException if the number of player isn't 2 or 3
     */
    public Player(int id, GameConstants gameConstants) {
        this.board = new Board(gameConstants);
        this.id = id;
        for (int i = 0; i < gameConstants.NUM_ASSISTANT_CARD; i++) {
            hand.add(i, new AssistantCard(i + 1, (i + 1) / 2 + (i + 1) % 2));
        }

    }

    /**
     * Gets the player's id.
     * @return the player's id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the nickname of the player.
     * @return the nickname of the player
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Allow the player to choose his preferred card back between the 4
     * present in the game.
     * @param back is the chosen card back
     */
    public void chooseBack(CardBack back) {
        if (back == null)
            throw new NullPointerException();
        else {
            for (int i = 0; i < gameConstants.NUM_ASSISTANT_CARD; i++) {
                hand.get(i).setCardBack(back);
            }
        }
    }

    /**
     * This method is called when the player have to play a card. The player
     * should provide the index of the card he wants to play. Once played the card
     * is removed from the hand of the player and is added to the discarded cards.
     * @param chosenCard is the index of the card chosen by the player
     * @return the played card
     */
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

    /**
     * Gets the list of the cards that the player haven' t already played.
     * @return the list of cards that the player haven't already played
     */
    public ArrayList<AssistantCard> getPlayableCard() {
        return new ArrayList<>(hand);
    }

    /**
     * Gets a copy of the last discarded card.
     * @return the last discarded card
     */
    public AssistantCard viewLastCard() {
        if(discardCard == null)
            return null;
        else
            return new AssistantCard(discardCard.getPriority(), discardCard.getMovement());
    }

    /**
     * Gets a reference to the player's board.
     * @return the player's board
     */
    public Board getBoard() {
        return board;
    }

    public ArrayList<AssistantCard> getHand() {
        return hand;
    }

    public boolean isPriorityAvailable(int priority){
        boolean isAvailable = false;
        for(AssistantCard card : hand){
            if(card.getPriority() == priority)
                isAvailable= true;
        }
        return isAvailable;
    }
    public int priorityToIndex(int priority){
        int temp = 0;
        for(AssistantCard card : hand){
            if(card.getPriority() == priority){
                temp = hand.indexOf(card);
            }
        }
        return temp;
    }
}



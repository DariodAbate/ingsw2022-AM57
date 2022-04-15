package it.polimi.ingsw.model;

import it.polimi.ingsw.model.constantFactory.GameConstants;

import java.util.ArrayList;
/**
 * The Player class contains all the information about the player and the methods to actually play the game.
 * In addition to the necessary methods to choose and play the assistant's card the class also contains methods
 * to take new coins and use it as well.
 *
 * @author Luca Bresciani
 */
public class Player {
    private GameConstants gameConstants;
    private String nickname;
    private ArrayList<AssistantCard> hand = new ArrayList<>();
    private AssistantCard discardCard;
    private Board board;
    private int coin;
    private int id;
    private boolean addCoinChecker[];

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
        addCoinChecker = new boolean[15];
        coin = 1;
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
        addCoinChecker = new boolean[15];
        coin = 1;
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
        else
            for (int i = 0; i < gameConstants.NUM_ASSISTANT_CARD; i ++) {
                hand.get(i).setCardBack(back);
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
     * Gets the last discarded card.
     * @return the last discarded card
     */
    public AssistantCard viewLastCard() {
        return discardCard;
    }

    /**
     * Gets the number of coin that a player have
     * @return the number of coin that a player have
     */
    public int getNumCoin() {
        return coin;
    }

    /**
     * Gets the player's board.
     * @return the player's board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Check if the condition for adding a coin to the pLayer's reserve is verified.
     * If yes a coin is added to the player's reserve otherwise nothing happen.
     */
    public void addCoin() {
        if ((!addCoinChecker[0]) && ((board.hallSize(Color.YELLOW) == 3))) {
            coin += 1;
            addCoinChecker[0] = true;
        }
        if ((!addCoinChecker[1]) && ((board.hallSize(Color.GREEN) == 3))) {
            coin += 1;
            addCoinChecker[1] = true;
        }
        if ((!addCoinChecker[2]) && ((board.hallSize(Color.BLUE) == 3))) {
            coin += 1;
            addCoinChecker[2] = true;
        }
        if ((!addCoinChecker[3]) && ((board.hallSize(Color.RED) == 3))) {
            coin += 1;
            addCoinChecker[3] = true;
        }
        if ((!addCoinChecker[4]) && ((board.hallSize(Color.PINK) == 3))) {
            coin += 1;
            addCoinChecker[4] = true;
        }
        if ((!addCoinChecker[5]) && ((board.hallSize(Color.YELLOW) == 6))) {
            coin += 1;
            addCoinChecker[5] = true;
        }
        if ((!addCoinChecker[6]) && ((board.hallSize(Color.GREEN) == 6))) {
            coin += 1;
            addCoinChecker[6] = true;
        }
        if ((!addCoinChecker[7]) && ((board.hallSize(Color.BLUE) == 6))) {
            coin += 1;
            addCoinChecker[7] = true;
        }
        if ((!addCoinChecker[8]) && ((board.hallSize(Color.RED) == 6))) {
            coin += 1;
            addCoinChecker[8] = true;
        }
        if ((!addCoinChecker[9]) && ((board.hallSize(Color.PINK) == 6))) {
            coin += 1;
            addCoinChecker[9] = true;
        }
        if ((!addCoinChecker[10]) && ((board.hallSize(Color.YELLOW) == 9))) {
            coin += 1;
            addCoinChecker[10] = true;
        }
        if ((!addCoinChecker[11]) && ((board.hallSize(Color.GREEN) == 9))) {
            coin += 1;
            addCoinChecker[11] = true;
        }
        if ((!addCoinChecker[12]) && ((board.hallSize(Color.BLUE) == 9))) {
            coin += 1;
            addCoinChecker[12] = true;
        }
        if ((!addCoinChecker[13]) && ((board.hallSize(Color.RED) == 9))) {
            coin += 1;
            addCoinChecker[13] = true;
        }
        if ((!addCoinChecker[14]) && ((board.hallSize(Color.PINK) == 9))) {
            coin += 1;
            addCoinChecker[14] = true;
       }
    }

    /**
     * Remove a coin from the player's reserve
     * @throws IllegalStateException if the number of player's coin is 0
     */
    public void removeCoin() {
        if (coin - 1 >= 0)
            coin -= 1;
        else
            throw new IllegalStateException("You didn't have enough coins");
    }
}



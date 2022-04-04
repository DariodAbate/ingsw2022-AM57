package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The Round class incorporates the management of the different pianification and action
 * phases. In particular, it provides the methods to manage the progress of the turns
 * and the tracking of the current player.
 *
 * @author Luca Bresciani
 */
public class Round {
    private int roundNumber;
    private ArrayList<Player> pianificationOrder;
    private ArrayList<Player> actionOrder;
    private ArrayList<Player> playersCopy;
    private Player currentTurn;
    private boolean isPianification;

    /**
     * Constructor of the class. Given the Arraylist of the players that are playing it
     * initializes the actionOrder list and the pianificationOrder list equal to the given
     * parameter list. The constructor also initialize the boolean value isPianification to true
     * becasue each game will start from this phase.
     * @param players is the list of player that are actually playing the game
     * @throws NullPointerException if the list player is null
     * @throws IllegalArgumentException if there are more than 3 player or less than 2
     */
    public Round(ArrayList<Player> players) {
        if (players == null)
            throw new NullPointerException();
        if (players.size() < 2 || players.size() > 3)
            throw new IllegalArgumentException("You need between 2 and 3 players");
        roundNumber = 0;
        playersCopy = new ArrayList<>(players);
        actionOrder = new ArrayList<>(players);
        pianificationOrder = new ArrayList<>(players);
        isPianification = true;
    }

    /**
     * Gets the round number indicating how many rounds the game has had so far.
     * @return the round number
     */
    public int getRoundNumber() {
        return roundNumber;
    }

    /**
     * Gets the list of the players during the pianification phase calculated in accord
     * to the game's rules.
     * @return the list of players during the pianification phase.
     */
    public ArrayList<Player> getPianificationOrder() {
        if (pianificationOrder == null)
            throw new NullPointerException("There is no pianification list yet");
        else
            return pianificationOrder;
    }

    /**
     * Gets the list of the players during the action phase calculated in accord
     * to the game's rules.
     * @return the list of the players during the action phase.
     */
    public ArrayList<Player> getActionOrder() {
        if (actionOrder == null)
            throw new NullPointerException("There is no action list yet");
        else
            return actionOrder;
    }

    /**
     * This method set the pianification phase list of players. At the beginning all element of
     * the previous pianification's list are removed. Then the variable firstPlayer is initialized
     * with the player that has played the card with the lower priority and finally the other players
     * are added to the list simulating a clockwise lap as you would do in the physical game.
     */
    public void setPianificationOrder() {
        pianificationOrder.removeAll(playersCopy);
        Player firstPlayer = Collections.min(playersCopy, (player1, player2) -> {
            if (player1.viewLastCard().getPriority() < player2.viewLastCard().getPriority())
                return -1;
            else
                return 1;
        });
        pianificationOrder.add(firstPlayer);
        int firstPlayerIndex = playersCopy.indexOf(firstPlayer);
        if (playersCopy.size() == 3) {
            pianificationOrder.add(playersCopy.get((firstPlayerIndex +1)%3));
            pianificationOrder.add(playersCopy.get((firstPlayerIndex +2)%3));
        }
        if(playersCopy.size() == 2) {
            pianificationOrder.add(playersCopy.get((firstPlayerIndex +1)%2));
        }
    }

    /**
     * This method set the action phase list of players. The list is sorted from the
     * player which as played the lower priority card to the player which as played the highest
     * priority card. The method also set the boolean variable isPianification to false that
     * indicates the beginning of the action phase.
     */
    // Case of same priority card to be added
    public void setActionOrder() {
        for (Player player : playersCopy) {
            Collections.sort(actionOrder, (player1, player2) -> {
                if (player1.viewLastCard().getPriority() < player2.viewLastCard().getPriority())
                    return -1;
                else
                    return 1;
            });
        }
        isPianification = false;
    }

    /**
     * Gets the current player.
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return currentTurn;
    }

    /**
     * This method modify the current player and indicates the start of a new turn.
     */
    // Tests still to be done
    public void nextTurn() {
        if (isPianification) {
            if (pianificationOrder.iterator().hasNext())
                currentTurn = pianificationOrder.iterator().next();
            else
                throw new IndexOutOfBoundsException("End of pianificationOrder list");
        } else {
            if (actionOrder.iterator().hasNext())
                currentTurn = actionOrder.iterator().next();
            else
                throw new IndexOutOfBoundsException("End of actionOrder list");
        }
    }

    /**
     * This method increase the round number and set the boolean value
     * isPianification to true which indicates the beginning of a new
     * pianification phase.
     */
    public void nextRound() {
        roundNumber += 1;
        isPianification = true;
    }
}


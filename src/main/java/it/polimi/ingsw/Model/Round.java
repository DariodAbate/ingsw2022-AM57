package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Player;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The Round class incorporates the management of the different planning and action
 * phases. In particular, it provides the methods to manage the progress of the turns
 * and the tracking of the current player.
 *
 * @author Luca Bresciani
 */
public class Round {
    private int roundNumber;
    private ArrayList<Player> planningPhaseOrder;
    private ArrayList<Player> actionOrder;
    private ArrayList<Player> playersCopy;
    private Player currentTurn;
    private boolean isPlanning;

    /**
     * Constructor of the class. Given the Arraylist of the players that are playing it
     * initializes the actionOrder list and the planningPhaseOrder list equal to the given
     * parameter list. The constructor also initialize the boolean value isPlanning to true
     * because each game will start from this phase.
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
        planningPhaseOrder = new ArrayList<>(players);
        isPlanning = true;
    }

    /**
     * Gets the round number indicating how many rounds the game has had so far.
     * @return the round number
     */
    public int getRoundNumber() {
        return roundNumber;
    }

    /**
     * Gets the list of the players during the planning phase calculated in accord
     * to the game's rules.
     * @return the list of players during the planning phase.
     */
    public ArrayList<Player> getPlanningPhaseOrder() {
        if (planningPhaseOrder == null)
            throw new NullPointerException("There is no planning list yet");
        else
            return planningPhaseOrder;
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
     * This method set the planning phase list of players. At the beginning all element of
     * the previous planning list are removed. Then the variable firstPlayer is initialized
     * with the player that has played the card with the lower priority and finally the other players
     * are added to the list simulating a clockwise lap as you would do in the physical game.
     */
    public void setPlanningPhaseOrder() {
        planningPhaseOrder.removeAll(playersCopy);
        Player firstPlayer = Collections.min(playersCopy, (player1, player2) -> {
            if (player1.viewLastCard().getPriority() < player2.viewLastCard().getPriority())
                return -1;
            else
                return 1;
        });
        planningPhaseOrder.add(firstPlayer);
        int firstPlayerIndex = playersCopy.indexOf(firstPlayer);
        if (playersCopy.size() == 3) {
            planningPhaseOrder.add(playersCopy.get((firstPlayerIndex +1)%3));
            planningPhaseOrder.add(playersCopy.get((firstPlayerIndex +2)%3));
        }
        if(playersCopy.size() == 2) {
            planningPhaseOrder.add(playersCopy.get((firstPlayerIndex +1)%2));
        }
    }

    /**
     * This method set the action phase list of players. The list is sorted from the
     * player which as played the lower priority card to the player which as played the highest
     * priority card. The method also set the boolean variable isPlanning to false that
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
        isPlanning = false;
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
        if (isPlanning) {
            if (planningPhaseOrder.iterator().hasNext())
                currentTurn = planningPhaseOrder.iterator().next();
            else
                throw new IndexOutOfBoundsException("End of planningPhaseOrder list");
        } else {
            if (actionOrder.iterator().hasNext())
                currentTurn = actionOrder.iterator().next();
            else
                throw new IndexOutOfBoundsException("End of actionOrder list");
        }
    }

    /**
     * This method increase the round number and set the boolean value
     * isPlanning to true which indicates the beginning of a new
     * planning phase.
     */
    public void nextRound() {
        roundNumber += 1;
        isPlanning = true;
    }
}


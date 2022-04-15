package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

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
    private ArrayList<Player> actionPhaseOrder;
    private final ArrayList<Player> playersCopy;
    private Player currentTurn;
    private boolean isPlanning;
    private boolean isEnding;
    /**
     * Constructor of the class. Given the Arraylist of the players that are playing it
     * initializes the actionPhaseOrder list and the planningPhaseOrder list equal to the given
     * parameter list. The constructor also initialize the boolean value isPlanning to true
     * because each game will start from this phase.
     * @param players is the list of player that are actually playing the game
     */
    public Round(ArrayList<Player> players) {
        roundNumber = 0;
        playersCopy = players;
        actionPhaseOrder = new ArrayList<>(players);
        planningPhaseOrder = new ArrayList<>(players);
        isPlanning = true;
        // Add RNG to pick the first player TODO
        currentTurn = players.get(0);
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
    public ArrayList<Player> getActionPhaseOrder() {
        if (actionPhaseOrder == null)
            throw new NullPointerException("There is no action list yet");
        else
            return actionPhaseOrder;
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
            planningPhaseOrder.add(playersCopy.get((firstPlayerIndex + 1) % 3));
            planningPhaseOrder.add(playersCopy.get((firstPlayerIndex + 2) % 3));
        }
        if(playersCopy.size() == 2) {
            planningPhaseOrder.add(playersCopy.get((firstPlayerIndex + 1) % 2));
        }
        currentTurn = planningPhaseOrder.get(0);
    }

    /**
     * This method set the action phase list of players. The list is sorted from the
     * player which as played the lower priority card to the player which as played the highest
     * priority card. The method also set the boolean variable isPlanning to false that
     * indicates the beginning of the action phase.
     */
    // Case of same priority card to be added
    public void setActionPhaseOrder() {
        for (Player player : playersCopy) {
            actionPhaseOrder.sort((player1, player2) -> {
                if (player1.viewLastCard().getPriority() < player2.viewLastCard().getPriority())
                    return -1;
                else
                    return 1;
            });
        }
        currentTurn = actionPhaseOrder.get(0);
        isPlanning = false;
    }

    /**
     * Gets the current player.
     * @return the reference to the current player
     */
    public Player getCurrentPlayer() {
        return currentTurn;
    }

    /**
     * Private method that calculate the currentTurn player index inside the
     * planning phase list or inside the action phase list depending on which phase
     * the game is.
     * @return the index of the currentTurn player inside the planningPhaseOrder list
     * or actionPhaseOrder list
     */
    private int getCurrentPlayerIndex(){
     if (isPlanning)
         return planningPhaseOrder.indexOf(currentTurn);
     else
         return actionPhaseOrder.indexOf(currentTurn);
    }

    /**
     * This method modify the current player and indicates the start of a new turn.
     */
    public void nextTurn() {

        /*  TODO
         if (!planningIterator.hasNext())
             setActionPhaseOrder();
         if (!actionIterator.hasNext())
             nextRound();
         planningIterator.next();
         actionIterator.next();
        */
        if (isPlanning) {
            currentTurn = planningPhaseOrder.get(getCurrentPlayerIndex() + 1);
        } else {
            currentTurn = actionPhaseOrder.get(getCurrentPlayerIndex() + 1);
        }
    }

    /**
     * This method increase the round number and set the boolean value
     * isPlanning to true which indicates the beginning of a new
     * planning phase.
     */
    public void nextRound() {
        if(isEnding){ //if isEnding is true, calls the end of the game
            //endgame();
            return;
        }
        roundNumber += 1;
        isPlanning = true;
        setPlanningPhaseOrder();
    }

    //@author Lorenzo Corrado
    public void setIsEnding(boolean isEnding) { //this method sets a condition for the endgame
         this.isEnding = isEnding;
    }
}


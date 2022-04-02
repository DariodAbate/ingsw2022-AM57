package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Round {
    private int roundNumber;
    private ArrayList<Player> pianificationOrder;
    private ArrayList<Player> actionOrder;
    private ArrayList<Player> playersCopy;
    private Player currentTurn;
    private boolean isPianification;


    public Round(ArrayList<Player> players) {
        if (players == null)
            throw new NullPointerException();
        if (players.size() < 2 || players.size() > 3)
            throw new IllegalArgumentException("You need between 2 and 3 players");
        roundNumber = 0;
        playersCopy = new ArrayList<>(players);
        actionOrder = new ArrayList<>(players);
        isPianification = true;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public ArrayList<Player> getPianificationOrder() {
        if (pianificationOrder == null)
            throw new NullPointerException("There is no pianification list yet");
        else
            return pianificationOrder;
    }

    public ArrayList<Player> getActionOrder() {
        if (actionOrder == null)
            throw new NullPointerException("There is no action list yet");
        else
            return actionOrder;
    }

    public void setPianificationOrder() {

    }

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

    public Player getCurrentPlayer() {
        return currentTurn;
    }

    public void nextTurn() {
        if (isPianification) {
            currentTurn = pianificationOrder.iterator().next();  // Controllo sempre il fatto della lista circolare
        } else {
            // controllare che la lista non sia finita
            currentTurn = actionOrder.iterator().next();
        }
    }

    public void nextRound() {
        roundNumber += 1;
        isPianification = true;
    }

    public Player returnMinPrio() {
        Player temp =  Collections.min(playersCopy, Comparator.comparing(Player::getId));
        return temp;
    }
}


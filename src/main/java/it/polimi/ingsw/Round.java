package it.polimi.ingsw;

import java.util.ArrayList;

public class Round {
    private int roundNumber;
    private ArrayList<Player> pianificationOrder;
    private ArrayList<Player> actionOrder;
    private ArrayList<Player> players;
    private Player currentTurn;
    private boolean isPianification;

    public void Round(ArrayList<Player> players) {
        if (players == null)
            throw new NullPointerException();
        if (players.size() < 2 || players.size() > 3)
            throw new IllegalArgumentException("You need between 2 and 3 players");
        roundNumber = 0;
        players = new ArrayList<>(players);
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
}


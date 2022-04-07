package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Player;

import java.util.ArrayList;

public class RoundStub {
    private ArrayList<Player> players;

    public RoundStub(ArrayList<Player> players){ this.players = players;}

    /**
     * @return the first element of the player's list
     */
    public Player getCurrentPlayer(){ return players.get(0);}
}

package it.polimi.ingsw.server;

import java.util.ArrayList;

public class GameHandler {
    private final int numPlayer;
    private ArrayList<int> playersConnections;
    public GameHandler(int numPlayer) {
        this.numPlayer = numPlayer;
    }

    public int getNumPlayer() {
        return numPlayer;
    }

}

package it.polimi.ingsw.network.server;

import java.util.ArrayList;

//tutto synchrionized
public class GameHandler {
    private final int numPlayer;
    public GameHandler(int numPlayer, boolean expertGame, ArrayList<ServerClientHandler> clientConnectionList) {
        this.numPlayer = numPlayer;
    }

    public int getNumPlayer() {
        return numPlayer;
    }

}

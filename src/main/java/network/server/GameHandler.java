package network.server;

//tutto synchrionized
public class GameHandler {
    private final int numPlayer;
    public GameHandler(int numPlayer) {
        this.numPlayer = numPlayer;
    }

    public int getNumPlayer() {
        return numPlayer;
    }

}

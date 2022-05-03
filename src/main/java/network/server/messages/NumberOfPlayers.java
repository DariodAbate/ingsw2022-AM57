package network.server.messages;

/**
 *  This class is a message client to server that provides the number of players chosen by the first player
 *  @author Lorenzo Corrado
 */
public class NumberOfPlayers implements Message {
    private final int numPlayer;

    public NumberOfPlayers(int num){
        this.numPlayer = num;
    }

    /**
     *
     * @return the number of players
     */
    public int getNumPlayer() {
        return numPlayer;
    }

}

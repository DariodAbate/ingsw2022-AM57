package it.polimi.ingsw.network.server.answers;

public class WinningAnswer implements Answer{
    private final String winner;

    public WinningAnswer(String winner){
        this.winner = winner;
    }

    /**
     *
     * @return nickname of winner
     */
    @Override
    public String getMessage() {
        return winner;
    }

}

package it.polimi.ingsw.model.constantFactory;

/**
 * This class is a concrete implementation of the interface GameConstants.
 * The methods return constants for a 2-player game
 * @author Dario d'Abate
 */
public class TwoPlayersConstants implements GameConstants{

    @Override
    public int getEntranceSize() {
        return 7;
    }

    @Override
    public int getNumTowersOnBoard() {
        return 8;
    }

    @Override
    public int getNumClouds() {
        return 2;
    }

    @Override
    public int getNumStudentsOnCloud() {
        return 3;
    }

    @Override
    public int getMaxCoinSize(){ return 18;}


}

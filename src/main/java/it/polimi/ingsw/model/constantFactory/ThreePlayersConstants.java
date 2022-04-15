package it.polimi.ingsw.model.constantFactory;

/**
 * This class is a concrete implementation of the interface GameConstants.
 * The methods return constants for a 3-player game
 * @author Dario d'Abate
 */
public class ThreePlayersConstants implements GameConstants{
    private final static int ENTRANCE_SIZE = 9;
    private final static int NUM_TOWERS = 6;
    private final static int NUM_CLOUDS = 3;
    private final static int NUM_STUDENTS_CLOUD = 4;
    private final static int MAX_COIN_SIZE = 17;
    @Override
    public int getEntranceSize() {
        return ENTRANCE_SIZE;
    }

    @Override
    public int getNumTowersOnBoard() {
        return NUM_TOWERS;
    }

    @Override
    public int getNumClouds() {return NUM_CLOUDS;}

    @Override
    public int getNumStudentsOnCloud() {
        return NUM_STUDENTS_CLOUD;
    }

    @Override
    public int getMaxCoinSize(){ return MAX_COIN_SIZE;}
}

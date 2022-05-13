package it.polimi.ingsw.model.constantFactory;

import java.io.Serializable;

/**
 * This class is a concrete implementation of the interface GameConstants.
 * The methods return constants for a 2-player game
 * @author Dario d'Abate
 */
public class TwoPlayersConstants implements GameConstants, Serializable {
    private final static int ENTRANCE_SIZE = 7;
    private final static int NUM_TOWERS = 8;
    private final static int NUM_CLOUDS = 2;
    private final static int NUM_STUDENTS_CLOUD = 3;
    private final static int MAX_COIN_SIZE = 18;
    private final static int MAX_NUM_STUD_MOVEMENTS = 3;

    @Override
    public int getEntranceSize() {
        return ENTRANCE_SIZE;
    }

    @Override
    public int getNumTowersOnBoard() { return NUM_TOWERS;}

    @Override
    public int getNumClouds() {
        return NUM_CLOUDS;
    }

    @Override
    public int getNumStudentsOnCloud() {
        return NUM_STUDENTS_CLOUD;
    }

    @Override
    public int getMaxCoinSize(){ return MAX_COIN_SIZE;}

    @Override
    public int getMaxNumStudMovements() {
        return MAX_NUM_STUD_MOVEMENTS;
    }
}

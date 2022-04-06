package it.polimi.ingsw.model.constantFactory;

/**
 * This class is a concrete implementation of the interface GameConstants.
 * The methods return constants for a 3-player game
 * @author Dario d'Abate
 */
public class ThreePlayersConstants implements GameConstants{
    @Override
    public int getEntranceSize() {
        return 9;
    }

    @Override
    public int getNumTowersOnBoard() {
        return 6;
    }

    @Override
    public int getNumClouds() {
        return 3;
    }

    @Override
    public int getNumStudentsOnCloud() {
        return 4;
    }
}

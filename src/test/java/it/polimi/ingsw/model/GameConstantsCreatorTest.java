package it.polimi.ingsw.model;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.constantFactory.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * This class tests the factory pattern used for handling constants
 *
 * @author Dario d'Abate
 */
class GameConstantsCreatorTest {
    GameConstantsCreator gameConstantsCreator;


    /**
     * This method tests the creation of an object with all the constants
     * for a 2-player game
     */
    @Test
    @DisplayName("Instantiate 2 player-game object with constants test")
    void twoPlayerTest(){
        gameConstantsCreator = new GameConstantsCreatorTwoPlayers();
        GameConstants constants = gameConstantsCreator.create();
        assertEquals(10,constants.HALL_SIZE);
        assertEquals(10,constants.NUM_ASSISTANT_CARD);
        assertEquals(12,constants.INITIAL_ARCHIPELAGO_SIZE);
        assertEquals(26,constants.MAX_SIZE_STUDENT_FOR_COLOR);
        assertEquals(7,constants.getEntranceSize());
        assertEquals(2,constants.getNumClouds());
        assertEquals(3,constants.getNumStudentsOnCloud());
        assertEquals(8,constants.getNumTowersOnBoard());
    }


    /**
     * This method tests the creation of an object with all the constants
     * for a 3-player game
     */
    @Test
    @DisplayName("Instantiate 3 player-game object with constants test")
    void threePlayerTest(){
        gameConstantsCreator = new GameConstantsCreatorThreePlayers();
        GameConstants constants = gameConstantsCreator.create();
        assertEquals(10,constants.HALL_SIZE);
        assertEquals(10,constants.NUM_ASSISTANT_CARD);
        assertEquals(12,constants.INITIAL_ARCHIPELAGO_SIZE);
        assertEquals(26,constants.MAX_SIZE_STUDENT_FOR_COLOR);
        assertEquals(9,constants.getEntranceSize());
        assertEquals(3,constants.getNumClouds());
        assertEquals(4,constants.getNumStudentsOnCloud());
        assertEquals(6,constants.getNumTowersOnBoard());
    }
}
package it.polimi.ingsw.model.constantFactory;

/**
 * This interface is a part of the factory pattern used for managing game's constants.
 * Here we declare constants that are the same for 2-player game or 3-player game, thus the methods
 * that return constants that changes based on the number of players
 *
 * @author Dario d'Abate
 */

public interface GameConstants {
     int HALL_SIZE = 10; //maximum number of students that the board's hall can contain
     int NUM_ASSISTANT_CARD = 10; //number of assistant card initially given to each player
     int INITIAL_ARCHIPELAGO_SIZE = 12; // number of cloudTile that are initially instantiated
     int MAX_SIZE_STUDENT_FOR_COLOR = 26; //number of students stored in studentHandler for each color
     /**
      * @return maximum number of students that the board's entrance can contain
      */
     int getEntranceSize();

     /**
      * @return number of towers initially contained on the board
      */
     int getNumTowersOnBoard();

     /**
      * @return number of clouds for a given game
      */
     int getNumClouds();

     /**
      * @return number of students tha each cloud contains
      */
     int getNumStudentsOnCloud();

     /**
      *
      * @return number of initial coins in the bank
      */
     int getMaxCoinSize();

     /**
      *
      * @return number of students that can be moved in an action phase
      */
     int getMaxNumStudMovements();

}

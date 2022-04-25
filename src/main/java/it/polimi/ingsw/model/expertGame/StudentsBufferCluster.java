package it.polimi.ingsw.model.expertGame;

import it.polimi.ingsw.model.Color;

/**
 * This interface contains methods for moving around students,according to the effect of expert cards.
 * This interface is used by the StudentsBufferCardsCluster class
 */
public interface StudentsBufferCluster {
     Color draw();

     void fromManCardToIsland(int idxChosenIsland, Color colorStudentToBeMoved);

     void fromClownCardToEntrance(Color colorStudentOnCard, Color colorStudentInEntrance);

     boolean fromWomanCardToHall( Color colorStudentToBeMoved);

}

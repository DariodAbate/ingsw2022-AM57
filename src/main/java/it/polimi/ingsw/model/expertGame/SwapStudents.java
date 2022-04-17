package it.polimi.ingsw.model.expertGame;

import it.polimi.ingsw.model.Color;

/**
 * This interface has the purpose of hiding methods and attributes in game, leaving the only useful for this scope.
 */
public interface SwapStudents {
    void swapStudents(Color entranceStudentColor, Color hallStudentColor);
}

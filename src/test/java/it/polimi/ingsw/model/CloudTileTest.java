package it.polimi.ingsw.model;

import it.polimi.ingsw.model.CloudTile;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.StudentsHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class CloudTileTest tests CloudTile class
 *
 * @author Dario d'Abate
 */
class CloudTileTest {

    /**
     * Method that tests fill() when the cloud tile is initially empty.
     * The set should contain the student passed as parameter
     */
    @Test
    @DisplayName("Filling an initially empty cloud test")
    void fill() {
        CloudTile c = new CloudTile(3);
        assertEquals(0, c.numStudOn());
        c.fill(Color.YELLOW);
        assertEquals(1, c.numStudOn(Color.YELLOW));
    }

    /**
     * Method that tests fill() when passing a null color.
     * The set of student on a tile should be unchanged
     * @throws NullPointerException if a null Color is passed
     */
    @Test
    void fillCloudNullColor() {
        CloudTile c = new CloudTile(3);
        assertEquals(0, c.numStudOn());
        assertThrows(NullPointerException.class, () -> c.fill(null));
        assertEquals(0, c.numStudOn());
    }

    /**
     * This method tests fill() when the tile contains the maximum number
     * of students of a determined color, and we try to add a single student
     * of that color.
     * The set of students that initially are on the tile should be unchanged
     */
    @Test
    @DisplayName("Filling a tile with single student and tile initially full of single color test")
    void fillFull1() {
        CloudTile c = new CloudTile(3); //for 3 players the maximum size is 4
        assertEquals(0, c.numStudOn());
        for (int i = 0; i < 4; i++)
            c.fill(Color.YELLOW);
        assertEquals(4, c.numStudOn());

        c.fill(Color.YELLOW);
        assertEquals(4, c.numStudOn(Color.YELLOW));
    }

    /**
     * This method tests fill() when the tile contains the maximum number
     * of students of various color, and we try to add a single student.
     * The set of students that initially are on the tile should be unchanged
     */
    @Test
    @DisplayName("Filling a tile with single student and tile initially full of various color test")
    void fillFull2() {
        CloudTile c = new CloudTile(3); //for 3 players the maximum size is 4
        assertEquals(0, c.numStudOn());
        for (int i = 0; i < 4; i++) {
            if(i < 2)
                c.fill(Color.YELLOW);
            else
                c.fill(Color.GREEN);
        }
        assertEquals(4, c.numStudOn());

        c.fill(Color.YELLOW);
        assertEquals(4, c.numStudOn());
    }


    /**
     * This method tests getTile() when there's a tile that is full.
     * The cloud should be empty after the method call and the set of student
     * has to be obtainable
     */
    @Test
    @DisplayName("Getting a full tile test")
    void getTile(){
        CloudTile c1 = new CloudTile(2);
        for (int i = 0; i < 3; i++) {
            if(i < 2)
                c1.fill(Color.YELLOW);
            else
                c1.fill(Color.GREEN);
        }
        assertEquals(3, c1.numStudOn());

        StudentsHandler s = c1.getTile();
        assertEquals(3, s.numStudents());
        assertEquals(0, c1.numStudOn());
    }

    /**
     * This method tests getTile() when there's a tile that
     * contains at least one student, but it is not full.
     * The set of students on a tile should be unchanged
     * @throws IllegalStateException if the tile is not full
     */
    @Test
    @DisplayName("Getting a tile whe it is not full test")
    void getNotFullTile(){
        CloudTile c = new CloudTile(2);
        c.fill(Color.GREEN);

        assertEquals(1, c.numStudOn());
        assertThrows(IllegalStateException.class, c::getTile);
        assertEquals(1, c.numStudOn());
    }

    /**
     * This method tests getTile() when the tile is empty.
     * The tile should remain empty
     * @throws IllegalStateException if the tile is not full
     */
    @Test
    void getEmptyTile(){
        CloudTile c = new CloudTile(2);

        assertEquals(0, c.numStudOn());
        assertThrows(IllegalStateException.class, c::getTile);
        assertEquals(0, c.numStudOn());
    }
}
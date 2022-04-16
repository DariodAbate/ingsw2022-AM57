package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Bag;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.StudentsHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BagTest {

    /**
     * This method tests all the constructors and tries to draw every single token from a bag filled with different tokens
     */
    @Test
    void draw() {
        Bag bag1 = new Bag(10, 6, 9, 20, 29);
        Bag bag2 = new Bag(10);
        Bag bag3 = new Bag(10);
        int i;
        StudentsHandler colors = new StudentsHandler();
        while (!bag1.isEmpty()) {
            colors.add(bag1.draw());
        }
        assertEquals(10, colors.numStudents((Color.RED)));
        assertEquals(6, colors.numStudents((Color.BLUE)));
        assertEquals(9, colors.numStudents((Color.GREEN)));
        assertEquals(20, colors.numStudents((Color.PINK)));
        assertEquals(26, colors.numStudents((Color.YELLOW)));
        assertNotEquals(29, colors.numStudents((Color.YELLOW)));

        for(i=0; !bag2.isEmpty(); i++){
            bag2.draw();
        }
        assertEquals(0, bag2.size());
        assertEquals(50, i);

        while(!(bag3.draw() == (null))){

        }
        assertNull(bag3.draw());
    }

    /**
     * Tests the method that add to the bag a specified number of students of the same color
     */
    @Test
    void addTest() {
        Bag bag = new Bag(10, 15, 8, 12, 14);
        bag.add(Color.RED,2);
        assertEquals(61, bag.size());
        while (bag.isEmpty()) {
            bag.draw();
        }
        bag.add(Color.YELLOW, 2);
        assertEquals(2, bag.size());
    }

    /**
     * Tests that the method add actually add the right students color even if the bag is not empty
     */
    @Test
    void correctColorSingleAdd() {
        Bag bag = new Bag(10, 6, 9, 20, 12);
        int i;
        bag.add(Color.GREEN, 5);
        bag.add(Color.RED,4);
        StudentsHandler colors = new StudentsHandler();
        while (!bag.isEmpty()) {
            colors.add(bag.draw());
            }
        assertEquals(14, colors.numStudents((Color.RED)));
        assertEquals(6, colors.numStudents((Color.BLUE)));
        assertEquals(14, colors.numStudents((Color.GREEN)));
        assertEquals(20, colors.numStudents((Color.PINK)));
        assertEquals(12, colors.numStudents((Color.YELLOW)));
    }
}
package it.polimi.ingsw.model;

import it.polimi.ingsw.model.constantFactory.GameConstants;
import it.polimi.ingsw.model.constantFactory.GameConstantsCreator;
import it.polimi.ingsw.model.constantFactory.GameConstantsCreatorTwoPlayers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class BoardTest tests Board class
 *
 * @author Dario d'Abate
 */
class BoardTest {
    Board b;
    GameConstantsCreator g;

    @BeforeEach
    void setup() {
        g = new GameConstantsCreatorTwoPlayers();
        GameConstants gc = g.create();
        b = new Board(gc);
    }


    /**
     *Method that tests addProfessor() trying to add
     * to a set an already existing professor
     */
    @Test
    @DisplayName("Adding prof test with an already existing professor")
    void addProfessorDuplication() {
        b.addProfessor(Color.BLUE);
        b.addProfessor(Color.PINK);
        b.addProfessor(Color.RED);
        b.addProfessor(Color.GREEN);
        b.addProfessor(Color.YELLOW);
        b.addProfessor(Color.BLUE);


        assertEquals(5, b.getProfessors().size());
    }

    /**
     * Method that tests addProfessor() trying to pass
     * a null color
     * @throws NullPointerException if a null Color is passed
     */
    @Test
    @DisplayName("Adding prof test with null parameter")
    void addProfessorNullArgument() {
        b.addProfessor(Color.BLUE);
        assertThrows(NullPointerException.class,
                () -> b.addProfessor(null));

    }

    /**
     *Method that tests removeProfessor() trying to remove an
     * existing professor
     */
    @Test
    @DisplayName("Deleting prof test with deletion of existing prof")
    void rmvProfessor() {
        b.addProfessor(Color.BLUE);
        assertEquals(1, b.getProfessors().size());
        b.removeProfessor(Color.BLUE);
        assertEquals(0, b.getProfessors().size());

    }

    /**
     * removeNonExistentProfessor() tests removeProfessor() trying to remove
     * a non-existent professor.
     * The expected behavior is to have an unchanged set of professors
     */
    @Test
    @DisplayName("Removing prof test with deletion of non-existing prof")
    void removeNonExistentProfessor() {
        assertEquals(0, b.getProfessors().size());
        b.removeProfessor(Color.BLUE);
        assertEquals(0, b.getProfessors().size());

    }

    /**
     * removeNullProfessor() tests removeProfessor() trying to pass
     * a null color
     * @throws NullPointerException if a null Color is passed
     */
    @Test
    @DisplayName("Removing prof test when passing null color")
    void removeNullProfessor() {
        assertEquals(0, b.getProfessors().size());
        assertThrows(NullPointerException.class,
                () -> b.removeProfessor(null));

    }

    /**
     * fillEntranceNormal() tests fillEntrance() when the entrance is
     * initially empty and a single student is added
     */
    @Test
    @DisplayName("Filling entrance test with single student and entrance initially empty")
    void fillEntranceNormal(){
        assertEquals(0, b.entranceSize());
        assertEquals(0, b.hallSize());

        b.fillEntrance(Color.RED);
        assertEquals(1, b.entranceSize());
        assertEquals(1, b.entranceSize(Color.RED));
        assertEquals(0, b.hallSize());
    }

    /**
     * fillFullEntrance1() tests fillEntrance() when the entrance contains the
     * maximum number of students of a determined color, and we try to add a
     * single student of that color.
     * The set of entrance's students should be unchanged
     */
    @Test
    @DisplayName("Filling entrance test with single student and entrance initially full of single color")
    void fillFullEntrance1(){
        //for 2 player the maximum number of student in the entrance is 7
        assertEquals(0, b.entranceSize());
        assertEquals(0, b.hallSize());
        for(int j = 0 ; j < 10; j++)
            b.fillEntrance(Color.RED);
        assertEquals(7, b.entranceSize());
        assertEquals(7, b.entranceSize(Color.RED));
        assertEquals(0, b.hallSize());

        b.fillEntrance(Color.RED);
        assertEquals(7, b.entranceSize());
        assertEquals(7, b.entranceSize(Color.RED));
        assertEquals(0, b.hallSize());
    }

    /**
     *
     *fillFullEntrance2() tests fillEntrance() when the entrance contains the
     *maximum number of students of various color, and we try to add a
     *single student of another color.
     *The set of entrance's students should be unchanged
     */
    @Test
    @DisplayName("Filling entrance test with single student and entrance initially full of various color")
    void fillFullEntrance2(){
        //for 2 player the maximum number of student in the entrance is 7
        assertEquals(0, b.entranceSize());
        assertEquals(0, b.hallSize());
        for(int j = 0 ; j < 10; j++) {
            if (j < 3)
                b.fillEntrance(Color.BLUE);
            else
                b.fillEntrance(Color.PINK);
        }
        assertEquals(7, b.entranceSize());
        assertEquals(3, b.entranceSize(Color.BLUE));
        assertEquals(4, b.entranceSize(Color.PINK));
        assertEquals(0, b.hallSize());

        b.fillEntrance(Color.RED);
        assertEquals(7, b.entranceSize());
        assertEquals(3, b.entranceSize(Color.BLUE));
        assertEquals(4, b.entranceSize(Color.PINK));
        assertEquals(0, b.hallSize());
    }


    /**
     *This method tests entranceToHall() when it is possible to move a student
     *from the entrance to the hall
     */
    @Test
    @DisplayName("Moving student inside the board test in normal condition")
    void entranceToHall() {
        for(Color i: Color.values()){
            assertEquals(0, b.entranceSize(i));
            assertEquals(0, b.hallSize(i));

        }

        b.fillEntrance(Color.GREEN);
        for(Color i: Color.values()){
            if(i.equals(Color.GREEN))
                assertEquals(1, b.entranceSize(i));
            else
                assertEquals(0, b.entranceSize(i));
        }
        for(Color i: Color.values()){
            assertEquals(0, b.hallSize(i));
        }

        b.entranceToHall(Color.GREEN);
        for(Color i: Color.values()){
            if(i.equals(Color.GREEN))
                assertEquals(1, b.hallSize(i));
            else
                assertEquals(0, b.hallSize(i));
        }
        for(Color i: Color.values()) {
            assertEquals(0, b.entranceSize(i));
        }
    }

    /**
     *This method tests entranceToHall() trying to move a student of a determined
     *color, but that student does not exist in the entrance.
     *The entrance set of students should be unchanged
     */
    @Test
    @DisplayName("Moving non-existing student inside the board test")
    void emptyEntranceToHall() {
        for(Color i: Color.values()){
            assertEquals(0, b.entranceSize(i));
            assertEquals(0, b.hallSize(i));

        }

        //now "i" have an empty entrance

        b.entranceToHall(Color.GREEN);

        for(Color i: Color.values()) {
            assertEquals(0, b.entranceSize(i));
            assertEquals(0, b.hallSize(i));
        }
    }

    /**
     * This method tests entranceToHall() when it is passed a null color.
     * The sets of students in entrance and in the hall should be unchanged
     * @throws NullPointerException if the color passed is null
     */
    @Test
    @DisplayName("Moving a student of null color inside the board test")
    void entranceToHallNullColor() {
        for(Color i: Color.values()){
            assertEquals(0, b.entranceSize(i));
            assertEquals(0, b.hallSize(i));

        }

        //now "i" have an empty entrance

        b.entranceToHall(null);

        for(Color i: Color.values()) {
            assertEquals(0, b.entranceSize(i));
            assertEquals(0, b.hallSize(i));
        }
    }

    /**
     *This method tests entranceToHall() trying to move a student of a determined color
     *from the entrance to the hall, when the hall of that color already contains
     *the maximum number of students.
     *The sets of students in the entrance and in the hall should be unchanged
     */
    @Test
    @DisplayName("Moving a student from entrance to a full hall test")
    void entranceToHallFullHall() {
        for(Color i: Color.values()){
            assertEquals(0, b.entranceSize(i));
            assertEquals(0, b.hallSize(i));

        }
        for(int j = 0; j < 10 ; j++) {//full green hall
            b.fillEntrance(Color.GREEN);
            b.entranceToHall(Color.GREEN);
        }
        b.fillEntrance(Color.GREEN);

        for(Color i: Color.values()){
            if(i.equals(Color.GREEN)) {
                assertEquals(1, b.entranceSize(i));
                assertEquals(10, b.hallSize(i));
            }
            else {
                assertEquals(0, b.entranceSize(i));
                assertEquals(0, b.hallSize(i));
            }
        }

        b.entranceToHall(Color.GREEN);

        for(Color i: Color.values()) {
            if(i.equals(Color.GREEN)) {
                assertEquals(1, b.entranceSize(i));
                assertEquals(10, b.hallSize(i));
            }
            else {
                assertEquals(0, b.entranceSize(i));
                assertEquals(0, b.hallSize(i));
            }

        }
    }

    /**
     * Tests the correct working of the hallToEntrance method
     */
    @Test
    @DisplayName("Moving a student from hall to entrance")
    void hallToEntranceTest() {
        for (Color i : Color.values()) {
            assertEquals(0, b.entranceSize(i));
            assertEquals(0, b.hallSize(i));
        }
        b.fillEntrance(Color.GREEN);
        b.fillEntrance(Color.YELLOW);
        b.fillEntrance(Color.RED);
        b.fillEntrance(Color.GREEN);
        assertEquals(4, b.entranceSize());
        b.entranceToHall(Color.GREEN);
        b.entranceToHall(Color.YELLOW);
        b.entranceToHall(Color.GREEN);
        assertEquals(0, b.entranceSize(Color.GREEN));
        assertEquals(3, b.hallSize());
        b.hallToEntrance(Color.GREEN);
        assertEquals(1, b.entranceSize(Color.GREEN));
        assertEquals(2, b.hallSize());
    }

    /**
     * Tests that nothing happen if the method hallToEntrance is called
     * with an empty hall
     */
    @Test
    @DisplayName("Moving a student from a empty hall to the entrance")
    void emptyHallToEntrance() {
        for (Color i : Color.values()) {
            assertEquals(0, b.entranceSize(i));
            assertEquals(0, b.hallSize(i));
        }
        b.fillEntrance(Color.PINK);
        b.fillEntrance(Color.YELLOW);
        b.fillEntrance(Color.RED);
        b.fillEntrance(Color.YELLOW);
        assertEquals(4, b.entranceSize());
        b.hallToEntrance(Color.YELLOW);
        assertEquals(0, b.hallSize());
        assertEquals(4, b.entranceSize());
    }

}

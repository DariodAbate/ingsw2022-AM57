package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void testConstructorException() {

        assertThrows(IllegalArgumentException.class,
                () -> {
                    Board b = new Board(5);
                });
    }

    @Test
    void addProfessorDuplication() {
        Board b = new Board(2);
        b.addProfessor(Color.BLUE);
        b.addProfessor(Color.PINK);
        b.addProfessor(Color.RED);
        b.addProfessor(Color.GREEN);
        b.addProfessor(Color.YELLOW);
        b.addProfessor(Color.BLUE);


        assertEquals(5, b.getProfessors().size());
    }

    @Test
    void addProfessorNullArgument() {
        Board b = new Board(2);
        b.addProfessor(Color.BLUE);
        assertThrows(NullPointerException.class,
                () -> {
                    b.addProfessor(null);
                });

    }


    @Test
    void removeProfessor() {
        Board b = new Board(2);
        b.addProfessor(Color.BLUE);
        assertEquals(1, b.getProfessors().size());
        b.removeProfessor(Color.BLUE);
        assertEquals(0, b.getProfessors().size());

    }

    @Test
    void removeNonexistentProfessor() {
        Board b = new Board(2);
        assertEquals(0, b.getProfessors().size());
        b.removeProfessor(Color.BLUE);
        assertEquals(0, b.getProfessors().size());

    }

    @Test
    void removeNullProfessor() {
        Board b = new Board(2);
        assertEquals(0, b.getProfessors().size());
        assertThrows(NullPointerException.class,
                () -> b.removeProfessor(null));

    }

    //testing normal behaviour
    @Test
    void fillEntrance(){
        Board b = new Board(2);
        assertEquals(0, b.entranceSize());
        assertEquals(0, b.hallSize());

        b.fillEntrance(Color.RED);
        assertEquals(1, b.entranceSize());
        assertEquals(1, b.entranceSize(Color.RED));
        assertEquals(0, b.hallSize());
    }

    //full entrance of a single color
    @Test
    void fillFullEntrance1(){
        //for 2 player the maximum number of student in the entrance is 7
        Board b = new Board(2);
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

    //full entrance of variable color
    @Test
    void fillFullEntrance2(){
        //for 2 player the maximum number of student in the entrance is 7
        Board b = new Board(2);
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



    //testing normal behaviour
    @Test
    void entranceToHall() {
        Board b = new Board(2);

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

    @Test
    void entranceToHallEmptyEntrance() {
        Board b = new Board(2);


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

    @Test
    void entranceToHallFullHall() {
        Board b = new Board(2);
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

    @Test
    void entranceToHallNullColor() {
        Board b = new Board(2);

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



}
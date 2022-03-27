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
        b.addProfessor(null);
        assertEquals(1, b.getProfessors().size());

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
        b.removeProfessor(null);
        assertEquals(0, b.getProfessors().size());

    }

    /*
    //TO RUN THESE TEST DELETE COMMENT TAG FOR getEntrance() and getHall()

    //testing normal behaviour
    @Test
    void entranceToHall() {
        Board b = new Board(2);
        StudentsHandler e = b.getEntrance();
        StudentsHandler h = b.getHall();

        for(Color i: Color.values()){
            assertEquals(0, e.numStudents(i));
            assertEquals(0, h.numStudents(i));

        }

        e.add(Color.GREEN);
        for(Color i: Color.values()){
            if(i.equals(Color.GREEN))
                assertEquals(1, e.numStudents(i));
            else
                assertEquals(0, e.numStudents(i));
        }
        for(Color i: Color.values()){
            assertEquals(0, h.numStudents(i));
        }

        b.entranceToHall(Color.GREEN);
        for(Color i: Color.values()){
            if(i.equals(Color.GREEN))
                assertEquals(1, h.numStudents(i));
            else
                assertEquals(0, h.numStudents(i));
        }
        for(Color i: Color.values()) {
            assertEquals(0, e.numStudents(i));
        }
    }

    @Test
    void entranceToHallEmptyEntrance() {
        Board b = new Board(2);
        StudentsHandler e = b.getEntrance();
        StudentsHandler h = b.getHall();

        for(Color i: Color.values()){
            assertEquals(0, e.numStudents(i));
            assertEquals(0, h.numStudents(i));

        }

        //now "i" have an empty entrance

        b.entranceToHall(Color.GREEN);

        for(Color i: Color.values()) {
            assertEquals(0, e.numStudents(i));
            assertEquals(0, h.numStudents(i));
        }
    }

    @Test
    void entranceToHallFullHall() {
        Board b = new Board(2);
        StudentsHandler e = b.getEntrance();
        StudentsHandler h = b.getHall();

        for(Color i: Color.values()){
            assertEquals(0, e.numStudents(i));
            assertEquals(0, h.numStudents(i));

        }

        e.add(Color.GREEN);
        h.add(Color.GREEN, 10); //full green hall
        for(Color i: Color.values()){
            if(i.equals(Color.GREEN)) {
                assertEquals(1, e.numStudents(i));
                assertEquals(10, h.numStudents(i));
            }
            else {
                assertEquals(0, e.numStudents(i));
                assertEquals(0, h.numStudents(i));
            }
        }

        b.entranceToHall(Color.GREEN);

        for(Color i: Color.values()) {
            if(i.equals(Color.GREEN)) {
                assertEquals(1, e.numStudents(i));
                assertEquals(10, h.numStudents(i));
            }
            else {
                assertEquals(0, e.numStudents(i));
                assertEquals(0, h.numStudents(i));
            }

        }
    }

    @Test
    void entranceToHallNullColor() {
        Board b = new Board(2);
        StudentsHandler e = b.getEntrance();
        StudentsHandler h = b.getHall();

        for(Color i: Color.values()){
            assertEquals(0, e.numStudents(i));
            assertEquals(0, h.numStudents(i));

        }

        //now "i" have an empty entrance

        b.entranceToHall(null);

        for(Color i: Color.values()) {
            assertEquals(0, e.numStudents(i));
            assertEquals(0, h.numStudents(i));
        }
    }


     */
}
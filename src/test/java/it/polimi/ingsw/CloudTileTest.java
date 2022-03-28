package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CloudTileTest {

    //testing normal behaviour
    @Test
    void fill() {
        CloudTile c = new CloudTile(3);
        assertEquals(0, c.numStudOn());
        c.fill(Color.YELLOW);
        assertEquals(1, c.numStudOn(Color.YELLOW));
    }

    @Test
    void fillCloudNullColor() {
        CloudTile c = new CloudTile(3);
        assertEquals(0, c.numStudOn());
        assertThrows(NullPointerException.class, () -> c.fill(null));
        assertEquals(0, c.numStudOn());
    }

    //single color
    @Test
    void fillFull1() {
        CloudTile c = new CloudTile(3); //for 3 players the maximum size is 4
        assertEquals(0, c.numStudOn());
        for (int i = 0; i < 4; i++)
            c.fill(Color.YELLOW);
        assertEquals(4, c.numStudOn());

        c.fill(Color.YELLOW);
        assertEquals(4, c.numStudOn(Color.YELLOW));
    }

    //various color
    @Test
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


    //testing normal behaviour
    @Test
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

    @Test
    void getNotFullTile(){
        CloudTile c = new CloudTile(2);
        c.fill(Color.GREEN);

        assertEquals(1, c.numStudOn());
        assertThrows(IllegalStateException.class, c::getTile);
        assertEquals(1, c.numStudOn());
    }

    @Test
    void getEmptyTile(){
        CloudTile c = new CloudTile(2);

        assertEquals(0, c.numStudOn());
        assertThrows(IllegalStateException.class, c::getTile);
        assertEquals(0, c.numStudOn());
    }
}
package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CloudTileTest {

    //testing normal behaviour
    @Test
    void fillCloudTile() {
        CloudTile c = new CloudTile(3);
        assertEquals(0, c.getSize());
        c.fillCloudTile(Color.YELLOW);
        assertEquals(1, c.getSize(Color.YELLOW));
    }

    @Test
    void fillCloudNullColor() {
        CloudTile c = new CloudTile(3);
        assertEquals(0, c.getSize());
        assertThrows(NullPointerException.class, () -> c.fillCloudTile(null));
        assertEquals(0, c.getSize());
    }

    //single color
    @Test
    void fillCloudTileFull1() {
        CloudTile c = new CloudTile(3); //for 3 players the maximum size is 4
        assertEquals(0, c.getSize());
        for (int i = 0; i < 4; i++)
            c.fillCloudTile(Color.YELLOW);
        assertEquals(4, c.getSize());

        c.fillCloudTile(Color.YELLOW);
        assertEquals(4, c.getSize(Color.YELLOW));
    }

    //various color
    @Test
    void fillCloudTileFull2() {
        CloudTile c = new CloudTile(3); //for 3 players the maximum size is 4
        assertEquals(0, c.getSize());
        for (int i = 0; i < 4; i++) {
            if(i < 2)
                c.fillCloudTile(Color.YELLOW);
            else
                c.fillCloudTile(Color.GREEN);
        }
        assertEquals(4, c.getSize());

        c.fillCloudTile(Color.YELLOW);
        assertEquals(4, c.getSize());
    }


    //testing normal behaviour
    @Test
    void getTile(){
        CloudTile c1 = new CloudTile(2);
        for (int i = 0; i < 3; i++) {
            if(i < 2)
                c1.fillCloudTile(Color.YELLOW);
            else
                c1.fillCloudTile(Color.GREEN);
        }
        assertEquals(3, c1.getSize());

        StudentsHandler s = c1.getTile();
        assertEquals(3, s.numStudents());
        assertEquals(0, c1.getSize());
    }

    @Test
    void getNotFullTile(){
        CloudTile c = new CloudTile(2);
        c.fillCloudTile(Color.GREEN);

        assertEquals(1, c.getSize());
        assertThrows(IllegalStateException.class, c::getTile);
        assertEquals(1, c.getSize());
    }

    @Test
    void getEmptyTile(){
        CloudTile c = new CloudTile(2);

        assertEquals(0, c.getSize());
        assertThrows(IllegalStateException.class, c::getTile);
        assertEquals(0, c.getSize());
    }
}
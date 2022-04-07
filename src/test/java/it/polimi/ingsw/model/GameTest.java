package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    Game g;

    @BeforeEach
    void setup(){
        //first player choose player's number
        g = new Game("Dario",3);
    }

    /**
     * This method tests addPlayer() adding more players than the
     * established number.
     * The player list should only contain players added when the
     * set number had not yet been reached
     */
    @DisplayName("Adding more players than set number test")
    @Test
    void addingTooPlayer() {
        assertEquals(1, g.getNumPlayers());
        g.addPlayer("Lorenzo");
        g.addPlayer("Luca");
        assertEquals(3, g.getNumPlayers());
        g.addPlayer("Matteo");
        assertEquals(3, g.getNumPlayers());
    }

    /**
     * This method tests adding a player with empty string as nickname
     * @throws IllegalStateException when empty string is passed as nickname's player
     */
    @DisplayName("Adding a player with empty nickname test ")
    @Test
    void addEmptyPlayer(){
        assertThrows(IllegalArgumentException.class,
                () -> g.addPlayer(""));
        assertEquals(1, g.getNumPlayers());
    }

    /**
     * This method tests starting a game when there are missing player
     * @throws IllegalStateException when required number of players has not been reached
     */
    @DisplayName("Starting a game without reaching required number of players test")
    @Test
    void illegalStarting(){
        assertThrows(IllegalStateException.class,
                () ->g.startGame());
    }

    //helper method for other tests method
    void setupFullPlayer(){
        g.addPlayer("Lorenzo");
        g.addPlayer("Luca");
    }

    /**
     * This method tests the setup for a set of clouds
     */
    @Test
    @DisplayName("Init cloud tiles test")
    void checkCloudInit(){
        setupFullPlayer();
        assertEquals(3, g.getNumPlayers());
        g.startGame();

        //clouds initially empty
        for(CloudTile c: g.getCloudTiles()){
            assertEquals(0,c.numStudOn());
        }

    }

    /**
     * This method tests if a player's entrance has been filled with students
     */
    @Test
    @DisplayName("Filling entrance test")
    void checkEntrancePlayer(){
        setupFullPlayer();
        g.startGame();

        for(Player p: g.getPlayers()){
            assertFalse(p.getBoard().entranceIsFillable());
        }

    }



    /**
     * This method tests if mother nature was initialised within the archipelago
     */
    @Test
    @DisplayName("Mother nature init test")
    void checkInitMotherNature(){
        setupFullPlayer();
        g.startGame();
        assertTrue(g.getMotherNature() < 12 && g.getMotherNature()>=0);
    }

    /**
     * This method tests if all the island tiles has one student, except that one with mother nature and the
     * tile at its opposite
     */
    @Test
    @DisplayName("Init tiles with students test")
    void checkStudentOnIsland(){
        setupFullPlayer();
        g.startGame();
        for(int i = 0; i< 12;i++){
            if(i == g.getMotherNature() || i == (g.getMotherNature()+6)%12)
                assertEquals(0,g.getArchipelago().get(i).getIslandStudents().numStudents() );
            else
                assertEquals(1,g.getArchipelago().get(i).getIslandStudents().numStudents() );
        }
    }



}
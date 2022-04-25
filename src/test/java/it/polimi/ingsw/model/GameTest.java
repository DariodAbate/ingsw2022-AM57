package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests Game class using getter method provided by Game class.
 *
 * @author Dario d'Abate
 * @author Lorenzo Corrado
 */

class GameTest {
    Game g;

    @BeforeEach
    void setup() {
        //first player choose player's number
        g = new Game("Dario", 3);
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
     *
     * @throws IllegalStateException when empty string is passed as nickname's player
     */
    @DisplayName("Adding a player with empty nickname test ")
    @Test
    void addEmptyPlayer() {
        assertThrows(IllegalArgumentException.class,
                () -> g.addPlayer(""));
        assertEquals(1, g.getNumPlayers());
    }

    /**
     * This method tests starting a game when there are missing player
     *
     * @throws IllegalStateException when required number of players has not been reached
     */
    @DisplayName("Starting a game without reaching required number of players test")
    @Test
    void illegalStarting() {
        assertThrows(IllegalStateException.class,
                () -> g.startGame());
    }

    //helper method for other tests method that adds 2
    //players
    void setupFullPlayer() {
        g.addPlayer("Lorenzo");
        g.addPlayer("Luca");
    }

    /**
     * This method tests the setup for a set of clouds
     */
    @Test
    @DisplayName("Init cloud tiles test")
    void checkCloudInit() {
        setupFullPlayer();
        assertEquals(3, g.getNumPlayers());
        g.startGame();

        //clouds initially empty
        for (CloudTile c : g.getCloudTiles()) {
            assertEquals(0, c.numStudOn());
        }

    }

    /**
     * This method tests if a player's entrance has been filled with students
     */
    @Test
    @DisplayName("Filling entrance test")
    void checkEntrancePlayer() {
        setupFullPlayer();
        g.startGame();

        for (Player p : g.getPlayers()) {
            assertFalse(p.getBoard().entranceIsFillable());
        }

    }


    /**
     * This method tests if mother nature was initialised within the archipelago
     */
    @Test
    @DisplayName("Mother nature init test")
    void checkInitMotherNature() {
        setupFullPlayer();
        g.startGame();
        assertTrue(g.getMotherNature() < 12 && g.getMotherNature() >= 0);
    }

    /**
     * This method tests if all the island tiles has one student, except that one with mother nature and the
     * tile at its opposite
     */
    @Test
    @DisplayName("Init tiles with students test")
    void checkStudentOnIsland() {
        setupFullPlayer();
        g.startGame();
        for (int i = 0; i < 12; i++) {
            if (i == g.getMotherNature() || i == (g.getMotherNature() + 6) % 12)
                assertEquals(0, g.getArchipelago().get(i).getIslandStudents().numStudents());
            else
                assertEquals(1, g.getArchipelago().get(i).getIslandStudents().numStudents());
        }
    }

    /**
     * This method tests the mechanism of refilling cloud tiles
     * when all the cloud tiles are empty
     */
    @Test
    @DisplayName("Refill empty cloud tile test")
    void bagToCloudEmpty() {
        setupFullPlayer();
        g.startGame();

        for (CloudTile cloudTile : g.getCloudTiles())
            assertTrue(cloudTile.isEmpty());

        g.bagToClouds();
        for (CloudTile cloudTile : g.getCloudTiles())
            assertFalse(cloudTile.isFillable());
    }

    /**
     * This method tests the mechanism of refilling cloud tiles
     * when  the cloud tiles are not empty
     *
     * @throws IllegalStateException when a not empty cloud tile is
     *                               filled with students
     */
    @DisplayName("Refill not empty cloud tile test")
    @Test
    void bagToCloudNotEmpty() {
        setupFullPlayer();
        g.startGame();

        g.getCloudTiles().get(2).fill(Color.YELLOW);
        assertThrows(IllegalStateException.class,
                () -> g.bagToClouds());

    }

    /**
     * This method test the mechanism of refilling the current player's board
     * with students on a specified cloud tile when all the movable students in the
     * board have been moved and the current players has chosen an existent cloud tile
     */
    @DisplayName("Refill current player board's entrance with students on cloud, normal condition test")
    @Test
    void cloudToBoard() {
        setupFullPlayer();
        g.startGame();
        g.bagToClouds();
        //all clouds filled

        Board boardCurrentPlayer = g.getCurrentPlayer().getBoard();
        assertFalse(boardCurrentPlayer.entranceIsFillable());
        int i = 0;
        for (Color color : Color.values()) {
            while (boardCurrentPlayer.studentInEntrance(color) && i < 4) {
                boardCurrentPlayer.removeStudentFromEntrance(color);
                ++i;
            }
        }

        assertTrue(boardCurrentPlayer.entranceIsFillable());
        g.cloudToBoard(0); //using a stub in game that returns the first player in the list
        assertFalse(boardCurrentPlayer.entranceIsFillable());

    }

    /**
     * This method test the mechanism of refilling the current player's board
     * with students on a specified cloud tile when all the movable students in the
     * board have not been moved and the current players has chosen an existent cloud tile
     */
    @DisplayName("Refill current player board's entrance with students on cloud, entrance not fillable test")
    @Test
    void cloudToBoard2() {
        setupFullPlayer();
        g.startGame();
        g.bagToClouds();
        //all clouds filled

        Board boardCurrentPlayer = g.getCurrentPlayer().getBoard();
        assertFalse(boardCurrentPlayer.entranceIsFillable());
        int i = 0;
        for (Color color : Color.values()) {
            while (boardCurrentPlayer.studentInEntrance(color) && i < 3) {
                boardCurrentPlayer.removeStudentFromEntrance(color);
                ++i;
            }
        }

        assertTrue(boardCurrentPlayer.entranceIsFillable());
        assertThrows(IllegalStateException.class, () -> g.cloudToBoard(0));


    }

    /**
     * This method test the mechanism of refilling the current player's board
     * with students on a specified cloud tile when all the movable students in the
     * board have been moved and the current players has chosen a not existent cloud tile
     */
    @DisplayName("Refill current player board's entrance with students on not existing cloud test")
    @Test
    void cloudToBoard3() {
        setupFullPlayer();
        g.startGame();
        g.bagToClouds();
        //all clouds filled

        Board boardCurrentPlayer = g.getCurrentPlayer().getBoard();
        assertFalse(boardCurrentPlayer.entranceIsFillable());
        int i = 0;
        for (Color color : Color.values()) {
            while (boardCurrentPlayer.studentInEntrance(color) && i < 4) {
                boardCurrentPlayer.removeStudentFromEntrance(color);
                ++i;
            }
        }

        assertTrue(boardCurrentPlayer.entranceIsFillable());
        assertThrows(IndexOutOfBoundsException.class, () -> g.cloudToBoard(3));


    }

    /**
     * This method tests all the corner case for the merging:
     * -two adjacent islands with the same color (right and left merge)
     * -three adjacent islands with the same color
     * -two adjacent islands with the same color and one adjacent island with a different color
     * -some merge cases to test also the correct behaviour of the cyclic array (using the first or last index in the array)
     */
    @DisplayName("Merging specific cases of islands")
    @Test
    void mergingIslands(){
        setupFullPlayer();
        g.startGame();
        int temp;
        temp = g.getArchipelago().get(0).getIslandStudents().numStudents() + g.getArchipelago().get(11).getIslandStudents().numStudents();
        //Test right merging
        g.getArchipelago().get(0).addTower();
        g.getArchipelago().get(0).changeTowerColor(Tower.WHITE);
        g.getArchipelago().get(11).addTower();
        g.getArchipelago().get(11).changeTowerColor(Tower.WHITE);
        g.setMotherNature(11);
        g.mergeIslandTile();
        assertEquals(11, g.getArchipelago().size());
        assertEquals(2, g.getArchipelago().get(0).getNumTowers());
        assertEquals(temp, g.getArchipelago().get(0).getIslandStudents().numStudents());
        assertEquals(0, g.getMotherNature());
        //Test not merging (current island no towers)
        g.setMotherNature(5);
        IslandTile tempIsland = g.getCurrentIsland();
        g.mergeIslandTile();
        assertEquals(11, g.getArchipelago().size());
        //Test not merging (right and left islands no towers)
        g.setMotherNature(0);
        g.mergeIslandTile();
        assertEquals(11, g.getArchipelago().size());
        //Test left merging
        g.getArchipelago().get(10).addTower();
        g.getArchipelago().get(10).changeTowerColor(Tower.WHITE);
        g.setMotherNature(0);
        temp = g.getArchipelago().get(0).getIslandStudents().numStudents() + g.getArchipelago().get(10).getIslandStudents().numStudents();
        g.mergeIslandTile();
        assertEquals(10, g.getArchipelago().size());
        assertEquals(3, g.getArchipelago().get(0).getNumTowers());
        assertEquals(temp, g.getArchipelago().get(0).getIslandStudents().numStudents());
        //Test no merging adjacent islands no towers
        g.mergeIslandTile();
        assertEquals(10, g.getArchipelago().size());
        //Test triple merging
        temp = g.getArchipelago().get(0).getIslandStudents().numStudents() + g.getArchipelago().get(9).getIslandStudents().numStudents()+g.getArchipelago().get(1).getIslandStudents().numStudents();
        g.getArchipelago().get(9).addTower();
        g.getArchipelago().get(9).changeTowerColor(Tower.WHITE);
        g.getArchipelago().get(1).addTower();
        g.getArchipelago().get(1).changeTowerColor(Tower.WHITE);
        g.setMotherNature(0);
        g.mergeIslandTile();
        assertEquals(8, g.getArchipelago().size());
        assertEquals(5, g.getArchipelago().get(0).getNumTowers());
        assertEquals(temp, g.getArchipelago().get(0).getIslandStudents().numStudents());
        //Test no merge (different tower color)
        g.getArchipelago().get(1).addTower();
        g.getArchipelago().get(1).changeTowerColor(Tower.BLACK);
        g.setMotherNature(0);
        g.mergeIslandTile();
        assertEquals(8, g.getArchipelago().size());
        //Test merge with different color
        g.getArchipelago().get(2).addTower();
        g.getArchipelago().get(2).changeTowerColor(Tower.BLACK);
        g.setMotherNature(1);
        g.mergeIslandTile();
        assertEquals(7, g.getArchipelago().size());
        assertEquals(2, g.getArchipelago().get(1).getNumTowers());
        assertTrue(g.getArchipelago().contains(tempIsland));
        assertEquals(3, g.getArchipelago().indexOf(tempIsland));
    }


    //The following tests involve moving a student to an island tile where there is no mother nature
    //I consider cases where there are only students due to initialization

    /**
     * This test involves moving a student to an island tile where there is no mother nature.
     * In this test there are only students on the archipelago due to initialization, so after a movement
     * an island tile has to contain exactly 2 students
     */
    @Test
    @DisplayName("Moving an existing student from entrance to an existing island tile")
    void entranceToIsland1(){
        setupFullPlayer();
        g.startGame();

        Board boardCurrentPlayer = g.getCurrentPlayer().getBoard();
        assertFalse(boardCurrentPlayer.entranceIsFillable());

        int idxTest = g.getMotherNature();
        int idxToBeTested = (idxTest + 1) % g.getArchipelago().size();
        assertEquals(1, g.getArchipelago().get(idxToBeTested).getIslandStudents().numStudents());

        if (boardCurrentPlayer.studentInEntrance(Color.YELLOW)) {
            g.entranceToIsland(idxToBeTested, Color.YELLOW);
            assertEquals(2, g.getArchipelago().get(idxToBeTested).getIslandStudents().numStudents());
        } else if (boardCurrentPlayer.studentInEntrance(Color.RED)) {
            g.entranceToIsland(idxToBeTested, Color.RED);
            assertEquals(2, g.getArchipelago().get(idxToBeTested).getIslandStudents().numStudents());
        } else if (boardCurrentPlayer.studentInEntrance(Color.GREEN)) {
            g.entranceToIsland(idxToBeTested, Color.GREEN);
            assertEquals(2, g.getArchipelago().get(idxToBeTested).getIslandStudents().numStudents());
        } else if (boardCurrentPlayer.studentInEntrance(Color.BLUE)) {
            g.entranceToIsland(idxToBeTested, Color.BLUE);
            assertEquals(2, g.getArchipelago().get(idxToBeTested).getIslandStudents().numStudents());
        } else {
            g.entranceToIsland(idxToBeTested, Color.PINK);
            assertEquals(2, g.getArchipelago().get(idxToBeTested).getIslandStudents().numStudents());
        }
    }

    /**
     * This method tests moving a student to a not existing island tile
     * @throws IndexOutOfBoundsException when is passed adn index that does not correspond to an existing island
     */
    @Test
    @DisplayName("Moving a student from entrance to a not existing island tile")
    void entranceToIsland2(){
        setupFullPlayer();
        g.startGame();
        Board boardCurrentPlayer = g.getCurrentPlayer().getBoard();
        assertFalse(boardCurrentPlayer.entranceIsFillable());

        assertThrows(IndexOutOfBoundsException.class, () ->
                g.entranceToIsland(13, Color.YELLOW));
    }

    /**
     * This method tests moving a not existing student to an island tile
     */
    @Test
    @DisplayName("Moving a not existing student from entrance to an existing island tile")
    void entranceToIsland3(){
        setupFullPlayer();
        g.startGame();
        Board boardCurrentPlayer = g.getCurrentPlayer().getBoard();
        assertFalse(boardCurrentPlayer.entranceIsFillable());
        while(boardCurrentPlayer.studentInEntrance(Color.YELLOW))
            boardCurrentPlayer.removeStudentFromEntrance(Color.YELLOW);
        assertFalse(boardCurrentPlayer.studentInEntrance(Color.YELLOW));

        assertThrows(IllegalArgumentException.class, () ->
                g.entranceToIsland(0, Color.YELLOW));
    }


    /**
     * This method tests whether moving a not existing student from the current player's entrance in its hall
     * throws an exception. The entrance should not be modified
     */
    @Test
    @DisplayName("Moving a not existing student from entrance to empty hall test")
    void entranceToHall1(){
        setupFullPlayer();
        g.startGame();
        Board boardCurrentPlayer = g.getCurrentPlayer().getBoard();

        while(boardCurrentPlayer.studentInEntrance(Color.BLUE)) //emptying entrance from blue students
            boardCurrentPlayer.removeStudentFromEntrance(Color.BLUE);
        int oldSize = boardCurrentPlayer.entranceSize();

        assertThrows(IllegalArgumentException.class, ()-> g.entranceToHall(Color.BLUE));
        int newSize = boardCurrentPlayer.entranceSize();
        assertEquals(oldSize,newSize);
    }

    //helper method for choosing an existing color on the entrance' board
    public Color getExistingColor(Board board) {
        for (Color color : Color.values()) {
            if (board.studentInEntrance(color))
                return color;
        }
        return null;
    }

    /**
     * This method tests whether moving an existing student from the current player's entrance in its full hall
     * throws an exception. The entrance should not be modified
     */
    @Test
    @DisplayName("Moving an existing student from entrance to full hall test")
    void entranceToHall2(){
        setupFullPlayer();
        g.startGame();
        Board boardCurrentPlayer = g.getCurrentPlayer().getBoard();
        Color existingColor = getExistingColor(boardCurrentPlayer);

        while(boardCurrentPlayer.hallIsFillable(existingColor)) //filling the hall with a specified color
            boardCurrentPlayer.fillHall(existingColor);

        int oldSize = boardCurrentPlayer.entranceSize();
        assertThrows(IllegalStateException.class, ()->g.entranceToHall(existingColor));
        int newSize = boardCurrentPlayer.entranceSize();
        assertEquals(oldSize,newSize);

    }

    /**
     * This method tests moving a student moving a student from the current player's entrance in its hall in a non
     * error condition.
     */
    @Test
    @DisplayName("Moving an existing student from entrance to empty hall test")
    void entranceToHall3(){
        setupFullPlayer();
        g.startGame();
        Board boardCurrentPlayer = g.getCurrentPlayer().getBoard();
        Color existingColor = getExistingColor(boardCurrentPlayer);

        int oldSizeEntrance = boardCurrentPlayer.entranceSize(existingColor);
        int oldSizeHall = boardCurrentPlayer.hallSize(existingColor);
        g.entranceToHall(existingColor);

        int newSizeEntrance = boardCurrentPlayer.entranceSize(existingColor);
        int newSizeHall = boardCurrentPlayer.hallSize(existingColor);
        assertEquals(oldSizeEntrance - 1, newSizeEntrance);
        assertEquals(oldSizeHall + 1, newSizeHall);

    }


    /**
     * This method tests the condition in which a player obtains a professor that has never taken before,
     * after he has moved a student from his entrance to his hall.
     */
    @Test
    @DisplayName("Obtain a not already taken professor test")
    void entranceToHall4(){
        setupFullPlayer();
        g.startGame();
        Board boardCurrentPlayer = g.getCurrentPlayer().getBoard();
        Color existingColor = getExistingColor(boardCurrentPlayer);
        assertFalse(boardCurrentPlayer.hasProfessor(existingColor));
        g.entranceToHall(existingColor);
        assertTrue(boardCurrentPlayer.hasProfessor(existingColor));
    }

    //helper method for getting the player of a player that's not the current player
    private Board getBoardOtherPlayer(){
        int idxCurrentPlayer = g.players.indexOf(g.getCurrentPlayer());
        Board board;
        for(int i = 0; i < g.players.size(); i++){
            board = g.players.get(i).getBoard();
            if(i != idxCurrentPlayer)
                return board;
        }
        return null;
    }

    /**
     * This method tests the condition in which a player obtains a professor that has already taken before,
     * after he has moved a student from his entrance to his hall.
     */
    @Test
    @DisplayName("Obtain an already taken professor test")
    void entranceToHall5(){
        setupFullPlayer();
        g.startGame();
        Board boardAnotherPlayer = getBoardOtherPlayer();
        Board boardCurrentPlayer = g.getCurrentPlayer().getBoard();
        Color color = getExistingColor(boardCurrentPlayer);

        assertFalse(boardAnotherPlayer.hasProfessor(color));
        boardAnotherPlayer.fillHall(color);
        boardAnotherPlayer.fillHall(color);
        boardAnotherPlayer.addProfessor(color);
        assertTrue(boardAnotherPlayer.hasProfessor(color));


        boardCurrentPlayer.fillHall(color);
        boardCurrentPlayer.fillHall(color);
        assertFalse(boardCurrentPlayer.hasProfessor(color));
        g.entranceToHall(color);
        assertTrue(boardCurrentPlayer.hasProfessor(color));
    }


    @Test
    @DisplayName("Getting name of winner who built the most towers on the islands test")
    void alternativeWinner1(){
        setupFullPlayer();
        g.startGame();
        Board boardCurrentPlayer = g.getCurrentPlayer().getBoard();

        boardCurrentPlayer.decNumTower();
        assertEquals(g.getCurrentPlayer().getNickname(), g.alternativeWinner());
    }


    @Test
    @DisplayName("Getting name of winner who owns the largest number of professors test")
    void alternativeWinner2(){
        setupFullPlayer();
        g.startGame();
        Board boardAnotherPlayer = getBoardOtherPlayer();
        Board boardCurrentPlayer = g.getCurrentPlayer().getBoard();

        boardCurrentPlayer.decNumTower();
        boardCurrentPlayer.addProfessor(Color.BLUE);
        boardAnotherPlayer.decNumTower();

        assertEquals(g.getCurrentPlayer().getNickname(), g.alternativeWinner());
    }

    /**
     * This method tests whether playing an assistant card sets the correct maximum number of moves that mother nature
     * can do
     */
    @Test
    @DisplayName("Playing existing assistant card test")
    void playCardTest1(){
        setupFullPlayer();
        g.startGame();

        //idxCard = 0 -> movement = 1, priority = 1
        g.playCard(0);
        assertEquals(1, g.getMaxMovement());
    }

    /**
     *  This method tests that playing a not existing assistant card throws an IllegalArgumentException
     */
    @Test
    @DisplayName("Playing not existing assistant card test")
    void playCardTest2(){
        setupFullPlayer();
        g.startGame();

        assertThrows(IllegalArgumentException.class, ()-> g.playCard(-1));
    }
}
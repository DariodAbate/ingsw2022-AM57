package it.polimi.ingsw.model;

import it.polimi.ingsw.model.expertGame.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests ExpertGame class. It tests the main mechanics of playing cards and coin management.
 * Tests that would involve a complete simulation of the game have not been done
 * @author Dario d'Abate
 * @author Lorenzo Corrado
 * @author Luca Bresciani
 */
class ExpertGameTest {
    ExpertGame g;
    StudentsBufferCardsCluster manCard;
    StudentsBufferCardsCluster clownCard;
    StudentsBufferCardsCluster womanCard;
    TakeProfessorEqualStudentsCard TPEScard;

    @BeforeEach
    void setup() {
        //first player choose player's number
        g = new ExpertGame("Dario", 3);
    }

    void setupFullPlayer() {
        g.addPlayer("Lorenzo");
        g.addPlayer("Luca");
    }

    /**
     * This method tests the correct merging and reindexing of mother nature due to the card effect
     */
    @DisplayName("Merge Island with PseudoMotherNature")
    @Test
    public void PseudoMotherNatureCardTest() {
        PseudoMotherNatureCard card = new PseudoMotherNatureCard(1, g);
        setupFullPlayer();
        g.startGame();
        g.getArchipelago().get(0).addTower();
        g.getArchipelago().get(0).changeTowerColor(Tower.WHITE);
        g.getArchipelago().get(1).addTower();
        g.getArchipelago().get(1).changeTowerColor(Tower.WHITE);
        g.getArchipelago().get(2).addTower();
        g.getArchipelago().get(2).changeTowerColor(Tower.WHITE);
        g.setMotherNature(5);
        card.effect();
        assertEquals(3, g.getMotherNature());
        card.changeIslandIndex(2);
        g.getArchipelago().get(1).addTower();
        g.getArchipelago().get(1).changeTowerColor(Tower.WHITE);
        g.getArchipelago().get(3).addTower();
        g.getArchipelago().get(3).changeTowerColor(Tower.WHITE);
        g.getArchipelago().get(2).addTower();
        g.getArchipelago().get(2).changeTowerColor(Tower.WHITE);
        card.effect();
        assertEquals(1, g.getMotherNature());
    }

    /**
     * This method tests the correct conquering due to the Pseudo Mother Nature card
     */
    @DisplayName("Check conquer functionalities")
    @Test
    public void PseudoMotherNatureCardTest2() {
        PseudoMotherNatureCard card = new PseudoMotherNatureCard(1, g);
        setupFullPlayer();
        g.startGame();
        g.getPlayers().get(1).getBoard().chooseTower(Tower.WHITE);
        g.getArchipelago().get(1).getIslandStudents().add(Color.RED, 5);
        g.getArchipelago().get(0).addTower();
        g.getArchipelago().get(0).changeTowerColor(Tower.WHITE);
        g.getArchipelago().get(2).addTower();
        g.getArchipelago().get(2).changeTowerColor(Tower.WHITE);
        g.getPlayers().get(0).getBoard().addProfessor(Color.BLUE);
        g.getPlayers().get(0).getBoard().addProfessor(Color.YELLOW);
        g.getPlayers().get(1).getBoard().addProfessor(Color.RED);
        g.getPlayers().get(1).getBoard().addProfessor(Color.GREEN);
        g.getPlayers().get(2).getBoard().addProfessor(Color.PINK);
        g.setMotherNature(5);
        card.effect();
        assertEquals(3, g.getMotherNature());
    }


    //Tests of cards inside studentCluster

    /**************************************************************************************
     *                                    MAN CARD                                        *
     * At the start of the game, draw 4 students and place them on top of this card.      *
     * EFFECT: Take 1 student from the card and place it on an island of your choice.     *
     * Then, draw 1 student from the bag and place it on this card                        *
     *                                                                                    *
     *************************************************************************************/


    //helper method for testing Man card
    public void setupManCard() {
        setupFullPlayer();
        g.startGame();
        manCard = new StudentsBufferCardsCluster(0, g); //0 corresponds to the man card
    }

    /**
     * This method tests if the card has been instantiated correctly, checking his price, the number of students
     * on it and if it has been played.
     */
    @DisplayName("Checking setup man card test")
    @Test
    public void initManCardTest() {
        setupManCard();
        assertEquals(4, manCard.getStudBuffer().numStudents()); //4 students on a card
        assertFalse(manCard.isPlayed());
        assertEquals(1, manCard.getPrice());
    }

    /**
     * This method tests whether activating the card's effect on a non-existent island throws an IndexOutOfBoundsException
     */
    @DisplayName("Moving a student to a not existing island")
    @Test
    public void manCardTest1() {
        setupManCard();
        manCard.setIdxChosenIsland(15); //max index is 11
        manCard.setStudentColorToBeMoved(getExistingColor(manCard.getStudBuffer()));
        assertThrows(IndexOutOfBoundsException.class, () -> manCard.effect());
        assertEquals(4, manCard.getStudBuffer().numStudents()); //4 students on a card
        assertFalse(manCard.isPlayed());
        assertEquals(1, manCard.getPrice());
    }

    /**
     * This method tests whether activating the card's effect with a non-existent student throws a NotExistingStudentException
     */
    @DisplayName("Moving a not existing student to an existing island")
    @Test
    public void manCardTest2() {
        setupManCard();
        manCard.setIdxChosenIsland(0);

        while (manCard.getStudBuffer().isRemovable(Color.YELLOW))
            manCard.getStudBuffer().remove(Color.YELLOW);
        manCard.setStudentColorToBeMoved(Color.YELLOW);
        assertThrows(IllegalArgumentException.class, () -> manCard.effect());
        assertFalse(manCard.isPlayed());
        assertEquals(1, manCard.getPrice());

    }

    //helper method for choosing an existing color on a StudentsHandler
    public Color getExistingColor(StudentsHandler studentsHandler) {
        for (Color color : Color.values()) {
            if (studentsHandler.numStudents(color) > 0)
                return color;
        }
        return null;
    }

    /**
     * This method tests the behavior of the card in a non-error condition. It moves a student to the initially empty island
     * with mother nature, so we can be sure that after effect() there will always be only one student on that tile.
     * It also tests the increase in the cost of the card after activating it once
     */
    @DisplayName("Moving an existing student to an existing island")
    @Test
    public void manCardTest3(){
        setupManCard();
        int idx = g.getMotherNature();
        IslandTile islandTile = g.getArchipelago().get(idx);
        assertEquals(0, islandTile.getIslandStudents().numStudents());

        //Since I don't know a priori which students are on a card, I use a helper method that returns
        // the first color of an existing student
        Color studentOnCard = getExistingColor(manCard.getStudBuffer());
        manCard.setStudentColorToBeMoved(studentOnCard);
        manCard.setIdxChosenIsland(idx);
        assertFalse(manCard.isPlayed());
        assertEquals(1, manCard.getPrice());

        manCard.effect();
        assertEquals(1, islandTile.getIslandStudents().numStudents());
        assertEquals(4, manCard.getStudBuffer().numStudents());
        assertTrue(manCard.isPlayed());
        assertEquals(2, manCard.getPrice());

    }

    /**
     * This method tests the behavior of the card in a non-error condition. It moves two students to the initially empty island
     * with mother nature, so we can be sure that after effect() there will always be only two students on that tile.
     * It also tests that there is only one cost increase after playing the card the first time
     */
    @DisplayName("Moving multiple existing students to an existing island")
    @Test
    public void manCardTest4(){
        setupManCard();
        int idx = g.getMotherNature();
        IslandTile islandTile = g.getArchipelago().get(idx);
        assertEquals(0, islandTile.getIslandStudents().numStudents());

        Color studentOnCard = getExistingColor(manCard.getStudBuffer());
        manCard.setStudentColorToBeMoved(studentOnCard);
        manCard.setIdxChosenIsland(idx);
        manCard.effect();
        assertEquals(1, islandTile.getIslandStudents().numStudents());
        assertEquals(4, manCard.getStudBuffer().numStudents());

        studentOnCard = getExistingColor(manCard.getStudBuffer());
        manCard.setStudentColorToBeMoved(studentOnCard);
        manCard.effect();
        assertEquals(2, islandTile.getIslandStudents().numStudents());
        assertEquals(4, manCard.getStudBuffer().numStudents());
        assertTrue(manCard.isPlayed());
        assertEquals(2, manCard.getPrice());
    }

    /**************************************************************************************
     *                                    CLOWN CARD                                      *
     * At the start of the game, draw 6 students and place them on top of this card.      *
     * EFFECT: You can take up to 3 Students from this card and swap them with as many    *
     * students in your hall                                                              *
     *                                                                                    *
     *************************************************************************************/

    //helper method for testing Clown card
    public void setupClownCard() {
        setupFullPlayer();
        g.startGame();
        clownCard = new StudentsBufferCardsCluster(1, g); //1 corresponds to the clown card
    }

    //helper method provided to avoiding a getter method od StudentsHandler in Board class
    public Color getExistingColorEntrance(){
        Board currentPlayerBoard = getBoard();
        for (Color color: Color.values()){
            if(currentPlayerBoard.studentInEntrance(color))
                return color;
        }
        return null;
    }

    /**
     * This method tests if the card has been instantiated correctly, checking his price, the number of students
     * on it and if it has been played.
     */
    @DisplayName("Checking setup Clown card test")
    @Test
    public void initClownCardTest() {
        setupClownCard();
        assertEquals(6, clownCard.getStudBuffer().numStudents()); //6 students on a card
        assertFalse(clownCard.isPlayed());
        assertEquals(1, clownCard.getPrice());
    }

    /**
     * This method tests whether the exception is thrown when attempting to swap a non-existent student from on top of a card.
     * We expect the card not to be activated, its cost not to be increased, and the students on it are unchanged
     */
    @DisplayName("Moving a not existing student from card")
    @Test
    public void clownCardTest1(){
        setupClownCard();
        Board currentPlayerBoardOld = getBoard();

        Color studentOnCard = getExistingColor(clownCard.getStudBuffer());
        Color studentInEntrance = getExistingColorEntrance();
        clownCard.setStudentColorToBeMoved(studentOnCard);
        clownCard.setStudentColorInEntrance(studentInEntrance);
        while(clownCard.getStudBuffer().isRemovable(studentOnCard)) ////emptying card of the specified color
            clownCard.getStudBuffer().remove(studentOnCard);

        assertThrows(IllegalArgumentException.class, () -> clownCard.effect());
        assertFalse(clownCard.isPlayed());
        assertEquals(1, clownCard.getPrice());
        Board currentPlayerBoardNew = getBoard();
        for(Color color: Color.values())
            assertEquals(currentPlayerBoardNew.entranceSize(color), currentPlayerBoardOld.entranceSize(color));
    }

    /**
     * This method tests whether the exception is thrown when attempting to swap a non-existent student from entrance.
     * We expect the card not to be activated, its cost not to be increased, and the students on it are unchanged

     */
    @DisplayName("Moving a not existing student from entrance")
    @Test
    public void clownCardTest2(){
        setupClownCard();
        Board currentPlayerBoardOld = getBoard();

        Color studentOnCard = getExistingColor(clownCard.getStudBuffer());
        Color studentInEntrance = getExistingColorEntrance();
        clownCard.setStudentColorToBeMoved(studentOnCard);
        clownCard.setStudentColorInEntrance(studentInEntrance);
        while(currentPlayerBoardOld.studentInEntrance(studentInEntrance)) ////emptying entrance of the specified color
            currentPlayerBoardOld.removeStudentFromEntrance(studentInEntrance);

        assertThrows(IllegalArgumentException.class, () -> clownCard.effect());
        assertFalse(clownCard.isPlayed());
        assertEquals(1, clownCard.getPrice());
        Board currentPlayerBoardNew = getBoard();
        for(Color color: Color.values())
            assertEquals(currentPlayerBoardNew.entranceSize(color), currentPlayerBoardOld.entranceSize(color));

    }

    /**
     * This method tests the behavior of the card in a non-error condition.
     * It also tests the increase in the cost of the card after activating it once
     */
    @DisplayName("Swapping existent students")
    @Test
    public void clownCardTest3(){
        setupClownCard();
        Board currentPlayerBoard = getBoard();

        Color studentOnCard = getExistingColor(clownCard.getStudBuffer()); //color of the student that goes from the card to the entrance
        Color studentInEntrance = getExistingColorEntrance(); //color of the students that goes from the entrance to the card

        int oldNumStudSwappedOnCard = clownCard.getStudBuffer().numStudents(studentOnCard);
        int oldNumStudEarnedOnCard = clownCard.getStudBuffer().numStudents(studentInEntrance);
        int oldNumStudSwappedInEntrance = currentPlayerBoard.entranceSize(studentInEntrance);
        int oldNumStudEarnedInEntrance = currentPlayerBoard.entranceSize(studentOnCard);

        clownCard.setStudentColorToBeMoved(studentOnCard);
        clownCard.setStudentColorInEntrance(studentInEntrance);
        clownCard.effect();
        assertTrue(clownCard.isPlayed());
        assertEquals(2, clownCard.getPrice());

        int newNumStudSwappedOnCard = clownCard.getStudBuffer().numStudents(studentOnCard);
        int newNumStudEarnedOnCard = clownCard.getStudBuffer().numStudents(studentInEntrance);
        int newNumStudSwappedInEntrance = currentPlayerBoard.entranceSize(studentInEntrance);
        int newNumStudEarnedInEntrance = currentPlayerBoard.entranceSize(studentOnCard);

        if(studentInEntrance.equals(studentOnCard)){ //unchanged size because we swap the same color
            assertEquals(oldNumStudSwappedOnCard, newNumStudSwappedOnCard );
            assertEquals(oldNumStudEarnedOnCard, newNumStudEarnedOnCard );
            assertEquals(oldNumStudSwappedInEntrance, newNumStudSwappedInEntrance);
            assertEquals(oldNumStudEarnedInEntrance, newNumStudEarnedInEntrance);
        }else {
            assertEquals(oldNumStudSwappedOnCard, newNumStudSwappedOnCard + 1);
            assertEquals(oldNumStudEarnedOnCard, newNumStudEarnedOnCard - 1);
            assertEquals(oldNumStudSwappedInEntrance, newNumStudSwappedInEntrance + 1);
            assertEquals(oldNumStudEarnedInEntrance, newNumStudEarnedInEntrance - 1);
        }

    }

    /**
     *This method tests that there is only one cost increase after playing the card the first time
     */
    @DisplayName("Swapping multiple existent students")
    @Test
    public void clownCardTest4(){
        setupClownCard();

        Color studentOnCard = getExistingColor(clownCard.getStudBuffer()); //color of the student that goes from the card to the entrance
        Color studentInEntrance = getExistingColorEntrance(); //color of the students that goes from the entrance to the card

        clownCard.setStudentColorToBeMoved(studentOnCard);
        clownCard.setStudentColorInEntrance(studentInEntrance);
        clownCard.effect();
        assertTrue(clownCard.isPlayed());
        assertEquals(2, clownCard.getPrice());

        studentOnCard = getExistingColor(clownCard.getStudBuffer());
        studentInEntrance = getExistingColorEntrance();
        clownCard.setStudentColorToBeMoved(studentOnCard);
        clownCard.setStudentColorInEntrance(studentInEntrance);
        clownCard.effect();
        assertTrue(clownCard.isPlayed());
        assertEquals(2, clownCard.getPrice());

    }

    /**************************************************************************************
     *                                    WOMAN CARD                                      *
     * At the start of the game, draw 4 students and place them on top of this card.      *
     * EFFECT: Take 1 student from this card and place it in your room.                   *
     * Then, draw a new student from the bag and place it on this card                    *
     *                                                                                    *
     *************************************************************************************/

    //helper method for testing Woman card
    public void setupWomanCard() {
        setupFullPlayer();
        g.startGame();
        womanCard = new StudentsBufferCardsCluster(2, g); //2 corresponds to the woman card
    }

    /**
     * This method tests if the card has been instantiated correctly, checking his price, the number of students
     * on it and if it has been played.
     */
    @DisplayName("Checking setup Woman card test")
    @Test
    public void initWomanCardTest() {
        setupWomanCard();
        assertEquals(4, womanCard.getStudBuffer().numStudents()); //4 students on a card
        assertFalse(womanCard.isPlayed());
        assertEquals(2, womanCard.getPrice());
    }

    //helper method that returns the board of the current player
    public Board getBoard() {
        return g.getCurrentPlayer().getBoard();
    }

    /**
     * This method tests the effect of the card when the hall is full.
     * We expect the move will not happen, the student will not be removed from the bag,
     * the card is not played, the cost is not increased
     */
    @DisplayName("Moving a student to full hall")
    @Test
    public void womanCardTest1(){
        setupWomanCard();
        Color studentOnCard = getExistingColor(womanCard.getStudBuffer());
        Board currentPlayerBoard = getBoard();
        womanCard.setStudentColorToBeMoved(studentOnCard);
        while (currentPlayerBoard.hallIsFillable(studentOnCard)) //filling hall of the specified color
            currentPlayerBoard.fillHall(studentOnCard);

        int oldSize = womanCard.getStudBuffer().numStudents(studentOnCard);
        womanCard.effect();
        int newSize = womanCard.getStudBuffer().numStudents(studentOnCard);
        assertEquals(oldSize, newSize);
        assertFalse(womanCard.isPlayed());
        assertEquals(2, womanCard.getPrice());
    }

    /**
     * This method tests whether activating the card's effect with a non-existent student throws a NotExistingStudentException
     * The card must not be played and the cost must not be increased
     */
    @DisplayName("Moving a not existing student to a not full hall")
    @Test
    public void womanCardTest2() {
        setupWomanCard();
        Board currentPlayerBoardOld = getBoard();
        womanCard.setStudentColorToBeMoved(Color.GREEN);
        while (womanCard.getStudBuffer().isRemovable(Color.GREEN)) //emptying card of the specified color
            womanCard.getStudBuffer().remove(Color.GREEN);

        assertThrows(IllegalArgumentException.class, () -> womanCard.effect());
        Board currentPlayerBoardNew = getBoard();
        assertEquals(currentPlayerBoardOld.entranceSize(Color.GREEN), currentPlayerBoardNew.entranceSize(Color.GREEN));
        assertFalse(womanCard.isPlayed());
        assertEquals(2, womanCard.getPrice());
    }

    /**
     * This method tests the behavior of the card in a non-error condition.
     * It also tests the increase in the cost of the card after activating it once
     */
    @DisplayName("Moving an existing student to a not full hall")
    @Test
    public void womanCardTest3(){
        setupWomanCard();
        Color studentOnCard = getExistingColor(womanCard.getStudBuffer());
        Board currentPlayerBoard = getBoard();
        womanCard.setStudentColorToBeMoved(studentOnCard);
        int oldSize = currentPlayerBoard.hallSize(studentOnCard);
        assertFalse(womanCard.isPlayed());
        assertEquals(2, womanCard.getPrice());

        womanCard.effect();
        int newSize = currentPlayerBoard.hallSize(studentOnCard);
        assertEquals(newSize, oldSize + 1); //added one student in hall
        assertEquals(4, womanCard.getStudBuffer().numStudents());
        assertTrue(womanCard.isPlayed());
        assertEquals(3, womanCard.getPrice());

    }

    /**
     * This method tests the behavior of the card in a non-error condition. It moves two students to the hall of the
     * current player.
     * It also tests that there is only one cost increase after playing the card the first time
     */
    @DisplayName("Moving multiple existing student to a not full hall")
    @Test
    public void womanCardTest4(){
        setupWomanCard();
        Board currentPlayerBoard = getBoard();

        Color studentOnCard1 = getExistingColor(womanCard.getStudBuffer());
        womanCard.setStudentColorToBeMoved(studentOnCard1);
        int oldSize1 = currentPlayerBoard.hallSize(studentOnCard1);
        assertFalse(womanCard.isPlayed());
        assertEquals(2, womanCard.getPrice());

        womanCard.effect();
        int newSize1 = currentPlayerBoard.hallSize(studentOnCard1);
        assertEquals(newSize1, oldSize1 + 1); //added one student in hall
        assertEquals(4, womanCard.getStudBuffer().numStudents());
        assertTrue(womanCard.isPlayed());
        assertEquals(3, womanCard.getPrice());

        Color studentOnCard2 = getExistingColor(womanCard.getStudBuffer());
        womanCard.setStudentColorToBeMoved(studentOnCard2);
        int oldSize2 = currentPlayerBoard.hallSize(studentOnCard2);

        womanCard.effect();
        int newSize2 = currentPlayerBoard.hallSize(studentOnCard2);
        assertEquals(newSize2, oldSize2 + 1); //added one student in hall
        assertEquals(4, womanCard.getStudBuffer().numStudents());
        assertTrue(womanCard.isPlayed());
        assertEquals(3, womanCard.getPrice());

    }

    //Tests of card of TakeProfessorEqualStudentsCard class, called TPES for brevity

    /**************************************************************************************
     *                                    CARD                                            *
     * EFFECT: During this turn, take control of the professors even if you have the      *
     * same number of students in your room as the player currently controlling them.     *
     *                                                                                    *
     *************************************************************************************/

    public void setupTPESCard() {
        setupFullPlayer();
        g.startGame();
        TPEScard = new TakeProfessorEqualStudentsCard(g); //2 corresponds to the woman card
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

    //helper method for choosing an existing color on the entrance' board
    public Color getExistingColor(Board board) {
        for (Color color : Color.values()) {
            if (board.studentInEntrance(color))
                return color;
        }
        return null;
    }

    /**
     * This method tests the behavior of the card in non error condition. The current player should get the professor
     * even if he has the same number of students as the player who owns him at that moment
     */
    @DisplayName("Get professor with same number of student test")
    @Test
    public void TPESCardTest1(){
        setupTPESCard();
        TPEScard.effect();

        Board boardAnotherPlayer = getBoardOtherPlayer();
        Board boardCurrentPlayer = g.getCurrentPlayer().getBoard();
        Color color = getExistingColor(boardCurrentPlayer);

        assertFalse(boardAnotherPlayer.hasProfessor(color));
        boardAnotherPlayer.fillHall(color);
        boardAnotherPlayer.fillHall(color);
        boardAnotherPlayer.addProfessor(color);
        assertTrue(boardAnotherPlayer.hasProfessor(color));


        boardCurrentPlayer.fillHall(color);
        assertFalse(boardCurrentPlayer.hasProfessor(color));
        g.entranceToHall(color);
        assertTrue(boardCurrentPlayer.hasProfessor(color));

    }

    /**
     * This method tests the correct working of the SwapStudents card. In particular s tested
     * the case when the player chose to swap one student.
     */
    @DisplayName("Tests the correct swapping of one students")
    @Test
    void SwapOneStudentTest() {
        SwapStudentsCard card = new SwapStudentsCard(g);
        setupFullPlayer();
        g.initRound();
        g.getCurrentPlayer().getBoard().fillEntrance(Color.BLUE);
        g.getCurrentPlayer().getBoard().fillEntrance(Color.RED);
        g.getCurrentPlayer().getBoard().entranceToHall(Color.RED);
        card.setNumOfStudentsToMove(1);
        card.setStudentInEntranceColor(Color.BLUE);
        card.setStudentInHallColor(Color.RED);
        card.effect();
        assertEquals(2, card.getPrice());
        assertEquals(1, g.getCurrentPlayer().getBoard().hallSize(Color.BLUE));
        assertEquals(0, g.getCurrentPlayer().getBoard().hallSize(Color.RED));
    }

    /**
     * This method tests that an exception is thrown when the player try to swap a student
     * that isn't in the hall.
     */
    @DisplayName("Tests exception when the selected color isn't available")
    @Test
    void SwapNonExistentStudent() {
        SwapStudentsCard card = new SwapStudentsCard(g);
        setupFullPlayer();
        g.startGame();
        card.setNumOfStudentsToMove(1);
        card.setStudentInEntranceColor(Color.YELLOW);
        card.setStudentInHallColor(Color.GREEN);
        assertThrows(IllegalArgumentException.class,
                card::effect);
    }

    /**
     * This method tests the correct working of the PutThreeStudentsInTheBag card both in the
     * case when the player has enough students and in the case when he doesn't.
     */
    @DisplayName("Tests the correct removing of the students from the hall")
    @Test
    void PutThreeStudentsInTheBagTest() {
        PutThreeStudentsInTheBagCard card = new PutThreeStudentsInTheBagCard(g);
        setupFullPlayer();
        g.initRound();
        g.initBags();
        for (int i = 0; i < 4; i ++) {
            g.getPlayers().get(0).getBoard().fillEntrance(Color.GREEN);
            g.getPlayers().get(0).getBoard().entranceToHall(Color.GREEN);
        }
        for (int i = 0; i < 2; i ++) {
            g.getPlayers().get(1).getBoard().fillEntrance(Color.GREEN);
            g.getPlayers().get(1).getBoard().entranceToHall(Color.GREEN);
        }
        assertEquals(4, g.getPlayers().get(0).getBoard().hallSize(Color.GREEN));
        assertEquals(2, g.getPlayers().get(1).getBoard().hallSize(Color.GREEN));
        assertEquals(0, g.getPlayers().get(2).getBoard().hallSize(Color.GREEN));
        card.setStudentColor(Color.GREEN);
        card.effect();
        assertEquals(1, g.getPlayers().get(0).getBoard().hallSize(Color.GREEN));
        assertEquals(0, g.getPlayers().get(1).getBoard().hallSize(Color.GREEN));
        assertEquals(0, g.getPlayers().get(2).getBoard().hallSize(Color.GREEN));
    }


    @DisplayName("Initializing Expert Cards")
    @Test
    void PickCards(){
        ExpertGame expertGame = new ExpertGame("Lorenzo", 3);
        expertGame.addPlayer("Dario");
        expertGame.addPlayer("Luca");
        expertGame.startGame();
        assertEquals(3, expertGame.getExpertCards().size());
    }

    /**
     * Tests the correct working of the BannedIsland card checking that a player with more influence
     * on an island already controlled by another player doesn't conquer this island. Tests also that
     * the ban tiles are removed from the reserve of the game and when a player try to conquer an island
     * with a ban tile on it the tile is removed from the island and added back to the reserve.
     */
    @DisplayName("Testing correct working of BanedIsland card")
    @Test
    void BannedIslandTest() {
        BannedIslandCard card = new BannedIslandCard(g);
        setupFullPlayer();
        g.startGame();
        //Player 1 has green and pink professors
        //Player 2 has yellow and blue professors
        //Player 3 has red professor
        g.getPlayers().get(0).getBoard().addProfessor(Color.GREEN);
        g.getPlayers().get(0).getBoard().addProfessor(Color.PINK);
        g.getPlayers().get(1).getBoard().addProfessor(Color.YELLOW);
        g.getPlayers().get(1).getBoard().addProfessor(Color.BLUE);
        g.getPlayers().get(2).getBoard().addProfessor(Color.RED);
        g.getArchipelago().get(1).add(Color.GREEN);
        g.getArchipelago().get(1).add(Color.GREEN);
        g.getArchipelago().get(1).conquer(g.getPlayers());
        assertEquals(1, g.getArchipelago().get(1).getNumTowers());
        assertEquals(5, g.getPlayers().get(0).getBoard().getNumTower());
        assertEquals(6, g.getPlayers().get(1).getBoard().getNumTower());
        g.getArchipelago().get(1).add(Color.BLUE);
        g.getArchipelago().get(1).add(Color.BLUE);
        g.getArchipelago().get(1).add(Color.BLUE);
        g.getArchipelago().get(1).add(Color.BLUE);
        g.getArchipelago().get(1).add(Color.BLUE);
        assertEquals(4, g.getBanTile());
        card.setIslandIndex(1);
        card.effect();
        assertEquals(3, g.getBanTile());
        g.getArchipelago().get(1).conquer(g.getPlayers());
        assertEquals(6, g.getPlayers().get(1).getBoard().getNumTower());
        assertEquals(5, g.getPlayers().get(0).getBoard().getNumTower());
        assertEquals(1, g.getArchipelago().get(1).getNumTowers());
        assertFalse(g.getArchipelago().get(1).getIsBanned());
        assertEquals(4, g.getBanTile());
    }

    @DisplayName("Testing more than one ban tile on an island")
    @Test
    void MoreThanOneBanTile() {
        BannedIslandCard card = new BannedIslandCard(g);
        setupFullPlayer();
        g.startGame();
        //Player 1 has green and pink professors
        //Player 2 has yellow and blue professors
        //Player 3 has red professor
        g.getPlayers().get(0).getBoard().addProfessor(Color.GREEN);
        g.getPlayers().get(0).getBoard().addProfessor(Color.PINK);
        g.getPlayers().get(1).getBoard().addProfessor(Color.YELLOW);
        g.getPlayers().get(1).getBoard().addProfessor(Color.BLUE);
        g.getPlayers().get(2).getBoard().addProfessor(Color.RED);
        g.getArchipelago().get(1).add(Color.GREEN);
        g.getArchipelago().get(1).add(Color.GREEN);
        g.getArchipelago().get(1).conquer(g.getPlayers());
        assertEquals(1, g.getArchipelago().get(1).getNumTowers());
        assertEquals(5, g.getPlayers().get(0).getBoard().getNumTower());
        assertEquals(6, g.getPlayers().get(1).getBoard().getNumTower());
        g.getArchipelago().get(1).add(Color.BLUE);
        g.getArchipelago().get(1).add(Color.BLUE);
        g.getArchipelago().get(1).add(Color.BLUE);
        g.getArchipelago().get(1).add(Color.BLUE);
        g.getArchipelago().get(1).add(Color.BLUE);
        card.setIslandIndex(1);
        card.effect();
        card.effect();
        assertEquals(2, g.getBanTile());
        assertEquals(2, g.getArchipelago().get(1).getBanTile());
        g.getArchipelago().get(1).conquer(g.getPlayers());
        assertEquals(6, g.getPlayers().get(1).getBoard().getNumTower());
        assertEquals(5, g.getPlayers().get(0).getBoard().getNumTower());
        assertEquals(1, g.getArchipelago().get(1).getNumTowers());
        assertTrue(g.getArchipelago().get(1).getIsBanned());
        assertEquals(3, g.getBanTile());
        assertEquals(1, g.getArchipelago().get(1).getBanTile());
        g.getArchipelago().get(1).conquer(g.getPlayers());
        assertEquals(4, g.getBanTile());
        assertEquals(0, g.getArchipelago().get(1).getBanTile());
    }

    /**
     * Tests that an exception is thrown when try to add more than 4 tile
     * to the general tile reserve
     */
    @DisplayName("Tests exception throw when try to add more than 4 ban tile")
    @Test
    void AddingTooManyBanTile() {
        assertThrows(IllegalStateException.class,
                g::addBanTile);
    }

    /**
     * This method test the IncrementMaxMovementCard
     */
    @DisplayName("Increment Max Movement Card")
    @Test
    void IncrementMaxMovementCard(){
        setupFullPlayer();
        g.startGame();
        IncrementMaxMovementCard card = new IncrementMaxMovementCard(g);
        g.setMaxMovement(3);
        card.effect();
        assertEquals(5, g.getMaxMovement());
    }

}
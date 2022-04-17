package it.polimi.ingsw.model;

import it.polimi.ingsw.model.expertGame.ExpertGame;
import it.polimi.ingsw.model.expertGame.NotExistingStudentException;
import it.polimi.ingsw.model.expertGame.PseudoMotherNatureCard;
import it.polimi.ingsw.model.expertGame.StudentsBufferCardsCluster;
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
        assertThrows(NotExistingStudentException.class, () -> manCard.effect());
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
    public void manCardTest3() throws NotExistingStudentException {
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
    public void manCardTest4() throws NotExistingStudentException {
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

        assertThrows(NotExistingStudentException.class, () -> clownCard.effect());
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

        assertThrows(NotExistingStudentException.class, () -> clownCard.effect());
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
    public void clownCardTest3()throws NotExistingStudentException{
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
    public void clownCardTest4()throws NotExistingStudentException{
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
    public void womanCardTest1() throws NotExistingStudentException {
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

        assertThrows(NotExistingStudentException.class, () -> womanCard.effect());
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
    public void womanCardTest3() throws NotExistingStudentException {
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
    public void womanCardTest4() throws NotExistingStudentException {
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

}
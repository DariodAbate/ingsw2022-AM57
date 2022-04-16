package it.polimi.ingsw.model;

import it.polimi.ingsw.model.expertGame.ExpertGame;
import it.polimi.ingsw.model.expertGame.PseudoMotherNatureCard;
import it.polimi.ingsw.model.expertGame.PutThreeStudentsInTheBagCard;
import it.polimi.ingsw.model.expertGame.SwapStudentsCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class ExpertGameTest {
    ExpertGame g;

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
    void PseudoMotherNatureCardTest(){
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
    void PseudoMotherNatureCardTest2(){
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

    /**
     * This method tests the correct working of the SwapStudents card. In particular is tested
     * the case when the player chose to swap two students.
     */
   /* @DisplayName("Tests the correct swapping of two students")  //FIXME i test dipendono da come viene riempita la antrance
    @Test
    void SwapTwoStudentsTest() {
        SwapStudentsCard card = new SwapStudentsCard(2, Color.YELLOW, Color.RED, Color.PINK, Color.BLUE, g);
        setupFullPlayer();
        g.startGame();
        if(g.getCurrentPlayer().getBoard().studentInEntrance(Color.BLUE))
            g.getCurrentPlayer().getBoard().entranceToHall(Color.BLUE);
        else return;
        if(g.getCurrentPlayer().getBoard().studentInEntrance(Color.PINK))
            g.getCurrentPlayer().getBoard().entranceToHall(Color.PINK);
        else return;
        card.effect();
        assertEquals(1 ,g.getCurrentPlayer().getBoard().hallSize(Color.RED));
        assertEquals(1,g.getCurrentPlayer().getBoard().hallSize(Color.YELLOW));
        assertEquals(0,g.getCurrentPlayer().getBoard().hallSize(Color.BLUE));
        assertEquals(0, g.getCurrentPlayer().getBoard().hallSize(Color.PINK));
    }

    /**
     * This method tests the correct working of the SwapStudents card. In particular s tested
     * the case when the player chose to swap one student.
     */
    /* @DisplayName("Tests the correct swapping of one students") //FIXME i test dipendono da come viene riempita la entrance
    @Test
    void SwapOneStudentTest() {
        SwapStudentsCard card = new SwapStudentsCard(1, Color.BLUE, Color.RED, g);
        setupFullPlayer();
        g.startGame();
        if(g.getCurrentPlayer().getBoard().studentInEntrance(Color.RED))
            g.getCurrentPlayer().getBoard().entranceToHall(Color.RED);
        else return;
        card.effect();
        assertEquals(1, g.getCurrentPlayer().getBoard().hallSize(Color.BLUE));
        assertEquals(0, g.getCurrentPlayer().getBoard().hallSize(Color.RED));
    }

    /**
     * This method tests that an exception is thrown when the player try to swap a student
     * that isn't in the hall
     */
    @DisplayName("Tests exception when the selected color isn't available")
    @Test
    void SwapNonExistentStudent() {
        SwapStudentsCard card = new SwapStudentsCard(1, Color.YELLOW, Color.GREEN, g);
        setupFullPlayer();
        g.startGame();
        assertThrows(IllegalArgumentException.class,
                card::effect);
    }
}
package it.polimi.ingsw.model;

import it.polimi.ingsw.model.expertGame.ExpertGame;
import it.polimi.ingsw.model.expertGame.PseudoMotherNatureCard;
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
    public void PseudoMotherNatureCardTest(){
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
    public void PseudoMotherNatureCardTest2(){
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

}
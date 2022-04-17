package it.polimi.ingsw.model;

import it.polimi.ingsw.model.expertGame.ExpertGame;
import it.polimi.ingsw.model.expertGame.InfluenceCardsCluster;
import it.polimi.ingsw.model.statePattern.StandardCalculator;
import it.polimi.ingsw.model.constantFactory.GameConstants;
import it.polimi.ingsw.model.constantFactory.GameConstantsCreator;
import it.polimi.ingsw.model.constantFactory.GameConstantsCreatorThreePlayers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
//STUDENT 0 has BLUE, YELLOW
//STUDENT 1 has RED, GREEN
//STUDENT 2 has PINK
class IslandTileTest {
    ArrayList<Player> playerArrayList = new ArrayList<>();
    StandardCalculator calc = new StandardCalculator();
    IslandTile tempIsland = new IslandTile(calc);
    GameConstantsCreator g;

    @BeforeEach
    void setup(){
        g = new GameConstantsCreatorThreePlayers();
        GameConstants gc = g.create();
        for (int i = 0; i < 3; i++) {
            playerArrayList.add(new Player(i,gc));
        }
        playerArrayList.get(0).getBoard().addProfessor(Color.BLUE);
        playerArrayList.get(0).getBoard().addProfessor(Color.YELLOW);
        playerArrayList.get(1).getBoard().addProfessor(Color.RED);
        playerArrayList.get(1).getBoard().addProfessor(Color.GREEN);
        playerArrayList.get(2).getBoard().addProfessor(Color.PINK);
        playerArrayList.get(0).getBoard().chooseTower(Tower.WHITE);
        playerArrayList.get(1).getBoard().chooseTower(Tower.BLACK);
        playerArrayList.get(2).getBoard().chooseTower(Tower.GRAY);
        calc.setContext(tempIsland);
    }

    /**
     * This method first check:
     * -the influence of a player without a professor
     * - checks the single add for every player
     * - more than one token added for a single player
     * - test the influence with the towers
     * - test the influence if you change the tower color
     */
    @Test
    void checkInfluence() {
        g = new GameConstantsCreatorThreePlayers();
        GameConstants gc = g.create();
        Player player1 = new Player(0, gc);
        tempIsland.add(Color.GREEN);
        tempIsland.add(Color.PINK);
        tempIsland.add(Color.YELLOW);
        //TEST NO PROFESSOR PLAYER
        assertEquals(0, tempIsland.checkInfluence(player1));
        //TEST PROFESSOR PLAYERS
        for (Player player: playerArrayList) {
            assertEquals(1, calc.checkInfluence(player));
        }
        tempIsland.add(Color.RED);
        tempIsland.add(Color.RED);
        tempIsland.add(Color.RED);
        //TEST MORE THAN ONE TOKEN PLAYER
        assertEquals(4, calc.checkInfluence(playerArrayList.get(1)));
        tempIsland.addTower();
        tempIsland.changeTowerColor(Tower.WHITE);
        //TEST ADD TOWER
        assertEquals(2,calc.checkInfluence(playerArrayList.get(0)));
        //TEST CHANGE TOWER
        tempIsland.changeTowerColor(Tower.BLACK);
        assertEquals(1,calc.checkInfluence(playerArrayList.get(0)));
        assertEquals(5,calc.checkInfluence(playerArrayList.get(1)));
    }

    /**
     * This method test the conquest in the following situations:
     * -The island is empty
     * -The island has no tower and an even number of different token(the influence between two players is even)
     * -One player has more influence than the others
     * -The island has some towers, but the influence between the tower holder and the other players is even
     * -The island has some towers, but the influence of one player is bigger than the current holder
     * This method also check the correct add and removal of the towers from the players' board;
     */
    @Test
    void conquer() {
        //TEST EMPTY ISLAND
        tempIsland.conquer(playerArrayList);
        assertEquals(0, tempIsland.getNumTowers());
        //TEST NO EMPTY ISLAND (EVEN TOKENS)
        tempIsland.add(Color.RED);
        tempIsland.add(Color.PINK);
        tempIsland.conquer(playerArrayList);
        assertEquals(0, tempIsland.getNumTowers());
        assertNull(tempIsland.getTowerColor());
        assertThrows(IllegalStateException.class, ()-> tempIsland.changeTowerColor(Tower.BLACK));
        //TEST FILL ISLAND
        tempIsland.add(Color.RED);
        tempIsland.conquer(playerArrayList);
        assertEquals(5, playerArrayList.get(1).getBoard().getNumTower()); //TEST IF TOWER IS CORRECTLY REMOVED
        assertEquals(1, tempIsland.getNumTowers());
        assertEquals(Tower.BLACK, tempIsland.getTowerColor());
        //TEST ALREADY FILLED ISLAND, NO CHANGE HOLDER
        tempIsland.add(Color.YELLOW);
        tempIsland.add(Color.YELLOW);
        tempIsland.add(Color.YELLOW);
        tempIsland.conquer(playerArrayList);
        assertEquals(5, playerArrayList.get(1).getBoard().getNumTower());
        assertEquals(1, tempIsland.getNumTowers());
        assertEquals(Tower.BLACK, tempIsland.getTowerColor());
        //TEST ALREADY FILLED ISLAND, CHANGE HOLDER
        tempIsland.add(Color.YELLOW);
        tempIsland.conquer(playerArrayList);
        assertEquals(6, playerArrayList.get(1).getBoard().getNumTower());
        assertEquals(5, playerArrayList.get(0).getBoard().getNumTower());
        assertEquals(1, tempIsland.getNumTowers());
        assertEquals(Tower.WHITE, tempIsland.getTowerColor());
        //TEST CHANGE AGAIN HOLDER
        tempIsland.add(Color.RED);
        tempIsland.add(Color.RED);
        tempIsland.add(Color.RED);
        tempIsland.add(Color.GREEN);
        tempIsland.conquer(playerArrayList);
        assertEquals(5, playerArrayList.get(1).getBoard().getNumTower());
        assertEquals(6, playerArrayList.get(0).getBoard().getNumTower());
        assertEquals(1, tempIsland.getNumTowers());
        assertEquals(Tower.BLACK, tempIsland.getTowerColor());
    }

    /**
     * This is an helper method
     * @param game ExpertGame
     */
     void setup_influencePattern(ExpertGame game){
         game.addPlayer("Dario");
         game.addPlayer("Luca");
         game.startGame();
         game.getPlayers().get(0).getBoard().addProfessor(Color.BLUE);
         game.getPlayers().get(0).getBoard().addProfessor(Color.YELLOW);
         game.getPlayers().get(1).getBoard().addProfessor(Color.RED);
         game.getPlayers().get(1).getBoard().addProfessor(Color.GREEN);
         game.getPlayers().get(2).getBoard().addProfessor(Color.PINK);
         game.getPlayers().get(0).getBoard().chooseTower(Tower.WHITE);
         game.getPlayers().get(1).getBoard().chooseTower(Tower.BLACK);
         game.getPlayers().get(2).getBoard().chooseTower(Tower.GRAY);
     }
    /**
     * This method test all changes to the InfluenceCalculator pattern and also test the correct behavior of the states
     */
    @DisplayName("Test state pattern")
    @Test
    public void InfluencePattern(){
        //SETUP OF THE GAME
        ExpertGame game = new ExpertGame("Lorenzo", 3);
        InfluenceCardsCluster card = new InfluenceCardsCluster(0,game);
        card.changeColor(Color.YELLOW);
        setup_influencePattern(game);
        game.round.setCurrentTurn(game.getPlayers().get(0));
        IslandTile tempIsland = game.getCurrentIsland();
        tempIsland.add(Color.GREEN);
        tempIsland.add(Color.PINK);
        tempIsland.add(Color.YELLOW);
        tempIsland.add(Color.YELLOW);
        tempIsland.add(Color.BLUE);
        tempIsland.addTower();
        tempIsland.changeTowerColor(Tower.WHITE);
        //STANDARD CALCULATOR
        assertEquals(4, tempIsland.checkInfluence(game.getPlayers().get(0)));
        //NO TOWER CALCULATOR
        card.effect();
        game.getCurrentIsland().changeCalculator(game.calc);
        assertEquals(3, tempIsland.checkInfluence(game.getPlayers().get(0)));
        InfluenceCardsCluster card1 = new InfluenceCardsCluster(1,game);
        //TWO MORE CALCULATOR
        card1.effect();
        game.getCurrentIsland().changeCalculator(game.calc);
        assertEquals(6, tempIsland.checkInfluence(game.getPlayers().get(0)));
        InfluenceCardsCluster card2 = new InfluenceCardsCluster(2,game);
        //COLOR EXCEPTION CALCULATOR
        assertThrows(IllegalStateException.class, card2::effect);
        card2.changeColor(Color.YELLOW);
        card2.effect();
        game.getCurrentIsland().changeCalculator(game.calc);
        assertEquals(2, tempIsland.checkInfluence(game.getPlayers().get(0)));
    }

}
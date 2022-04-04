package it.polimi.ingsw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
//STUDENT 0 has BLUE, YELLOW
//STUDENT 1 has RED, GREEN
//STUDENT 2 has PINK
class IslandTileTest {
    ArrayList<Player> players = new ArrayList<>();
    StandardCalculator calc = new StandardCalculator();
    IslandTile island = new IslandTile(calc);

    @BeforeEach
    void setup(){
        for (int i = 0; i < 3; i++) {
            players.add(new Player(i,3));
        }
        players.get(0).getBoard().addProfessor(Color.BLUE);
        players.get(0).getBoard().addProfessor(Color.YELLOW);
        players.get(1).getBoard().addProfessor(Color.RED);
        players.get(1).getBoard().addProfessor(Color.GREEN);
        players.get(2).getBoard().addProfessor(Color.PINK);
        players.get(0).getBoard().chooseTower(Tower.WHITE);
        players.get(1).getBoard().chooseTower(Tower.BLACK);
        players.get(2).getBoard().chooseTower(Tower.GRAY);
        calc.setContext(island);
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
        Player player1 = new Player(0, 3);
        island.add(Color.GREEN);
        island.add(Color.PINK);
        island.add(Color.YELLOW);
        //TEST NO PROFESSOR PLAYER
        assertEquals(0, island.checkInfluence(player1));
        //TEST PROFESSOR PLAYERS
        for (Player player:players) {
            assertEquals(1, calc.checkInfluence(player));
        }
        island.add(Color.RED);
        island.add(Color.RED);
        island.add(Color.RED);
        //TEST MORE THAN ONE TOKEN PLAYER
        assertEquals(4, calc.checkInfluence(players.get(1)));
        island.addTower();
        island.changeTowerColor(Tower.WHITE);
        //TEST ADD TOWER
        assertEquals(2,calc.checkInfluence(players.get(0)));
        //TEST CHANGE TOWER
        island.changeTowerColor(Tower.BLACK);
        assertEquals(1,calc.checkInfluence(players.get(0)));
        assertEquals(5,calc.checkInfluence(players.get(1)));
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
        island.conquer(players);
        assertEquals(0, island.getNumTowers());
        //TEST NO EMPTY ISLAND (EVEN TOKENS)
        island.add(Color.RED);
        island.add(Color.PINK);
        island.conquer(players);
        assertEquals(0, island.getNumTowers());
        assertThrows(IllegalStateException.class, ()-> island.getTowerColor());
        assertThrows(IllegalStateException.class, ()-> island.changeTowerColor(Tower.BLACK));
        //TEST FILL ISLAND
        island.add(Color.RED);
        island.conquer(players);
        assertEquals(7, players.get(1).getBoard().getNumTower()); //TEST IF TOWER IS CORRECTLY REMOVED
        assertEquals(1, island.getNumTowers());
        assertEquals(Tower.BLACK, island.getTowerColor());
        //TEST ALREADY FILLED ISLAND, NO CHANGE HOLDER
        island.add(Color.YELLOW);
        island.add(Color.YELLOW);
        island.add(Color.YELLOW);
        island.conquer(players);
        assertEquals(7, players.get(1).getBoard().getNumTower());
        assertEquals(1, island.getNumTowers());
        assertEquals(Tower.BLACK, island.getTowerColor());
        //TEST ALREADY FILLED ISLAND, CHANGE HOLDER
        island.add(Color.YELLOW);
        island.conquer(players);
        assertEquals(8, players.get(1).getBoard().getNumTower());
        assertEquals(7, players.get(0).getBoard().getNumTower());
        assertEquals(1, island.getNumTowers());
        assertEquals(Tower.WHITE, island.getTowerColor());
        //TEST CHANGE AGAIN HOLDER
        island.add(Color.RED);
        island.add(Color.RED);
        island.add(Color.RED);
        island.add(Color.GREEN);
        island.conquer(players);
        assertEquals(7, players.get(1).getBoard().getNumTower());
        assertEquals(8, players.get(0).getBoard().getNumTower());
        assertEquals(1, island.getNumTowers());
        assertEquals(Tower.BLACK, island.getTowerColor());
    }

}
package it.polimi.ingsw.model;

import it.polimi.ingsw.model.constantFactory.GameConstants;
import it.polimi.ingsw.model.constantFactory.GameConstantsCreator;
import it.polimi.ingsw.model.constantFactory.GameConstantsCreatorThreePlayers;
import it.polimi.ingsw.model.constantFactory.GameConstantsCreatorTwoPlayers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class RoundTest tests Round
 *
 * @author Luca Bresciani
 */
class RoundTest {
    GameConstantsCreator g;
    GameConstants gc;
    ArrayList<Player> players = new ArrayList<>();
    Round r;

    @BeforeEach
    void setup() {
        g = new GameConstantsCreatorThreePlayers();
        gc = g.create();
    }

    /**
     * This method tests the correct working of the method setActionPhaseOrder()
     * when there is a game with 3 players.
     */
    @Test
    void setActionPhaseOrderTest3Player() {
        Player p1 = new Player(1, gc);
        Player p2 = new Player(2, gc);
        Player p3 = new Player(3, gc);
        p1.playCard(8);
        p2.playCard(0);
        p3.playCard(5);
        players.add(p1);
        players.add(p2);
        players.add(p3);
        r = new Round(players);
        r.setActionPhaseOrder();
        assertEquals(2, r.getActionPhaseOrder().get(0).getId());
        assertEquals(3, r.getActionPhaseOrder().get(1).getId());
        assertEquals(1, r.getActionPhaseOrder().get(2).getId());
        p1.playCard(5);
        p2.playCard(7);
        p3.playCard(2);
        players.add(p1);
        players.add(p2);
        players.add(p3);
        r.setActionPhaseOrder();
        assertEquals(3, r.getActionPhaseOrder().get(0).getId());
    }

    /**
     * This method tests the correct working of setActionPhaseOrder()
     * when there is a game with 2 players.
     */
    @Test
    void setActionPhaseOrderTest2Player() {
        g = new GameConstantsCreatorTwoPlayers();
        GameConstants gc = g.create();
        Player p1 = new Player(1, gc);
        Player p2 = new Player(2, gc);
        p1.playCard(8);
        p2.playCard(0);
        players.add(p1);
        players.add(p2);
        r = new Round(players);
        r.setActionPhaseOrder();
        assertEquals(2, r.getActionPhaseOrder().get(0).getId());
    }

    /**
     * This method tests the correct working of the method setPlanningPhaseOrder()
     * when there is a game with 3 players.
     */
    @Test
    void planningPhaseOrderTest3Player() {
        Player p1 = new Player(1, gc);
        Player p2 = new Player(2, gc);
        Player p3 = new Player(3, gc);
        players.add(p1);
        players.add(p2);
        players.add(p3);
        r = new Round(players);           //Planning order = random first player
        p1.playCard(4);
        p2.playCard(0);
        p3.playCard(9);
        r.setActionPhaseOrder();          //Action order = 213
        r.setPlanningPhaseOrder();        //Planning order = 231
        assertEquals(3, r.getPlanningPhaseOrder().get(1).getId());
        p1.playCard(8);
        p2.playCard(1);
        p3.playCard(0);
        r.setActionPhaseOrder();          //Action order = 321
        r.setPlanningPhaseOrder();        //Planning order = 312
        assertEquals(2, r.getPlanningPhaseOrder().get(2).getId());
    }

    /**
     * This method tests the correct working of the method setPlanningPhaseOrder()
     * when there is a game with 2 players.
     */
    @Test
    void planningPhaseOrderTest2Player() {
        g = new GameConstantsCreatorTwoPlayers();
        GameConstants gc = g.create();
        Player p1 = new Player(27, gc);
        Player p2 = new Player(10, gc);
        p1.playCard(4);
        p2.playCard(0);
        players.add(p1);
        players.add(p2);
        r = new Round(players);
        r.setPlanningPhaseOrder();
        assertEquals(27, r.getPlanningPhaseOrder().get(1).getId());
    }


    @Test
    void nextTurnTest() {
        Game g = new Game("luca", 3);
        Player p1 = new Player(1, gc);
        Player p2 = new Player(2, gc);
        Player p3 = new Player(3, gc);
        players.add(p1);
        players.add(p2);
        players.add(p3);
        r = new Round(players);
        r.setGame(g);//Planning order = random first player//Planning order = random first player
        r.setRefillInterface(g);
        p1.playCard(5);
        r.nextTurn();
        p2.playCard(0);
        r.nextTurn();
        p3.playCard(8);
        r.nextTurn();                 //Action order = 213
        assertEquals(2, r.getCurrentPlayer().getId());
        r.nextTurn();
        assertEquals(1, r.getCurrentPlayer().getId());
        r.nextTurn();
        assertEquals(3, r.getCurrentPlayer().getId());
        assertTrue(r.isRoundEnding());
    }
}
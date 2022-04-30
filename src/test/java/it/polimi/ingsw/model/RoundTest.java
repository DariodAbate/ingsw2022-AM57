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

    /**
     * This method tests the correct working of the method setActionPhaseOrder()
     * when there is a game with 3 players.
     */
    @Test
    void setActionPhaseOrderTest3Player() {
        g = new GameConstantsCreatorThreePlayers();
        GameConstants gc = g.create();
        ArrayList<Player> players = new ArrayList<>();
        Round r;
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
        ArrayList<Player> players = new ArrayList<>();
        Round r;
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
        g = new GameConstantsCreatorThreePlayers();
        GameConstants gc = g.create();
        ArrayList<Player> players = new ArrayList<>();
        Round r;
        Player p1 = new Player(1, gc);
        Player p2 = new Player(2, gc);
        Player p3 = new Player(3, gc);
        p1.playCard(4);
        p2.playCard(0);
        p3.playCard(9);
        players.add(p1);
        players.add(p2);
        players.add(p3);
        r = new Round(players);
        r.setPlanningPhaseOrder();
        assertEquals(3, r.getPlanningPhaseOrder().get(1).getId());
        p1.playCard(8);
        p2.playCard(1);
        p3.playCard(0);
        r.setPlanningPhaseOrder();
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
        ArrayList<Player> players = new ArrayList<>();
        Round r;
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

    /**
     * This method tests the correct working of the method nextTurn.
     */
    @Test
    void nextTurnTest() {
        g = new GameConstantsCreatorThreePlayers();
        GameConstants gc = g.create();
        ArrayList<Player> players = new ArrayList<>();
        Round r;
        Player p1 = new Player(1, gc);
        Player p2 = new Player(2, gc);
        Player p3 = new Player(3, gc);
        p1.playCard(4);
        p2.playCard(0);
        p3.playCard(8);
        players.add(p1);
        players.add(p2);
        players.add(p3);
        r = new Round(players);
        r.setPlanningPhaseOrder(); //setPlanningPhaseOrder --> 231
        assertEquals(2, r.getCurrentPlayer().getId());
        r.nextTurn();
        assertEquals(3,r.getCurrentPlayer().getId());
        r.nextTurn();
        assertEquals(1, r.getCurrentPlayer().getId());
        r.nextTurn();              //setActionPhaseOrder --> 213
        assertEquals(2, r.getCurrentPlayer().getId());
        r.nextTurn();
        assertEquals(1, r.getCurrentPlayer().getId());
        r.nextTurn();
        assertEquals(3, r.getCurrentPlayer().getId());
    }

    @Test
    void nextTurnTest1() {
        g = new GameConstantsCreatorThreePlayers();
        GameConstants gc = g.create();
        ArrayList<Player> players = new ArrayList<>();
        Round r;
        Player p1 = new Player(1, gc);
        Player p2 = new Player(2, gc);
        Player p3 = new Player(3, gc);
        players.add(p1);
        players.add(p2);
        players.add(p3);
        r = new Round(players);        //Planning order = random first player
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
        r.nextTurn();
        //New round, Planning order = 231
        assertEquals(1, r.getRoundNumber());
        assertEquals(2, r.getCurrentPlayer().getId());
        p2.playCard(4);
        r.nextTurn();
        assertEquals(3, r.getCurrentPlayer().getId());
        p3.playCard(2);
        r.nextTurn();
        assertEquals(1, r.getCurrentPlayer().getId());
        p1.playCard(0);
        r.nextTurn();                  //Action order = 132
        assertEquals(1, r.getCurrentPlayer().getId());
        r.nextTurn();
        assertEquals(3, r.getCurrentPlayer().getId());
        r.nextTurn();
        assertEquals(2, r.getCurrentPlayer().getId());
    }
}
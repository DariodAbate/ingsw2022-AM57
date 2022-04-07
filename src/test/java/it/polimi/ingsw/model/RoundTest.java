package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Round;
import it.polimi.ingsw.model.constantFactory.GameConstants;
import it.polimi.ingsw.model.constantFactory.GameConstantsCreator;
import it.polimi.ingsw.model.constantFactory.GameConstantsCreatorThreePlayers;
import it.polimi.ingsw.model.constantFactory.GameConstantsCreatorTwoPlayers;
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
     * This method tests that an Exception is thrown if the list of players
     * didn't contain exactly 2 or 3 players.
     */
    @Test
    void illegalNumberOfPlayers() {
        g = new GameConstantsCreatorThreePlayers();
        GameConstants gc = g.create();
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < 5; i ++) {
            players.add(new Player(i,gc));
        }
        assertThrows(IllegalArgumentException.class,
                () -> {
                    Round r = new Round(players);
                });
    }

    /**
     * This method tests that an Exception in thrown if the given list
     * of players is null.
     */
    @Test
    void nullListOfPlayers() {
        assertThrows(NullPointerException.class,
                () -> {
                    Round r = new Round(null);
                });
    }


    /**
     * This method tests the correct working of the method setActionOrder()
     * when there is a game with 3 players.
     */
    @Test
    void setActionOrderTest3Player() {
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
        r.setActionOrder();
        assertEquals(2, r.getActionOrder().get(0).getId());
        p1.playCard(5);
        p2.playCard(7);
        p3.playCard(2);
        players.add(p1);
        players.add(p2);
        players.add(p3);
        r.setActionOrder();
        assertEquals(3, r.getActionOrder().get(0).getId());
    }

    /**
     * This method tests the correct working of setActionOrder()
     * when there is a game with 2 players.
     */
    @Test
    void setActionOrderTest2Player() {
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
        r.setActionOrder();
        assertEquals(2, r.getActionOrder().get(0).getId());
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

}
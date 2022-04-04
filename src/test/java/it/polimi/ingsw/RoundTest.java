package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class RoundTest tests Round
 *
 * @author Luca Bresciani
 */
class RoundTest {

    /**
     * This method tests that an Exception is thrown if the list of players
     * didn't contain exactly 2 or 3 players.
     */
    @Test
    void illegalNumberOfPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < 5; i ++) {
            players.add(new Player(i,3));
        }
        assertThrows(IllegalArgumentException.class,
                () -> {
                    Round r = new Round(players);
                });
    }

    /**
     * This method tests that an Exception in thrown if the given list
     * of plaers is null.
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
    void setActionOrederTest3Player() {
        ArrayList<Player> players = new ArrayList<>();
        Round r;
        Player p1 = new Player(1, 3);
        Player p2 = new Player(2, 3);
        Player p3 = new Player(3, 3);
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
    void setActionOrederTest2Player() {
        ArrayList<Player> players = new ArrayList<>();
        Round r;
        Player p1 = new Player(1, 2);
        Player p2 = new Player(2, 2);
        p1.playCard(8);
        p2.playCard(0);
        players.add(p1);
        players.add(p2);
        r = new Round(players);
        r.setActionOrder();
        assertEquals(2, r.getActionOrder().get(0).getId());
    }

    /**
     * This method tests the correct working of the method setPianificationOrder()
     * when there is a game with 3 players.
     */
    @Test
    void pianificationOrderTest3Player() {
        ArrayList<Player> players = new ArrayList<>();
        Round r;
        Player p1 = new Player(1, 3);
        Player p2 = new Player(2, 3);
        Player p3 = new Player(3, 3);
        p1.playCard(4);
        p2.playCard(0);
        p3.playCard(9);
        players.add(p1);
        players.add(p2);
        players.add(p3);
        r = new Round(players);
        r.setPianificationOrder();
        assertEquals(3, r.getPianificationOrder().get(1).getId());
        p1.playCard(8);
        p2.playCard(1);
        p3.playCard(0);
        r.setPianificationOrder();
        assertEquals(2, r.getPianificationOrder().get(2).getId());
    }

    /**
     * This method tests the correct working of the method setPianificationOrder()
     * when there is a game with 2 players.
     */
    @Test
    void pianificationOrderTest2Player() {
        ArrayList<Player> players = new ArrayList<>();
        Round r;
        Player p1 = new Player(27, 3);
        Player p2 = new Player(10, 3);
        p1.playCard(4);
        p2.playCard(0);
        players.add(p1);
        players.add(p2);
        r = new Round(players);
        r.setPianificationOrder();
        assertEquals(27, r.getPianificationOrder().get(1).getId());
    }

}
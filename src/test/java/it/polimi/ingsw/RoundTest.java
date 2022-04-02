package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RoundTest {

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

    @Test
    void nullListOfPlayers() {
        assertThrows(NullPointerException.class,
                () -> {
                    Round r = new Round(null);
                });
    }

    @Test
    void noPianificationOrderYet() {
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < 3; i ++) {
            players.add(new Player(i,3));
        }
        Round r = new Round(new ArrayList<>(players));
        assertThrows(NullPointerException.class,
                () -> {
                    r.getPianificationOrder();
                });
    }

    @Test
    void noActionOrderYet() {
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < 5; i ++) {
            players.add(new Player(i,3));
        }
        Round r = new Round(players);
        assertThrows(NullPointerException.class,
                () -> {
                    r.getActionOrder();
                });
    }

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

    @Test
    void testMinPrio() {
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
        r.returnMinPrio();
        assertEquals(1, r.returnMinPrio().getId());
    }
}
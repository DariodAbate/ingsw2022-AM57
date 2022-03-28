package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void chooseBackTest() {
        var player = new Player(1);
        ArrayList<AssistantCard> hand = new ArrayList<>();
        player.chooseBack(CardBack.DRUID);
        hand = player.getHand();
        assertEquals(2, hand.get(3).getMovement());
    }
}
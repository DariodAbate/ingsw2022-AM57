package it.polimi.ingsw;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    Player p;

    @BeforeEach
    void setup(){
         p = new Player("Luca", 2);
    }

    @Test
    void cardBackInitializedNull() {
        assertNull(p.getPlayableCard().get(4).getCardBack());
    }

    @Test
    void cardsInizializationMovement() {
        assertEquals(3, p.getPlayableCard().get(5).getMovement());
    }

    @Test
    void noDiscardCardYet() {
        assertNull(p.viewLastCard());
    }

    @Test
    void choosenCardIndexShouldBeBetween0AndTheLAstCard() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    p.playCard(10);
                });
    }

    @Test
    void cardBackShouldNotBeNull() {
        assertThrows(NullPointerException.class,
                () -> {
                    p.chooseBack(null);
                });
    }

    @Test
    void playedCardBack() {
        p.chooseBack(CardBack.WITCH);
        assertEquals("WITCH", p.playCard(4).getCardBack().toString());
    }
}
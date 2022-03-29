package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void setNickNameNullArgument() {
        Player p = new Player(1);
        assertThrows(NullPointerException.class,
                () -> {
                    p.setNickname(null);
                });
    }

    @Test
    void chooseBack() {
        Player p = new Player(1);
        p.chooseBack(CardBack.DRUID);
        assertEquals("DRUID", p.getPlayableCard().get(6).getCardBack().toString());
    }

    @Test
    void cardBackInitializedNull() {
        Player p = new Player(1);
        assertNull(p.getPlayableCard().get(4).getCardBack());
    }

    @Test
    void nickNameSetter() {
        Player p = new Player(1);
        p.setNickname("foo");
        assertEquals("foo", p.getNickname());
    }

    @Test
    void cardsInizializationMovement() {
        Player p = new Player(1);
        assertEquals(3, p.getPlayableCard().get(5).getMovement());
    }

    @Test
    void noDiscardCardYet() {
        Player p = new Player(1);
        assertNull(p.viewLastCard());
    }

}
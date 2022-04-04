package it.polimi.ingsw;

import it.polimi.ingsw.Model.CardBack;
import it.polimi.ingsw.Model.Player;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class PlayerTest tests Player Class.
 *
 * @author Luca Bresciani
 */
class PlayerTest {
    Player p;

    @BeforeEach
    void setup(){
         p = new Player("foo", 2);
    }

    /**
     * This method tests that an Exception is thrown if an illegal number
     * of player is given as a parameter to the Player constructor.
     */
    @Test
    void illegalNumberOfPlayer() {
        assertThrows(IllegalArgumentException.class,
                () -> p = new Player("foo", 4));
    }

    /**
     * This method tests the logic behind the card movement initialization.
     */
    @Test
    void cardsInizializationMovement() {
        assertEquals(3, p.getPlayableCard().get(5).getMovement());
    }

    /**
     * This method tests that an Exception is thrown if there isn't
     * a discard card yet.
     */
    @Test
    void noDiscardCardYet() {
        assertNull(p.viewLastCard());
    }

    /**
     * This method tests that an Exception is thrown if the index
     * of the chosen card given by the player is negative or is greater
     * than the index of the last playable card.
     */
    @Test
    void choosenCardIndexShouldBeBetween0AndTheLAstCard() {
        assertThrows(IllegalArgumentException.class,
                () -> {
                    p.playCard(10);
                });
    }

    /**
     * This method tests that an Exception is thrown if the parameter
     * passed to the method chooseCardBack(CardBack back) is null.
     */
    @Test
    void cardBackShouldNotBeNull() {
        assertThrows(NullPointerException.class,
                () -> {
                    p.chooseBack(null);
                });
    }

    /**
     * This method tests that both the methods playCard(int chosenCard) and
     * chooseBack(CardBack back) works as expected.
     */
    @Test
    void playedCardBack() {
        p.chooseBack(CardBack.WITCH);
        assertEquals("WITCH", p.playCard(4).getCardBack().toString());
    }
}
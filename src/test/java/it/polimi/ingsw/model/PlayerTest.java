package it.polimi.ingsw.model;

import it.polimi.ingsw.model.CardBack;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.constantFactory.GameConstants;
import it.polimi.ingsw.model.constantFactory.GameConstantsCreator;
import it.polimi.ingsw.model.constantFactory.GameConstantsCreatorTwoPlayers;
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
    GameConstantsCreator gameConstantsCreator;

    @BeforeEach
    void setup(){
        gameConstantsCreator = new GameConstantsCreatorTwoPlayers();
        GameConstants constants = gameConstantsCreator.create();
        p = new Player("Luca", constants);
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

    /**
     * This method tests the correct working of the method addCoin and removeCoin.
     * The method also try to add a coin when shouldn't be added and check that the
     * number of coin stay the same.
     */
    @Test
    void coinTest() {
        p.getBoard().fillEntrance(Color.YELLOW);
        p.getBoard().fillEntrance(Color.YELLOW);
        p.getBoard().fillEntrance(Color.YELLOW);
        p.getBoard().entranceToHall(Color.YELLOW);
        p.getBoard().entranceToHall(Color.YELLOW);
        assertEquals(2, p.getBoard().hallSize(Color.YELLOW));
        p.addCoin();
        assertEquals(1, p.getNumCoin());
        p.removeCoin();
        p.getBoard().entranceToHall(Color.YELLOW);
        p.addCoin();
        p.addCoin();
        assertEquals(1, p.getNumCoin());
    }

    /**
     * This method tests that an exception is thrown when try to
     * remove coins that didn't exist.
     */
    @Test
    void removingTooManyCoins() {
        p.removeCoin();
        assertThrows(IllegalStateException.class,
                () -> p.removeCoin());
    }
}
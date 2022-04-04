package it.polimi.ingsw.Model;

/**
 * The AssistantCard class represent the card of the game.
 *
 * @author Luca Bresciani.
 */
public class AssistantCard {
    private final int movement;
    private final int priority;
    private CardBack back;

    /**
     * TConstructor of the class. It takes 2 parameter which are priority and
     * movement necessary to identifies each card.
     * @param priority is the card's priority
     * @param movement is the card's permitted maximum movement
     */
    public AssistantCard(int priority, int movement) {
        this.movement = movement;
        this.priority = priority;
    }

    /**
     *
     * @return card's maximum permitted movement
     */
    public int getMovement() {
        return this.movement;
    }

    /**
     *
     * @return card' priority
     */
    public int getPriority() {
        return this.priority;
    }

    /**
     * This method set the card back.
     * @param back is the chosen back
     */
    public void setCardBack(CardBack back) {
        this.back = back;
    }

    /**
     *
     * @return the card's back
     */
    public CardBack getCardBack() {
        return this.back;
    }
}

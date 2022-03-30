package it.polimi.ingsw;

public class AssistantCard {
    private final int movement;
    private final int priority;
    private CardBack back;

    public AssistantCard(int priority, int movement) {
        this.movement = movement;
        this.priority = priority;
    }

    public int getMovement() {
        return this.movement;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setCardBack(CardBack back) {
        this.back = back;
    }

    public CardBack getCardBack() {
        return this.back;
    }
}

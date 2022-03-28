package it.polimi.ingsw;

public class AssistantCard {
    private int movement;
    private int priority;
    private CardBack back;
    private boolean hasBeenPlayed;

    public AssistantCard(int priority, int movement, CardBack back) {
        this.movement = movement;
        this.priority = priority;
        this.back = back;
        hasBeenPlayed = false;
    }

    public int getMovement() {
        return this.movement;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setHasBeenPlayed() {
        hasBeenPlayed = true;
    }

    public boolean getHasBeenPlayed() {
        return hasBeenPlayed;
    }
}

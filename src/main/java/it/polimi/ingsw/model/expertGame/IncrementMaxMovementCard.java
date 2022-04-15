package it.polimi.ingsw.model.expertGame;

/**
 * This class represents the Increment Max Movement Card, which effect is to increments the maximum movement possible
 * for the player in that turn.
 */
public class IncrementMaxMovementCard extends ExpertCard {
    private final IncrementMaxMovement game;

    /**
     * This constructor has the default price, and uses a reference to IncrementMaxMovement
     * @param game reference to game
     */
    public IncrementMaxMovementCard(ExpertGame game){
        super(1);
        this.game = game;
    }

    /**
     * This effect increments the maxMovement of the player by 2
     * It also increments its price by 1 if this card is never be played in this game
     */
    @Override
    public void effect() {
        if(!isPlayed()){
            played = true;
            price += 1;
        }
        game.plus2MaxMovement();
    }
}

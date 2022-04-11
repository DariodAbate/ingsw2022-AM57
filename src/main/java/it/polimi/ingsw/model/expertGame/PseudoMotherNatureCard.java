package it.polimi.ingsw.model.expertGame;

/**
 * This class represents the PseudoMotherNature card
 * @author Lorenzo Corrado
 */
public class PseudoMotherNatureCard extends ExpertCard {
    private int islandIndex;
    private final PseudoMotherNature game;

    /**
     * This constructor creates a card with the default price of 3 and the starting index.
     * The reference to game is handled with the interface PseudoMotherNature
     * @param islandIndex default index of the island where the effect is gonna be activated
     * @param game reference to the instance of game
     */
    public PseudoMotherNatureCard(int islandIndex, PseudoMotherNature game){
        super(3);
        this.islandIndex = islandIndex;
        this.game = game;
    }

    /**
     * Change the island where the player wants the effect to be activated
     * @param index of the island
     */
    public void changeIslandIndex(int index){
        this.islandIndex = index;
    }

    /**
     * This effect simulates the merging and conquer as if mother nature is on the island selected by the player
     * It also increments its price by 1 if this card is never be played in this game
     */
    @Override
    public void effect() {
        if(isPlayed()){
            played = true;
            price += 1;
        }
        game.independentMerge(islandIndex);
    }
}

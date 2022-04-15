package it.polimi.ingsw.model.expertGame;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.IslandTile;

import java.util.ArrayList;

/**
 * This subclass of game is instantiated when selecting Expert Mode, it adds the coin and expert cards system
 * @author Lorenzo Corrado
 */
public class ExpertGame extends Game implements PseudoMotherNature, IncrementMaxMovement{

    private int coinBank;
    private ArrayList<ExpertCard> expertCards;

    /**
     * This constructor adds coins and expert Cards
     * @param nickPlayer Name of the first player to create the lobby
     * @param numGamePlayers Number of players
     */
    public ExpertGame(String nickPlayer, int numGamePlayers){
        super(nickPlayer, numGamePlayers);
        initBank();
    }

    /**
     * This method initializes the coinBank
     */
    private void initBank(){
        this.coinBank = gameConstants.getMaxCoinSize();
    }

    /**
     * This method initializes the cards in the game
     */
    private void pickCards(){
        expertCards = new ArrayList<>();
        //card generation
    }

    /**
     * This method is called by the controller to activate the effect of a card, identified by an index
     * @param indexCard represent a character card if the effect to activate
     */
        public void playEffect(int indexCard){
        expertCards.get(indexCard).effect();
    }

    /**
     * This method increments the max movement possible for the player by 2
     * Is utilized from Increment Max Movement Card
     */
    @Override
    public void plus2MaxMovement(){
        this.maxMovement = maxMovement+2;
    }

    /**
     * This method merges create a pseudo mother nature for the purpose of conquering and merging the islands
     * @param i The island where is gonna be placed the pseudo mother nature
     */
    @Override
    public void independentMerge(int i){
        int tempMotherNature = motherNature;//store the initial motherNature
        IslandTile tempIsland = getCurrentIsland();
        motherNature = i;
        getCurrentIsland().conquer(players);
        mergeIslandTile();
        if(archipelago.contains(tempIsland)){
            motherNature = archipelago.indexOf(tempIsland);
        }
    }
}

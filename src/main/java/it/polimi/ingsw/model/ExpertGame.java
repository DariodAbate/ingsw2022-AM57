package it.polimi.ingsw.model;

import it.polimi.ingsw.model.constantFactory.GameConstants;

import java.util.ArrayList;

/**
 * This subclass of game is instantiated when selecting Expert Mode, it adds the coin and expert cards system
 * @author Lorenzo Corrado
 */
public class ExpertGame extends Game{
    private int coinBank;
    private ArrayList<ExpertCard> expertCards;

    /**
     * This constructor adds coins and expert Cards
     * @param nickPlayer Name of the first player to create the lobby
     * @param numGamePlayers Number of partecipating players
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
     * @param indexCard represent a character card of the effect to activate
     */
    public void playEffect(int indexCard){ //FIXME management of index
        expertCards.get(indexCard).effect();
    }
}

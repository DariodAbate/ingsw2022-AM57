package it.polimi.ingsw.model.statePattern;

import it.polimi.ingsw.model.IslandTile;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.RoundObserver;
import it.polimi.ingsw.model.expertGame.ExpertCard;
import it.polimi.ingsw.model.expertGame.InfluenceCardsCluster;

/**
 * This class extends a standard calculator adding 2 more influence point to the count
 * @author Lorenzo Corrado
 */
public class TwoMoreCalculator extends StandardCalculator{
    private Player currentPlayer;
    private InfluenceCardsCluster card;
    public TwoMoreCalculator(InfluenceCardsCluster card){
        this.card = card;

    }
    /**
     * Set the island where he needs to calculate the influence
     * @param island where the influence will be calculated
     */
    @Override
    public void setContext(IslandTile island) {
        super.setContext(island);
    }

    /**
     * Method that check how much influence has one player
     * This variant adds 2 more influence points to the total
     * @param player Calculate the influence of this player
     * @return The value of the influence
     */
    @Override
    public int checkInfluence(Player player) {
        currentPlayer = card.getRound().getRoundPlayer();
        if(currentPlayer == player) return super.checkInfluence(player) + 2;
        return super.checkInfluence(player);
    }
}

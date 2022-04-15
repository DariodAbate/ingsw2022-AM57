package it.polimi.ingsw.model.statePattern;

import it.polimi.ingsw.model.IslandTile;
import it.polimi.ingsw.model.Player;

/**
 * This class extends a standard calculator adding 2 more influence point to the count
 * @author Lorenzo Corrado
 */
public class TwoMoreCalculator extends StandardCalculator{
    public TwoMoreCalculator(){

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
        return super.checkInfluence(player) + 2;
    }
}

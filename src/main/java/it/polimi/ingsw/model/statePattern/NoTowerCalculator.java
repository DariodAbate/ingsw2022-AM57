package it.polimi.ingsw.model.statePattern;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.IslandTile;
import it.polimi.ingsw.model.Player;

/**
 * This class implements the influence calculator that ignores towers
 * @author Lorenzo Corrado
 */

public class NoTowerCalculator implements InfluenceCalculator{
    private IslandTile island;

    public NoTowerCalculator(){
    }

    /**
     * Set the island where he needs to calculate the influence
     * @param island where the influence will be calculated
     */
    @Override
    public void setContext(IslandTile island) {
        this.island = island;
    }

    /**
     * Method that check how much influence has one player
     * This variant ignores the towers
     * @param player Calculate the influence of this player
     * @return The value of the influence
     */
    @Override
    public int checkInfluence(Player player) {
        int sum = 0;
        Board board = player.getBoard();
        for (Color color:Color.values()) {
            if (board.getProfessors().contains(color)){
                sum+= island.getInfluenceColor(color);
            }
        }
        return sum;

    }
}

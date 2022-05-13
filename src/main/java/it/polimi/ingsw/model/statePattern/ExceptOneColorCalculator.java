package it.polimi.ingsw.model.statePattern;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.IslandTile;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.expertGame.InfluenceCardsCluster;

import java.io.Serializable;

/**
 * This class implements a calculator that ignores one single kind of color in the checkInfluence method
 * @author Lorenzo Corrado
 */
public class ExceptOneColorCalculator implements InfluenceCalculator, Serializable {
    private IslandTile island;
    private Color color;
    private final InfluenceCardsCluster card;
    public ExceptOneColorCalculator(InfluenceCardsCluster card){
        this.card = card;
        this.island = null;
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
     * This variant ignores a single student color
     * @param player Calculate the influence of this player
     * @return The value of the influence
     */
    @Override
    public int checkInfluence(Player player) {
        this.color = card.getColor();
        int sum = 0;
        Board board = player.getBoard();
        if(island.getNumTowers()==0){
            sum+=0;
        }
        else{
            if(island.getTowerColor() == board.getTowerColor()) sum += island.getInfluenceTower();
        }
        for (Color color:Color.values()) {
            if(color != this.color) {
                if (board.getProfessors().contains(color)) {
                    sum += island.getInfluenceColor(color);
                }
            }
        }
        return sum;
    }
}

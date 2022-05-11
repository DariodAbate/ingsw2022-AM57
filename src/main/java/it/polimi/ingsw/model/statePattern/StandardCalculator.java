package it.polimi.ingsw.model.statePattern;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.IslandTile;
import it.polimi.ingsw.model.Player;

import java.io.Serializable;

/** The Standard checkInfluence calculator state
 * @author Lorenzo Corrado
 *
 */

public class StandardCalculator implements InfluenceCalculator, Serializable {
    protected IslandTile island;

    public StandardCalculator(){
    }
    public void setContext(IslandTile island){
        this.island = island;
    }

    /**
     * This is the standard checkInfluence, calculates either towers and students
     * @param player Calculate influence of that player
     * @return The value of the influence
     */
    @Override
    public int checkInfluence(Player player) {
        int sum = 0;
        Board board = player.getBoard();
        if(island.getNumTowers()==0){
            sum+=0;
        }
        else{
            if(island.getTowerColor() == board.getTowerColor()) sum += island.getInfluenceTower();
        }
        for (Color color:Color.values()) {
            if (board.getProfessors().contains(color)){
                sum+= island.getInfluenceColor(color);
            }
        }
        return sum;
    }
}

package it.polimi.ingsw.model.statePattern;

import it.polimi.ingsw.model.IslandTile;
import it.polimi.ingsw.model.Player;

/**
 * This class is an interface for the state pattern
 * @author Lorenzo Corrado
 */
public interface InfluenceCalculator {
    public int checkInfluence(Player player);
    public void setContext(IslandTile island);
}

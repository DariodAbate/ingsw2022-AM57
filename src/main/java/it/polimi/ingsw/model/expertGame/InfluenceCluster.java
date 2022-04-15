package it.polimi.ingsw.model.expertGame;

import it.polimi.ingsw.model.statePattern.InfluenceCalculator;

/**
 * This interface only has one method to change the calculator inside game (the current calculator)
 */
public interface InfluenceCluster {
    public void changeCalculator(InfluenceCalculator calc); //is about Game.class
}

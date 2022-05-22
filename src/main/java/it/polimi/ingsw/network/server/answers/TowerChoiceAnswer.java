package it.polimi.ingsw.network.server.answers;

import it.polimi.ingsw.model.Tower;

import java.util.ArrayList;
/**
 * This class represent the answer given to the clients that lets them choose their tower color
 *
 * @author Dario d'Abate
 */
public class TowerChoiceAnswer implements Answer{
    private final ArrayList<Tower> towerChoices;

    public TowerChoiceAnswer(ArrayList<Tower> towerChoices){
        this.towerChoices = new ArrayList<>(towerChoices);
    }
    @Override
    public Object getMessage() {
        return towerChoices;
    }
}

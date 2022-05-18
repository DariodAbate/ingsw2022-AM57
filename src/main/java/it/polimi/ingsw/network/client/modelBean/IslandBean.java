package it.polimi.ingsw.network.client.modelBean;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;

import java.util.HashMap;
import java.util.Map;


/**
 * This class is a simplified representation of the model in the server. It will be used by the CLI or the GUI.
 * This class represent an island tile
 *
 * @author Dario d'Abate
 */
public class IslandBean {
    private Map<Color, Integer > students;
    private Tower towerColor;
    private int numTowers;
    private boolean banToken;

    public IslandBean(){
        students = new HashMap<>();
    }


    public Map<Color, Integer> getStudents() {
        return students;
    }

    public void setStudents(Map<Color, Integer> students) {
        this.students = students;
    }

    public Tower getTowerColor() {
        return towerColor;
    }

    public void setTowerColor(Tower towerColor) {
        this.towerColor = towerColor;
    }

    public int getNumTowers() {
        return numTowers;
    }

    public void setNumTowers(int numTowers) {
        this.numTowers = numTowers;
    }

    public boolean isBanToken() {
        return banToken;
    }

    public void setBanToken(boolean banToken) {
        this.banToken = banToken;
    }
}

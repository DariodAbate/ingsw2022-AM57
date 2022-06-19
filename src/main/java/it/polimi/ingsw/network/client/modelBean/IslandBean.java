package it.polimi.ingsw.network.client.modelBean;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * This class is a simplified representation of the model in the server. It will be used by the CLI or the GUI.
 * This class represent an island tile
 *
 * @author Dario d'Abate
 */
public class IslandBean implements Serializable {
    private Map<Color, Integer > students;
    private Tower towerColor;
    private int numTowers;
    private int banToken;

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

    public int  getBanToken() {
        return banToken;
    }

    public boolean  isBanToken() {
        return banToken > 0;
    }

    public void setBanToken(int banToken) {
        this.banToken = banToken;
    }
}

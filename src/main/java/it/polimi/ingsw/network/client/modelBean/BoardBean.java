package it.polimi.ingsw.network.client.modelBean;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tower;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class is a simplified representation of the model in the server. It will be used by the CLI or the GUI.
 * This class represent the board
 *
 * @author Dario d'Abate
 */
public class BoardBean  implements Serializable {
    private Map<Color, Integer > entranceStudent;
    private Map<Color, Integer > hallStudent;
    private Set<Color> professors;
    private Tower towerColor;
    private int numTowers;
    private int numCoins;

    public BoardBean(){
        entranceStudent = new HashMap<>();
        hallStudent = new HashMap<>();
    }

    public Map<Color, Integer> getEntranceStudent() {
        return entranceStudent;
    }

    public void setEntranceStudent(Map<Color, Integer> entranceStudent) {
        this.entranceStudent = entranceStudent;
    }

    public Map<Color, Integer> getHallStudent() {
        return hallStudent;
    }

    public void setHallStudent(Map<Color, Integer> hallStudent) {
        this.hallStudent = hallStudent;
    }

    public void setTowerColor(Tower towerColor) {
        this.towerColor = towerColor;
    }

    public Tower getTowerColor() {
        return towerColor;
    }

    public int getNumTowers() {
        return numTowers;
    }

    public void setNumTowers(int numTowers) {
        this.numTowers = numTowers;
    }

    public int getNumCoins() {
        return numCoins;
    }

    public void setNumCoins(int numCoins) {
        this.numCoins = numCoins;
    }

    public Set<Color> getProfessors() {
        return professors;
    }

    public void setProfessors(Set<Color> professors) {
        this.professors = professors;
    }
}

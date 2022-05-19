package it.polimi.ingsw.network.server.answers;

import it.polimi.ingsw.model.Tower;

import java.util.ArrayList;

/**
 * This class represent the answer provided to the clients when a player chose his tower color.
 * Provides the following attributes:
 * -The nickname of the player who chose the tower color
 * -The tower color chosen by the player
 * -The remaining Tower colors
 * @author Lorenzo Corrado
 */
public class TowerColorAnswer implements Answer{
    private String nickname;
    private Tower tower;
    private ArrayList<Tower> remainingTowerColors;

    public TowerColorAnswer(String nickname, Tower tower, ArrayList<Tower> remainingTowerColors){
        this.nickname = nickname;
        this.tower = tower;
        this.remainingTowerColors = remainingTowerColors;
    }

    @Override
    public Object getMessage() {
        return null;
    }

    public String getNickname() {
        return nickname;
    }

    public Tower getTower() {
        return tower;
    }
}

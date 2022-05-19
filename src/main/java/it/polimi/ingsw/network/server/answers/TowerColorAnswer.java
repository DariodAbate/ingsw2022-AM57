package it.polimi.ingsw.network.server.answers;

import it.polimi.ingsw.model.Tower;

/**
 * This class represent the answer provided to the clients when a player chose his tower color.
 * Provides the following attributes:
 * -The nickname of the player who chose the tower color
 * -The tower color chosen by the player
 * @author Lorenzo Corrado
 */
public class TowerColorAnswer implements Answer{
    private String nickname;
    private Tower tower;

    public TowerColorAnswer(String nickname, Tower tower){
        this.nickname = nickname;
        this.tower = tower;
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

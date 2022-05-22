package it.polimi.ingsw.network.server.answers;

import it.polimi.ingsw.network.client.modelBean.BoardBean;
import it.polimi.ingsw.network.client.modelBean.IslandBean;

import java.util.ArrayList;

/**
 * This class represent the answer given to the clients when one player moves a student from the entrance to an island.
 * This class provides you with the following attributes:
 * -The name of the player who made the action
 * -The board updated
 * -The archipelago updated
 * @author Dario d'Abate
 */
public class ToIslandUpdateAnswer implements Answer{
    String nickname;
    BoardBean updatedBoard;
    ArrayList<IslandBean> updatedArchipelago;

    public ToIslandUpdateAnswer(String nickname, BoardBean updatedBoard, ArrayList<IslandBean> updatedArchipelago){
        this.nickname = nickname;
        this.updatedBoard = updatedBoard;
        this.updatedArchipelago = updatedArchipelago;
    }

    public String getNickname() {
        return nickname;
    }

    public BoardBean getUpdatedBoard() {
        return updatedBoard;
    }

    public ArrayList<IslandBean> getUpdatedArchipelago() {
        return updatedArchipelago;
    }

    @Override
    public Object getMessage() {
        return null;
    }
}

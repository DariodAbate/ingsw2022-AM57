package it.polimi.ingsw.network.server.answers.update;

import it.polimi.ingsw.network.client.modelBean.BoardBean;
import it.polimi.ingsw.network.server.answers.Answer;

import java.util.ArrayList;

/**
 * This class represent the answer given to the clients when one player moves a student from the entrance to the hall.
 * This class provides you with the following attributes:
 * -The name of the player who made the action
 * -The boards updated
 * @author Dario d'Abate
 */
public class ToHallUpdateAnswer implements Answer {
    String nickname;
    ArrayList<BoardBean> updatedBoardList;

    public ToHallUpdateAnswer(String nickname, ArrayList<BoardBean> updatedBoard){
        this.nickname = nickname;
        this.updatedBoardList = updatedBoard;
    }

    public String getNickname() {
        return nickname;
    }

    public ArrayList<BoardBean> getUpdatedBoardList() {
        return updatedBoardList;
    }

    @Override
    public Object getMessage() {
        return null;
    }
}

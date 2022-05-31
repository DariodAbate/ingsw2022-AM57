package it.polimi.ingsw.network.server.answers.update;

import it.polimi.ingsw.network.client.modelBean.BoardBean;
import it.polimi.ingsw.network.server.answers.Answer;

/**
 * This class represent the answer given to the clients when one player moves a student from the entrance to the hall.
 * This class provides you with the following attributes:
 * -The name of the player who made the action
 * -The board updated
 * @author Dario d'Abate
 */
public class ToHallUpdateAnswer implements Answer {
    String nickname;
    BoardBean updatedBoard;

    public ToHallUpdateAnswer(String nickname, BoardBean updatedBoard){
        this.nickname = nickname;
        this.updatedBoard = updatedBoard;
    }

    public String getNickname() {
        return nickname;
    }

    public BoardBean getUpdatedBoard() {
        return updatedBoard;
    }

    @Override
    public Object getMessage() {
        return null;
    }
}

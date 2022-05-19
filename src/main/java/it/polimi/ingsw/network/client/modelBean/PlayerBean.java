package it.polimi.ingsw.network.client.modelBean;

import it.polimi.ingsw.model.AssistantCard;
import it.polimi.ingsw.model.Board;

import java.util.ArrayList;


/**
 * This class is a simplified representation of the model in the server. It will be used by the CLI or the GUI.
 * This class represent a player
 *
 * @author Dario d'Abate
 */
public class PlayerBean {
    private String nickname;
    private Board board;
    private ArrayList<AssistantCard> hand;

    public PlayerBean(){
        hand = new ArrayList<>();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public ArrayList<AssistantCard> getHand() {
        return hand;
    }

    public void setHand(ArrayList<AssistantCard> hand) {
        this.hand = hand;
    }
}

package it.polimi.ingsw.network.client.modelBean;

import it.polimi.ingsw.model.AssistantCard;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * This class is a simplified representation of the model in the server. It will be used by the CLI or the GUI.
 * This class represent a player
 *
 * @author Dario d'Abate
 */
public class PlayerBean implements Serializable {
    private String nickname;
    private BoardBean board;
    private ArrayList<AssistantCard> hand;
    private AssistantCard playedCard;

    public PlayerBean(){
        hand = new ArrayList<>();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public BoardBean getBoard() {
        return board;
    }

    public void setBoard(BoardBean board) {
        this.board = board;
    }

    public ArrayList<AssistantCard> getHand() {
        return hand;
    }

    public void setHand(ArrayList<AssistantCard> hand) {
        this.hand = hand;
    }

    public void setPlayedCard(AssistantCard playedCard){
        this.playedCard = playedCard;
    }

    public AssistantCard getPlayedCard() {
        return playedCard;
    }
}

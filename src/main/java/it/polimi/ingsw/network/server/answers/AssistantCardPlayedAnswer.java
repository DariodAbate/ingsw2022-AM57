package it.polimi.ingsw.network.server.answers;

import it.polimi.ingsw.model.AssistantCard;

import java.util.ArrayList;

/**
 * This class represent the answer given to the clients when one player plays an assistant card.
 * This class provides you with the following attributes:
 * -The name of the player who played the card
 * -The remaining hand of the player who played the card
 * -The PRIORITY of the card played from the player (it identifies the assistant card played)
 * @author Lorenzo Corrado
 */
public class AssistantCardPlayedAnswer implements Answer{
    private String nickname;
    private ArrayList<AssistantCard> hand;
    private int card;

    public AssistantCardPlayedAnswer(String nickname, ArrayList<AssistantCard> hand, int card) {
        this.nickname = nickname;
        this.hand = hand;
        this.card = card;
    }

    @Override
    public Object getMessage() {
        return null;
    }

    public String getNickname() {
        return nickname;
    }

    public ArrayList<AssistantCard> getHand() {
        return hand;
    }

    public int getCard() {
        return card;
    }
}

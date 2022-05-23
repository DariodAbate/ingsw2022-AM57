package it.polimi.ingsw.network.server.answers;

import it.polimi.ingsw.model.AssistantCard;

import java.util.ArrayList;

/**
 * This class represent the answer given to the clients when one player plays an assistant card.
 * This class provides you with the following attributes:
 * -The name of the player who played the card
 * -The remaining hand of the player who played the card
 * -The LAST played card
 * @author Lorenzo Corrado
 */
public class AssistantCardPlayedAnswer implements Answer{
    private final String nickname;
    private final ArrayList<AssistantCard> hand;
    private final AssistantCard  playedCard;

    public AssistantCardPlayedAnswer(String nickname, ArrayList<AssistantCard> hand, AssistantCard playedCard) {
        this.nickname = nickname;
        this.hand = hand;
        this.playedCard = playedCard;
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

    public AssistantCard getCard() {
        return playedCard;
    }
}

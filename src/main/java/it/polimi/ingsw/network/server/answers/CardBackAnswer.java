package it.polimi.ingsw.network.server.answers;

import it.polimi.ingsw.model.CardBack;

/**
 *  * This class represent the answer provided to the clients when a player chose his card back.
 *  * Provides the following attributes:
 *  * -The nickname of the player who chose the card back
 *  * -The card back chosen by the player
 * @author Lorenzo Corrado
 */
public class CardBackAnswer implements Answer{
    private String nickname;
    private CardBack card;

    public CardBackAnswer(String nickname, CardBack card){
        this.nickname = nickname;
        this.card = card;
    }

    @Override
    public Object getMessage() {
        return null;
    }

    public String getNickname() {
        return nickname;
    }

    public CardBack getCard() {
        return card;
    }

}

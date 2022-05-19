package it.polimi.ingsw.network.server.answers;

import it.polimi.ingsw.model.CardBack;

import java.util.ArrayList;

/**
 *  * This class represent the answer provided to the clients when a player chose his card back.
 *  * Provides the following attributes:
 *  * -The nickname of the player who chose the card back
 *  * -The card back chosen by the player
 *  - The remaining card backs
 * @author Lorenzo Corrado
 */
public class CardBackAnswer implements Answer{
    private String nickname;
    private CardBack card;
    private ArrayList<CardBack> remainingCardBacks;

    public CardBackAnswer(String nickname, CardBack card, ArrayList<CardBack> remainingCardBacks){
        this.remainingCardBacks = remainingCardBacks;
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

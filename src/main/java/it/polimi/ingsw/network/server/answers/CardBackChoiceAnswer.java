package it.polimi.ingsw.network.server.answers;

import it.polimi.ingsw.model.CardBack;

import java.util.ArrayList;

/**
 * This class represent the answer given to the clients that lets them choose their card back
 *
 * @author Dario d'Abate
 */
public class CardBackChoiceAnswer implements Answer{
    private final ArrayList<CardBack> cardBackChoices;

    public CardBackChoiceAnswer( ArrayList<CardBack> cardBackChoices){
        this.cardBackChoices = cardBackChoices;
    }

    @Override
    public Object getMessage() {
        return cardBackChoices;
    }
}

package it.polimi.ingsw.network.client.messages;

import it.polimi.ingsw.model.CardBack;

public class ChooseCardBack {
    private CardBack card;

    public ChooseCardBack(CardBack card){
        this.card = card;
    }

    public CardBack getMessage() {
        return card;
    }
}

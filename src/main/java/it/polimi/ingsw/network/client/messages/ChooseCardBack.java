package it.polimi.ingsw.network.client.messages;


public class ChooseCardBack implements Message{
    private final String card;

    public ChooseCardBack(String card){
        this.card = card;
    }

    public String getMessage() {
        return card;
    }
}

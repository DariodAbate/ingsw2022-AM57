package it.polimi.ingsw.network.client.messages;

import it.polimi.ingsw.model.Tower;

public class ChooseTowerColor implements Message{
    private Tower color;
    public ChooseTowerColor(Tower color){
        this.color = color;
    }

    public Tower getColor() {
        return color;
    }
}

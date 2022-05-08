package it.polimi.ingsw.network.client.messages;

import it.polimi.ingsw.model.Color;

public class ColorChosen implements Message{
    private final Color color;

    public ColorChosen(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}

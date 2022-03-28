package it.polimi.ingsw;

public class AlreadyPlayedCard extends Exception{
    @Override
    public String getMessage() {
        return ("You already played this card");
    }
}

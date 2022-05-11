package it.polimi.ingsw.network.server.answers;

public class Shutdown implements Answer{
    private final String message;

    public Shutdown(String message){
        this.message=message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

package it.polimi.ingsw.network.server.answers;

/**
 * This class represent the answer given to the clients to disconnect them from the server.
 *
 * @author Dario d'Abate
 */
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

package it.polimi.ingsw.network.server.answers;

/**
 * This message is sent to the client while a game has not started. So if a client crashes, the server
 * can unregister it
 */
public class Pong implements Answer{
    @Override
    public Object getMessage() {
        return null;
    }
}

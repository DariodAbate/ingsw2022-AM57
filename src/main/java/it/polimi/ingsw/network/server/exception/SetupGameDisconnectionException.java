package it.polimi.ingsw.network.server.exception;

/**
 * This Exception is thrown when there's a disconnection that do not cause the saving mechanism of a game
 *
 * @author  Dario d'Abate
 */
public class SetupGameDisconnectionException extends Exception{
    public SetupGameDisconnectionException(){
        super();
    }
}

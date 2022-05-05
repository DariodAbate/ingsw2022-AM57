package it.polimi.ingsw.network.server.answers;

import java.io.Serializable;

/**
 * This class represents the generic answer server to client
 * @author Lorenzo Corrado
 */
public interface Answer extends Serializable {
    /**
     *
     * @return the message of the Answer
     */
    Object getMessage();
}

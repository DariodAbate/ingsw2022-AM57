package it.polimi.ingsw.model.statePattern;

/**
 * Thrown to indicate that the hall in the board cannot contain a student of the specified color
 * @author Dario d'Abate
 */
public class HallFullException extends Exception{
    public HallFullException(String errorMessage){
        super(errorMessage);
    }
}

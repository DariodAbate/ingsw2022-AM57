package it.polimi.ingsw.model.expertGame;

/**
 * Thrown to indicate that a student of the specified color does not exist
 * @author Dario d'Abate
 */
public class NotExistingStudentException extends Exception{
    public NotExistingStudentException(String errorMessage){
        super(errorMessage);
    }
}

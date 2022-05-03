package it.polimi.ingsw.answers;
/**
 * This class represents a string sent from the server to the client, used for a generic message
 * @author Lorenzo Corrado
 */
public class GenericAnswer implements Answer {
    private final String message;

    public GenericAnswer(String message){
        this.message=message;
    }

    /**
     *
     * @return the message in the form of a string
     */
    @Override
    public String getMessage() {
        return message;
    }
}

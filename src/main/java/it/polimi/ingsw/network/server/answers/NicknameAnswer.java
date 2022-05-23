package it.polimi.ingsw.network.server.answers;

/**
 * This class represent the answer given to the clients to associate their nickname with the UI.

 * @author Dario d'Abate
 */
public class NicknameAnswer implements Answer{
    private final String nickname;

    public NicknameAnswer(String nickname){
        this.nickname = nickname;
    }

    @Override
    public Object getMessage() {
        return nickname;
    } //used to associate the nickname to the cli or gui

}

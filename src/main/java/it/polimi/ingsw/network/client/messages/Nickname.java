package it.polimi.ingsw.network.client.messages;
/**
 * This class is a message client to server that setups connection for the player with provided nickname.
 *
 * @author Lorenzo Corrado
 * @see Message
 */
public class Nickname implements Message{
    private final String nickname;

    public Nickname(String nick){
        this.nickname = nick;
    }

    /**
     * @return the nickname chosen from the player
     */
    public String getNickname() {
        return nickname;
    }
}

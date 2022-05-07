package it.polimi.ingsw.network.client.messages;



public class IntegerMessage implements Message {
    private final int msg;
    public IntegerMessage(int msg){
        this.msg = msg;
    }

    public int getMessage() {
        return msg;
    }
}

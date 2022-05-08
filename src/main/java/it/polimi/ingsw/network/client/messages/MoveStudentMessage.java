package it.polimi.ingsw.network.client.messages;

public class MoveStudentMessage implements Message{
    private final String msg;

    public MoveStudentMessage(String msg){
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }

}

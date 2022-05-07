package it.polimi.ingsw.network.client.messages;



public class PlayAssistantCard implements Message {
    private int idxCard;
    public PlayAssistantCard(int idxCard){
        this.idxCard = idxCard;
    }

    public int getMessage() {
        return idxCard;
    }
}

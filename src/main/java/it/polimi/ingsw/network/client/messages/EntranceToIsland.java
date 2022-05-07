package it.polimi.ingsw.network.client.messages;

public class EntranceToIsland implements Message{
    private int idxIsland;
    public EntranceToIsland(int idxIsland){
        this.idxIsland = idxIsland;
    }

    public int getIdxIsland() {
        return idxIsland;
    }
}

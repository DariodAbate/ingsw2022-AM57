package it.polimi.ingsw.network.client.messages;

public class IndexIsland implements Message{
    private int idxIsland;

    public IndexIsland(int idxIsland) {
        this.idxIsland = idxIsland;
    }

    public int getIdxIsland() {
        return idxIsland;
    }
}

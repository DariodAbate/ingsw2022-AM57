package it.polimi.ingsw.model.expertGame;

import java.io.Serializable;

public class BannedIslandCard extends ExpertCard implements Serializable {
    private final BannedIsland game;
    private int islandIndex;

    public BannedIslandCard (BannedIsland game) {
        super(2);
        this.game = game;
    }

    public void setIslandIndex(int islandIndex) {
        this.islandIndex = islandIndex;
    }

    public int getIslandIndex() {
        return this.islandIndex;
    }

    @Override
    public void effect() {
        if (!isPlayed()) {
            played = true;
            price += 1 ;
        }
        game.banIsland(islandIndex);
    }
}
